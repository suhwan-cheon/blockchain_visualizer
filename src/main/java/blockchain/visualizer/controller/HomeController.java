package blockchain.visualizer.controller;

import blockchain.visualizer.hash.SHA256;
import blockchain.visualizer.model.*;
import blockchain.visualizer.transaction.Transaction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

@Controller
public class HomeController {

    SHA256 sha256 = new SHA256();
    String hashvalue = "";

    ArrayList<Block> blockChain = new ArrayList<Block>();
    String createFlag = "";

    Wallet wallet; // 사용자의 wallet
    ArrayList<Wallet> testWallets = new ArrayList<>(); // TX 보낼 때 생성되는 임의의 Wallet들 저장 (추후 verift logic 짜게 될 때를 위해서)

    @GetMapping("/")
    public String hash(Model model, HashDto hashDto){
        model.addAttribute("hashDto", hashDto);
        model.addAttribute("hashvalue", hashvalue);
        return "home";
    }

    @PostMapping("/hash")
    public String createBbs(@ModelAttribute("hashDto") HashDto hashDto) throws NoSuchAlgorithmException {
        hashvalue = sha256.encrypt(hashDto.getContent());
        System.out.println(hashvalue);
        return "redirect:/";
    }

    @GetMapping("/block/create")
    public String createBlock(Model model, BlockDto blockDto){
        model.addAttribute("blockDto", blockDto);
        model.addAttribute("createFlag", createFlag);
        createFlag = "";
        return "block-create";
    }

    @PostMapping("/block/create")
    public String addBlock(@ModelAttribute("blockDto") BlockDto blockDto) throws NoSuchAlgorithmException {

        System.out.println(blockDto.getTransaction());

        String[] transactions = blockDto.getTransaction().split("\\n");

        Block block = new Block(transactions, /// 가장 마지막 블록의 주소 참조
                (blockChain.size() == 0) ? 0 : blockChain.get(blockChain.size() - 1).getBlockHash(), LocalDate.now(), 1, 1, sha256.merkle(transactions), 1);

        blockChain.add(block);
        createFlag = "생성 완료";
        return "redirect:/block/create";
    }

    @GetMapping("/block/view")
    public String viewBlock(Model model){
        model.addAttribute("blockChain", blockChain);
        return "block-view";
    }

    @GetMapping("/wallet")
    public String getWallet(Model model){
        model.addAttribute("wallet", new Wallet());
        return "wallet";
    }

    @PostMapping("/wallet")
    public String viewWallet(Wallet wallet, Model model) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        
        this.wallet = wallet;
        
        model.addAttribute("owner", wallet.getOwner());
        model.addAttribute("publicKey", wallet.publicKey.getEncoded());
        model.addAttribute("privateKey", wallet.privateKey);

        return "wallet";
    }
    
    /*@GetMapping("/wallet/utxo")
    public String getUtxo(Model model){
        model.addAttribute("utxos", wallet.getUtxos());
        model.addAttribute("utxo", new Utxo());
        return "utxo-view";
    }
    
    @PostMapping("/wallet/utxo")
    public String makeUtxo(Utxo utxo){
        *//*utxo.setIdx(wallet.getUtxos().size() + 1); // idx 계산해서 변경
        wallet.getUtxos().add(utxo);
        wallet.setBalance(wallet.getBalance() + utxo.getPrice()); // balance 변경*//*
        
        return "redirect:/wallet/utxo";
    }*/
    
    @GetMapping("/transaction")
    public String getTx(Model model){
        model.addAttribute("tx", new TxDto());
        return "transaction";
    }
    
    @PostMapping("/transaction")
    public String makeTx(TxDto tx){
        // TX 생성하기
        Wallet to = new Wallet();
        boolean check = false;
        for(Wallet wl : testWallets) { // 이미 지갑이 생성된 적이 있는 사람한테 TX 보내는 것이면 찾아서 사용
            if(wl.getOwner().equals(tx.getReceiver())) {
                to = wl;
                check = true;
                break;
            }
        }
        if(!check) { // 새롭게 생성된 지갑
            to.setOwner(tx.getReceiver());
            testWallets.add(to);
        }
        
        Transaction transaction = new Transaction(wallet.getPublicKey(), to.getPublicKey(), tx.getValue());
        transaction.generateSignature(wallet.privateKey); // 서명
        
        transaction.setIdx(wallet.getTxs().size() + 1); // TX idx 붙여주기 (출력용)
        
        wallet.getTxs().add(transaction);
        
        return "redirect:/transaction";
    }
    
    @GetMapping("/transaction/view")
    public String getTxView(Model model){
        model.addAttribute("txs", wallet.getTxs());
        return "transaction-view";
    }
    
    
    @GetMapping("/peer")
    public String getPeer(){
        return "peer";
    }
    
    @GetMapping("/miner")
    public String getMiner(){
        return "miner";
    }
    
    @GetMapping("/network")
    public String getNetwork(){
        return "network";
    }
}
