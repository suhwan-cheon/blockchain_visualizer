package blockchain.visualizer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Utxo {
	
	private int idx;
	private String sender;
	private String receiver;
	private double price;
	
}
