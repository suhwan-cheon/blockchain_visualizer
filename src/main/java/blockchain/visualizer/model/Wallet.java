package blockchain.visualizer.model;

import blockchain.visualizer.transaction.Transaction;
import lombok.Getter;
import lombok.Setter;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;

@Getter @Setter
public class Wallet {

    public PrivateKey privateKey;
    public PublicKey publicKey;

    public String owner; // 지갑 주인 이름
    public double balance; // 잔액
    public ArrayList<Transaction> txs; // utxo 배열

    public Wallet() {
        // utxos init
        txs = new ArrayList<>();
        generateKeyPair();
    }
    
    public void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            // Initialize the key generator and generate a KeyPair
            keyGen.initialize(ecSpec, random); //256
            KeyPair keyPair = keyGen.generateKeyPair();
            // Set the public and private keys from the keyPair
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();

        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}