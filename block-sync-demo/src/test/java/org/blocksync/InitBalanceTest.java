package org.blocksync;

import java.math.BigInteger;
import java.util.List;
import java.util.Random;
import org.blocksync.entity.Node;
import org.blocksync.entity.Pair;
import org.junit.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.http.HttpService;

/**
 * @author zacconding
 * @Date 2018-05-30
 * @GitHub : https://github.com/zacscoding
 */
public class InitBalanceTest {

    @Test
    public void initBalance() throws Exception {
        String url = "http://192.168.5.50:8540/";
        HttpService httpService = new HttpService(url);
        Web3j web3j = Web3j.build(httpService);
        Admin admin = Admin.build(httpService);

        List<String> accounts = web3j.ethAccounts().send().getAccounts();
        BigInteger value = BigInteger.valueOf(100000);
        String from = "0x0002cc6a7cec1276e76a76385ad78a76e619dc49";
        for(String account : accounts) {
            BigInteger balance = web3j.ethGetBalance(account, DefaultBlockParameterName.LATEST).send().getBalance();
            if (balance.equals(BigInteger.ZERO)) {
                try {
                    String to = account;
                    Transaction tx = new Transaction(
                        from,
                        null,
                        BigInteger.ZERO,
                        null,
                        to,
                        value,
                        null
                    );
                    String hash = admin.personalSendTransaction(tx,"user0").send().getTransactionHash();
                    System.out.println("#Send tx from : " + from + ", to : " + to + ", value : " + value + " ==> hash : " + hash);
                    Thread.sleep(new Random().nextInt(100));
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
