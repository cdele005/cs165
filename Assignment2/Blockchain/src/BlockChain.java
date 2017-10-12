import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.plaf.basic.BasicBorders.ToggleButtonBorder;

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
   private HashMap<byte[], BlockNode> H;    
   private BlockNode maxHeightBlock;    
   private TransactionPool txPool;
   private int maxHeight;

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
       
       H = new HashMap<byte[], BlockNode>();   
       H.put(genesisBlock.getHash(), genesis);
       
       maxHeightBlock = genesis;  
       genesis.height = 1;
       maxHeight = 1;
       
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
	   
	   // reject if block claims to be genesis
	   if (prev_hash == null)
		   return false;
	   
	   // reject if prev block hash hasn't been seen yet	
	   if(!H.containsKey(prev_hash)) 
		   return false;
	   
	   // get parent node
	   BlockNode parent = H.get(prev_hash);
	   int height = parent.height + 1;
	   
	   // reject if blockchain is full
	   if(height <= (maxHeight - CUT_OFF_AGE)) 
		   return false;
	   
	   // check UTXOs of this branch; remove transactions from pool
	   TxHandler handler = new TxHandler(parent.getUTXOPoolCopy());
	   for(Transaction t : b.getTransactions()) {
		   if(handler.isValidTx(t)) {
			   txPool.removeTransaction(t.getHash());
		   } else { return false; }		   
	   }
	   
	   // construct the new node
	   BlockNode newNode = new BlockNode(b, parent, parent.getUTXOPoolCopy());
	   
	   // add new node to the lists
	   heads.add(newNode);
	   H.put(b.getHash(), newNode);
	   newNode.height = height;
	   
	   // set max heights
	   if(height > maxHeight) {
		   maxHeight = height;
		   maxHeightBlock = newNode;
	   }
	   
	   return true;
   }

   /* Add a transaction in transaction pool
    */
   public void addTransaction(Transaction tx) {
      // IMPLEMENT THIS
	   TxHandler handler = new TxHandler(getMaxHeightUTXOPool());
	   if(handler.isValidTx(tx))
		   txPool.addTransaction(tx);
   }
}