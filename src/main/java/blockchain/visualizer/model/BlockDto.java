package blockchain.visualizer.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlockDto {
    private String transaction;
    private int nonce;
}
