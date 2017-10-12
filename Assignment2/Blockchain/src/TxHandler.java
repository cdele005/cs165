import java.util.ArrayList;

public class TxHandler {

	private UTXOPool curr_pool;
	/* Creates a public ledger whose current UTXOPool (collection of unspent 
	 * transaction outputs) is utxoPool. This should make a defensive copy of 
	 * utxoPool by using the UTXOPool(UTXOPool uPool) constructor.
	 */
	public TxHandler(UTXOPool utxoPool) {
		// IMPLEMENT THIS
		curr_pool = new UTXOPool(utxoPool);
	}

	/* Returns true if 
	 * (1) all outputs claimed by tx are in the current UTXO pool, 
	 * (2) the signatures on each input of tx are valid, 
	 * (3) no UTXO is claimed multiple times by tx, 
	 * (4) all of tx's output values are non-negative, and
	 * (5) the sum of tx's input values is greater than or equal to the sum of   
	        its output values;
	   and false otherwise.
	 */

	public boolean isValidTx(Transaction tx) {
		// IMPLEMENT THIS
		double sum_input = 0;
		double sum_output = 0;
		
		for (Transaction.Output o : tx.getOutputs()){
			// checks if output values are non-negative
			if (o.value < 0)
			{return false;}
			// calculates sum of output values
			else
			{sum_output += o.value;}
		}
		
		// stores all UTXO's that have been found to check for multiple claims
		UTXOPool found = new UTXOPool();
		
		// checks if outputs in tx are in current pool
		for (int i = 0; i < tx.numInputs(); i++){
			Transaction.Input input = tx.getInput(i);
			UTXO to_check = new UTXO(input.prevTxHash, input.outputIndex);
			Transaction.Output prev_out = curr_pool.getTxOutput(to_check);
			
			// checks multiple claims using UTXOPool found
			if (found.contains(to_check))
			{return false;}
			else 
			{found.addUTXO(to_check, prev_out);}
			
			// checks if input is in current pool
			if (!curr_pool.contains(to_check))
			{return false;}
		}
		
		for (int i = 0; i < tx.numInputs(); i++){
			Transaction.Input input = tx.getInput(i);
			UTXO to_check = new UTXO(input.prevTxHash, input.outputIndex);
			Transaction.Output prev_out = curr_pool.getTxOutput(to_check);
			
			// signature check
			RSAKey address_check = prev_out.address;
			byte [] raw_data = tx.getRawDataToSign(i);
			if (!address_check.verifySignature(raw_data, input.signature))
			{return false;}
		}
		
		// calculates sum of inputs
		for (Transaction.Input i : tx.getInputs()){
			UTXO key = new UTXO(i.prevTxHash,i.outputIndex);
			Transaction.Output prev_out = curr_pool.getTxOutput(key);
			sum_input += prev_out.value;
		}
		
		// checks if sum_input >= sum_output
		if (sum_input < sum_output)
	    {return false;}
		
		return true;
	}

	/* Handles each epoch by receiving an unordered array of proposed 
	 * transactions, checking each transaction for correctness, 
	 * returning a mutually valid array of accepted transactions, 
	 * and updating the current UTXO pool as appropriate.
	 */
	public Transaction[] handleTxs(Transaction[] possibleTxs) {
		// IMPLEMENT THIS
		ArrayList <Transaction> arr = new ArrayList<Transaction>();
		
		for (int i = 0; i < possibleTxs.length; i++){
			Transaction curr_tx = new Transaction(possibleTxs[i]);
			
			if (isValidTx(curr_tx)){
				arr.add(curr_tx);
				
				for (Transaction.Input input : curr_tx.getInputs()){
					UTXO to_remove = new UTXO(input.prevTxHash, input.outputIndex);
					curr_pool.removeUTXO(to_remove);
				}
				
				for (int index = 0; index < curr_tx.numOutputs(); index++){
					UTXO to_add = new UTXO(curr_tx.getHash(), index);
					curr_pool.addUTXO(to_add, curr_tx.getOutput(index));
				}
			}
		}
		
		Transaction [] ret_arr = new Transaction[arr.size()];
		for (int i = 0; i < arr.size(); i++){
			ret_arr[i] = arr.get(i);
		}
				
		return ret_arr;
	}
	
	/* Returns the current UTXO pool.If no outstanding UTXOs, returns an empty (non-null) UTXOPool object. */
	public UTXOPool getUTXOPool() {
		// IMPLEMENT THIS
		if (curr_pool != null)
		{return curr_pool;}
		
		else
		{return new UTXOPool();}
	}
	
} 