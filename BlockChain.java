import java.util.ArrayList;
import java.util.HashMap;

/* Block Chain should maintain only limited block nodes to satisfy the functions
   You should not have the all the blocks added to the block chain in memory 
   as it would overflow memory
 */

public class BlockChain {
   public static final int CUT_OFF_AGE = 10;

   // all information required in handling a block in block chain
   private class BlockNode {
      public Block b;
      public BlockNode parent;
      public ArrayList<BlockNode> children;
      public int height;
      // utxo pool for making a new block on top of this block
      private UTXOPool uPool;

      public BlockNode(Block b, BlockNode parent, UTXOPool uPool) {
         this.b = b;
         this.parent = parent;
         children = new ArrayList<BlockNode>();
         this.uPool = uPool;
         if (parent != null) {
            height = parent.height + 1;
            parent.children.add(this);
         } else {
            height = 1;
         }
      }

      public UTXOPool getUTXOPoolCopy() {
         return new UTXOPool(uPool);
      }
   }
   
   private ArrayList<BlockNode> heads;  
   private HashMap<ByteArrayWrapper, BlockNode> H;    
   private int height;   
   private BlockNode maxHeightBlock;    
   private TransactionPool txPool;

   /* create an empty block chain with just a genesis block.   
   * Assume genesis block is a valid block    */   
   public BlockChain(Block genesisBlock) {      
       UTXOPool uPool = new UTXOPool();     
       Transaction coinbase = genesisBlock.getCoinbase();     
       UTXO utxoCoinbase = new UTXO(coinbase.getHash(), 0);     
       uPool.addUTXO(utxoCoinbase, coinbase.getOutput(0));     
       BlockNode genesis = new BlockNode(genesisBlock, null, uPool);     
       heads = new ArrayList<BlockNode>();     
       heads.add(genesis);     
       H = new HashMap<ByteArrayWrapper, BlockNode>();     
       H.put(new ByteArrayWrapper(genesisBlock.getHash()), genesis);     
       height = 1;     
       maxHeightBlock = genesis;     
       txPool = new TransactionPool();   
   }
   
   public Block getMaxHeightBlock() {
      // IMPLEMENT THIS
	   return maxHeightBlock.b;
   }
   
   /* Get the UTXOPool for mining a new block on top of 
    * max height block
    */
   public UTXOPool getMaxHeightUTXOPool() {
      // IMPLEMENT THIS
	   return maxHeightBlock.uPool;
   }
   
   /* Get the transaction pool to mine a new block
    */
   public TransactionPool getTransactionPool() {
      // IMPLEMENT THIS
	   return txPool;
   }

   /* Add a block to block chain if it is valid.
    * For validity, all transactions should be valid
    * and block should be at height > (maxHeight - CUT_OFF_AGE).
    * For example, you can try creating a new block over genesis block 
    * (block height 2) if blockChain height is <= CUT_OFF_AGE + 1. 
    * As soon as height > CUT_OFF_AGE + 1, you cannot create a new block at height 2.
    * Return true of block is successfully added
    */
   public boolean addBlock(Block b) {
       // IMPLEMENT THIS
	   byte[] prev_hash = b.getPrevBlockHash();
	   if (prev_hash == null)
	   {return false;}
	   
	   BlockNode parent_node = maxHeightBlock;
	   UTXOPool pool = parent_node.getUTXOPoolCopy();
	   TxHandler handler = new TxHandler(pool);
	   for (Transaction t : b.getTransactions()){
		   if (!handler.isValidTx(t))
		   {return false;}
	   }
	   
	   BlockNode to_add = new BlockNode(b, parent_node, pool);
	   heads.add(to_add);
	   
	   if (parent_node.children.size() == 0)
	   {height++;}
   
	   return true;
   }

   /* Add a transaction in transaction pool
    */
   public void addTransaction(Transaction tx) {
      // IMPLEMENT THIS
	   txPool.addTransaction(tx);
   }
}