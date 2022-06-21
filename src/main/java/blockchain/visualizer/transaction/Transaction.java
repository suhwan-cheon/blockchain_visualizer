package blockchain.visualizer.transaction;

import blockchain.visualizer.hash.StringUtil;
import lombok.Getter;
import lombok.Setter;

import java.security.*;
import java.util.ArrayList;

@Getter @Setter
public class Transaction implements Comparable<Transaction>{

    public int idx;
    public String transactionId; //Contains a hash of transaction*
    public PublicKey sender; //Senders address/public key.
    public PublicKey receiver; //Recipients address/public key.
    public double value; //Contains the amount we wish to send to the recipient.
    public double fee; // 수수료
    public byte[] signature; //This is to prevent anybody else from spending funds in our wallet.

    // public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    private static int sequence = 0; //A rough count of how many transactions have been generated
    
    // Constructor:
    public Transaction(PublicKey sender, PublicKey receiver, double value, double fee) {
        this.sender = sender;
        this.receiver = receiver;
        this.value = value;
        this.fee = fee;
        this.transactionId = StringUtil.applySha256(
                StringUtil.getStringFromKey(sender) +
                        StringUtil.getStringFromKey(receiver) +
                        sequence++
        );
    }
    
    /**
     * sender + receiver + value 를 private key로 서명함
     * @param privateKey
     */
    public void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(receiver) + Double.toString(value)	;
        signature = StringUtil.applyECDSASig(privateKey,data);
    }

    public boolean verifySignature() {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(receiver) + Double.toString(value)	;
        return StringUtil.verifyECDSASig(sender, data, signature);
    }

    private String calculateHash() {
        sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
        return StringUtil.applySha256(
                StringUtil.getStringFromKey(sender) +
                        StringUtil.getStringFromKey(receiver) +
                        Double.toString(value) + sequence
        );
    }
    
    @Override
    public int compareTo(Transaction tx) {
        if(this.fee < tx.fee) return 1;
        else return -1;
    }
}