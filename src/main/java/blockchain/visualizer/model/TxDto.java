package blockchain.visualizer.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TxDto {
	
	private String receiver;
	private double value;
	private double fee;
}
