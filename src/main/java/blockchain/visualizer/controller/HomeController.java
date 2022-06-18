package blockchain.visualizer.controller;

import blockchain.visualizer.hash.SHA256;
import blockchain.visualizer.model.Block;
import blockchain.visualizer.model.BlockDto;
import blockchain.visualizer.model.HashDto;
import blockchain.visualizer.model.Wallet;
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
    public String getWallet(){

        return "wallet";
    }

    @PostMapping("/wallet")
    public String viewWallet(Model model) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        Wallet walletA = new Wallet();
//        Wallet walletB = new Wallet();
//
//        Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
//        transaction.generateSignature(walletA.privateKey);
//
//        System.out.println(transaction.verifySignature());
        model.addAttribute("publicKey", walletA.publicKey.getEncoded());
        model.addAttribute("privateKey", walletA.privateKey);

        return "wallet";
    }
}
