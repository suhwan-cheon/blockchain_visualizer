package blockchain.visualizer.controller;

import blockchain.visualizer.hash.SHA256;
import blockchain.visualizer.model.*;
import blockchain.visualizer.transaction.Transaction;
import blockchain.visualizer.transaction.TransactionOutput;
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
import java.util.Collections;
import java.util.List;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    SHA256 sha256 = new SHA256();
    String hashvalue = "";

    String createFlag = "";
    
    // Variable
    public Wallet wallet; // 사용자의 wallet
    public ArrayList<Wallet> testWallets = new ArrayList<>(); // TX 보낼 때 생성되는 임의의 Wallet들 저장 (추후 verift logic 짜게 될 때를 위해서)
    public ArrayList<Transaction> allTx = new ArrayList<>(); // Miner 노드를 위해 모든 TX 가지고 있음
    public ArrayList<Block> blockChain = new ArrayList<>(); // Block들
    public Block block;
    public Block blockt;
    public int count;
    public String hash, valHash;
    public boolean check = false, flag = false;
    public boolean flag1 = false, flag2 = false;
    
    public HomeController() {
        allTx.add(new Transaction(new Wallet().getPublicKey(), new Wallet().getPublicKey(), 12.5, 0.01));
        allTx.add(new Transaction(new Wallet().getPublicKey(), new Wallet().getPublicKey(), 7.7, 0.03));
        allTx.add(new Transaction(new Wallet().getPublicKey(), new Wallet().getPublicKey(), 25.9, 0.1));


    }
    
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
    public String createBlock(Model model){
        model.addAttribute("block", blockt);
        model.addAttribute("flag1", flag1);
        model.addAttribute("flag2", flag2);
        model.addAttribute("createFlag", createFlag);
        createFlag = "";
        return "block-create";
    }


    @PostMapping("/block/create")
    public String addBlock() {
        if(flag1 && flag2) {
            createFlag = "생성 완료";
            blockt = block;
        }

        return "redirect:/block/create";
    }

    @GetMapping("/merkleTree")
    public String merkelTree(Model model) {
        List<List<String>> lists = SHA256.getMerkleRoot(allTx);
        String merkleRoot = "";
        if(lists.size() == 0) merkleRoot = "";
        else merkleRoot = lists.get(lists.size() - 1).get(0);

        model.addAttribute("merkleRoot", lists);
        // block에 값 저장
        String[] array = new String[allTx.size()];
        int size = 0;
        for(Transaction tx : allTx){
            array[size++] = tx.toString();
        }
        String prevBlockHeaderHash = blockChain.size() == 0 ? "0" : SHA256.applySha256(blockChain.get(blockChain.size() - 1).getHeader());
        block = new Block(array, prevBlockHeaderHash, LocalDate.now(), 0, 1, merkleRoot, 1);
        blockChain.add(block);
        flag1 = true;
        return "merkle-tree";
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
    public String makeTx(Model model, TxDto tx, RedirectAttributes redirectAttributes){

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
        
        if(wallet == null) System.out.println("??????");


        ArrayList<Transaction> txs = wallet.getTxs();
        double utxoSum = 0;
        for (Transaction transaction : txs) {
            for (TransactionOutput output : transaction.getOutputs()) {
                utxoSum += output.value;
            }
        }

        if (tx.getValue() > utxoSum) {
            redirectAttributes.addFlashAttribute("message", "transaction 생성 오류 : not enough balance");
            return "redirect:/transaction";
        }

        Transaction transaction = new Transaction(wallet.getPublicKey(), to.getPublicKey(), tx.getValue(), tx.getFee());
        Transaction transaction2 = new Transaction(wallet.getPublicKey(), wallet.getPublicKey(), utxoSum - tx.getValue(), 0);
        transaction.generateSignature(wallet.privateKey); // 서명

        transaction.setIdx(wallet.getTxs().size() + 1); // TX idx 붙여주기 (출력용)
        allTx.add(transaction);
        allTx.add(transaction2);

        wallet.getTxs().add(transaction);

        wallet.getTxs().add(transaction2);

        redirectAttributes.addFlashAttribute("message", "transaction 생성 완료");

        return "redirect:/transaction";
    }
    
    @GetMapping("/transaction/view")
    public String getTxView(Model model){
        model.addAttribute("txs", wallet.getTxs());
        return "transaction-view";
    }
    
    @GetMapping("/miner/transaction")
    public String getMinerTx(Model model){
        // allTx order by Fee
        Collections.sort(allTx);
        model.addAttribute("allTx", allTx);
        return "miner-transaction";
    }
    
    @GetMapping("/nonce")
    public String getNonce(Model model){
        if(!check) findNonce(); // 영상 시연용 nonce 값 미리 찾기
        model.addAttribute("block", block);
        model.addAttribute("blockDto", new BlockDto());
        model.addAttribute("count", count);
        model.addAttribute("hash", hash);

        return "nonce";
    }
    
    @PostMapping("/nonce")
    public String checkNonce(BlockDto blockDto, Model model){
        // block 헤더 값을 가져온다.
        // 이때 더하는 값들은 "previousBlockHash + timeStamp + nonce + version + merkleRoot + bits" 이다.
        block.setNonce(blockDto.getNonce());
        String header = block.getHeader();
        // 헤더 값을 Hash 한다.
        hash = SHA256.applySha256(header);
        System.out.println(hash);
        // hash 값에서 앞에서 1번째 자리가 0인 nonce라면 마이닝 성공!
        count = 0;
        for(int i=0; i<hash.length(); i++){
            char c = hash.charAt(i);
            if(c == '0') count++;
            else break;
        }
        flag2 = true;
        return "redirect:/nonce";
    }
    
    @GetMapping("/decode")
    public String decoding(Model model){ // nonce 값이 맞는지 검증
        model.addAttribute("nonce", block.getNonce());
        model.addAttribute("blockDto", new BlockDto());
        model.addAttribute("validatorHash", valHash);
        model.addAttribute("minerHash", block.getBlockHash());
        model.addAttribute("flag", flag);
        return "decode";
    }
    
    @PostMapping("/decode")
    public String getNonceForDecode(BlockDto blockDto){
        Block temp = new Block(block.getTransactions(), block.getPreviousBlockHash(), block.getTimeStamp(), blockDto.getNonce(), 1, block.getMerkleRoot(), 1);
        valHash = SHA256.applySha256(temp.getHeader());
        if(valHash.equals(block.getBlockHash())) flag = true; // 검증 성공
        else flag = false; // 검증 실패
        return "redirect:/decode";
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
    
    public void findNonce(){ // 영상 시연을 위해 nonce값 미리 찾아놓는 함수
        for(int i=1; i<100000000; i++){
            block.setNonce(i);
            String temp = SHA256.applySha256(block.getHeader());
            if(temp.charAt(0) == '0') {
                System.out.println("nonce 값은 : " + i);
                block.setBlockHash(temp);
                check = true;
                break;
            }
        }
    }
}
