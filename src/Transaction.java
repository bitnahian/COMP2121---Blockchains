public class Transaction {
	private String sender;
	private String content;

	// getters and setters
	public void setSender(String sender) {
		this.sender = sender;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSender() {
		return sender;
	}

	public String getContent() {
		return content;
	}

	public String toString() {
		return String.format("|%s|%70s|\n", sender, content);
	}
	
	// implement helper functions here if you need any
}
