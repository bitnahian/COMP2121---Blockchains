import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;

public class Block {

    private Block previousBlock;
    private byte[] previousHash;
    private ArrayList<Transaction> transactions;

    public Block() {
        transactions = new ArrayList<>();
    }

    public byte[] getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(byte[] prevHash) {
        this.previousHash = prevHash;
    }

    public Block synchronized getPreviousBlock() {
        return previousBlock;
    }

    public void synchronized setPreviousBlock(Block previousBlock) {
        this.previousBlock = previousBlock;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public void synchronized setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void synchronized addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

    public byte[] calculateHash() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            ByteArrayOutputStream baout = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baout);
            out.write(previousHash);
            for (Transaction tx : transactions) {
                out.writeUTF("tx|" + tx.getSender() + "|" + tx.getContent());
            }
            byte[] bytes = baout.toByteArray();
            return digest.digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public byte[] calculateHashWithNonce(int nonce) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            ByteArrayOutputStream baout = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baout);
            out.write(nonce);
            out.write(previousHash);
            for (Transaction tx : transactions) {
                out.writeUTF("tx|" + tx.getSender() + "|" + tx.getContent());
            }
            byte[] bytes = baout.toByteArray();
            return digest.digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public String toString() {
        String cutOffRule = new String(new char[81]).replace("\0", "-") + "\n";
        String prevHashString = String.format("|PreviousHash:|%65s|\n", Base64.getEncoder().encodeToString(previousHash));
        String hashString = String.format("|CurrentHash:|%66s|\n", Base64.getEncoder().encodeToString(calculateHash()));
        String transactionsString = "";
        for (Transaction tx : transactions) {
            transactionsString += tx.toString();
        }
        return "Block:\n"
                + cutOffRule
                + hashString
                + cutOffRule
                + transactionsString
                + cutOffRule
                + prevHashString
                + cutOffRule;
    }
}