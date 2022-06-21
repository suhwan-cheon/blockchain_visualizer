package blockchain.visualizer.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Arrays;

@Getter
@Setter
public class Block {

    private String[] transactions;
    private int blockHash;
    private String previousBlockHash;
    private LocalDate timeStamp;
    private int nonce;
    private int version;
    private String merkleRoot;
    private int bits;

    public Block(String[] transactions, String previousBlockHash, LocalDate timeStamp, int nonce, int version, String merkleRoot, int bits) {
        this.transactions = transactions;
        // this.blockHash = Arrays.hashCode(new int[] {Arrays.hashCode(transactions), this.previousBlockHash});
        this.previousBlockHash = previousBlockHash;
        this.timeStamp = timeStamp;
        this.nonce = nonce;
        this.version = version;
        this.merkleRoot = merkleRoot;
        this.bits = bits;
    }

    @Override
    public String toString() {
        return "Block{" +
                "transactions=" + Arrays.toString(transactions) +
                ", blockHash=" + blockHash +
                ", previousBlockHash=" + previousBlockHash +
                ", timeStamp=" + timeStamp +
                ", nonce=" + nonce +
                ", version=" + version +
                ", merkleRoot=" + merkleRoot +
                ", bits=" + bits +
                '}';
    }
    
    public String getHeader(){
        return previousBlockHash + timeStamp + nonce + version + merkleRoot + bits;
    }
}
