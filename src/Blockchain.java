import java.util.ArrayList;
import java.util.regex.Pattern;

public class Blockchain {

	private Block head;
	private ArrayList<Transaction> pool;
	private int length;

	private final int poolLimit = 3;

	public Blockchain() {
		pool = new ArrayList<>();
		length = 0;
	}

	// getters and setters	
	public Block getHead() {
		return head;
	}

	public ArrayList<Transaction> getPool() {
		return pool;
	}

	public int getLength() {
		return length;
	}

	public void setHead(Block head) {
		this.head = head;
	}

	public void setPool(ArrayList<Transaction> pool) {
		this.pool = pool;
	}

	public void setLength(int length) {
		this.length = length;
	}

	// add a transaction
	public int addTransaction(String txString) {
		
		String parts[] = txString.split("\\|");
		// Check validity
		if(parts.length != 3)
			return 0;
		else if( !parts[0].equals("tx") || !Pattern.matches("[a-z]{4}[0-9]{4}", parts[1]) || parts[2].length() > 70 || parts[2].contains("\\|"))
			return 0;
		// Create the new transaction
		Transaction tx = new Transaction();
		
		tx.setSender(parts[1]);
		tx.setContent(parts[2]);
		// Add to the pool
		getPool().add(tx);
		
		if(getPool().size() == poolLimit)
		{
			addBlock();
			return 2;
		}
		
		return 1;
		// implement you code here.
	}

	public String toString() {
		String cutOffRule = new String(new char[81]).replace("\0", "-") + "\n";
		String poolString = "";
		for (Transaction tx : pool) {
			poolString += tx.toString();
		}
		
		String blockString = "";
		Block bl = head;
		while (bl != null) {
			blockString += bl.toString();
			bl = bl.getPreviousBlock();
		}

		return "Pool:\n" + cutOffRule + poolString + cutOffRule + blockString;
	}

	public void addBlock()
	{
		Block block = new Block();
		// Create link
		if(getLength() == 0)
		{
			// Set Previous Hash to 32 bytes of 0
			block.setPreviousHash(new byte[32]);
		}
		else 
		{
			block.setPreviousBlock(getHead());
			block.setPreviousHash(getHead().calculateHash());
		}
		block.setTransactions(getPool());
		
		// Reset pool to empty array list
		setPool(new ArrayList<Transaction>());
		// Set head to block
		setHead(block);
		setLength(getLength()+ 1);
		
	}
	// implement helper functions here if you need any.
}
