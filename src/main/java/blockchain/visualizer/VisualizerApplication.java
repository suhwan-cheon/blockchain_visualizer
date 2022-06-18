package blockchain.visualizer;

import blockchain.visualizer.transaction.Transaction;
import blockchain.visualizer.model.Wallet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.Security;

@SpringBootApplication
public class VisualizerApplication {

	public static void main(String[] args) {

		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		Wallet walletA = new Wallet();
        Wallet walletB = new Wallet();

        Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
        transaction.generateSignature(walletA.privateKey);

        System.out.println(walletA.privateKey);
        System.out.println(walletA.publicKey);

		SpringApplication.run(VisualizerApplication.class, args);
	}

}
