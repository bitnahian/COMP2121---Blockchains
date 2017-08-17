import static org.junit.Assert.*;

import org.junit.Test;

public class BlockchainTest {

	private Blockchain b;
	public void setup() {
		b = new Blockchain();		
	}

	@Test
	public void testSample() {
		setup();
		String message;
		for (int i = 1; i < 8; i++) {
			message = "tx|test" + i + i + i + i + "|"+ i;
			b.addTransaction(message);
		}
		Block mirror = b.getHead();
		while (mirror != null) {
			System.out.println(mirror.toString());
			mirror = mirror.getPreviousBlock();
		}
		
		System.out.println(b.toString());
	}
}
