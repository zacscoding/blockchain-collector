# Blockchain Collector  
; Listen blockchain events & Produce kafka messages  

> ## Blockchain Events Listeners  

- #### Ethereum  
  - block event
  - pending transaction event
  - transaction receipt event  

---  

> ## Getting started

```aidl
$ git clone https://github.com/zacscoding/blockchain-collector.git
```  

### 1. maven build & run  

```aidl
$ mvn clean install  
$ java -jar target/blockchain-collector --spring.config.location=/home/app/application.yml
```  

### 2. docker-compose

```aidl  
$ cd blockchain-collector/docker
$ docker-compose -f docker-compose.yml up -d
$ tail -f ../logs/collector.log
```  

---  

> ## TODO  

- [x] ethereum subscribe(observer)  
- [x] produce kafka message
- [x] docker compose + update getting started
- [ ] blockchain data save (elasticsearch or mongodb or etc)   
- [ ] pending transaction manager
- [ ] clustering (zookeeper) will produce unique event message if register multiple noded    
- [ ] multiple block chain (bitcoin, qtum, etc...)  

---  

> ## Examples  

#### Block event  

```aidl
2018-12-21 02:21:04.210 INFO [message] ## [Consumer] receive block message.
{
  "metadata": {
    "networkName": "Private",
    "nodeName": "Node01"
  },
  "block": {
    "number": 105,
    "hash": "0x21548fd82a924d6004a043713cd1d5ccf6d76a7d94d4abdd4712385c62c0d49b",
    "parentHash": "0x0180857604e6a51f7125450778a39130df5c40826a4690bc86fbf206a9b24d67",
    "sha3Uncles": "0x1dcc4de8dec75d7aab85b567b6ccd41ad312451b948a7413f0a142fd40d49347",
    "logsBloom": "0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "transactionsRoot": "0x0ab5ad3c6a74c5eb9bde566506e789818c3d87e10d4669d675a8bb3b00321d7d",
    "stateRoot": "0xaf113e98ed57ad3e116f51aa2c966d946ae84eec55d3c473f167f42fff98b8aa",
    "miner": "0x00bd138abd70e2f00903268f3db08f2d25677c9e",
    "difficulty": "0xfffffffffffffffffffffffffffffffd",
    "totalDifficulty": "0x68fffffffffffffffffffffffff9de02d3",
    "extraData": "0xd583010a028650617269747986312e32352e30826c69",
    "size": "0x2ab",
    "gasLimit": "0x52a0e0",
    "gasUsed": "0x5208",
    "timestamp": "0x5c1bcf7c",
    "sealFields": [
      "0x840623fcc4",
      "0xb8414436d2d4280287eafa9c2af93affc7197789ac82219dea910fd5d665c46e750e150c8d65599cda6f1adbb3d7a8600d8fea5e8f29f4d55b61b32d40fe1d0a9ccb00"
    ],
    "uncles": [],
    "transactions": [
      "0x3bb58c41a62992a246b9aef9d17270a67588dea87c6b39b12a2f772eecb80907"
    ]
  }
}
```  

#### Pending tx message  

```aidl
2018-12-21 02:20:54.245 INFO [message] ## [Consumer] receive pending message.
{
  "metadata": {
    "networkName": "Private",
    "nodeName": "Node01"
  },
  "pendingTransaction": {
    "hash": "0x3bb58c41a62992a246b9aef9d17270a67588dea87c6b39b12a2f772eecb80907",
    "nonce": "0x2",
    "from": "0x00d695cd9b0ff4edc8ce55b493aec495b597e235",
    "to": "0x001ca0bb54fcc1d736ccd820f14316dedaafd772",
    "value": "0x3b9aca00",
    "gas": "0xe57e0",
    "gasPrice": "0x0",
    "input": "0x"
  }
}
```  

#### Transaction message  - transfer

```aidl
2018-12-21 02:21:04.215 INFO [message] ## [Consumer] receive tx message.
{
  "metadata": {
    "networkName": "Private",
    "nodeName": "Node01"
  },
  "transaction": {
    "hash": "0x3bb58c41a62992a246b9aef9d17270a67588dea87c6b39b12a2f772eecb80907",
    "nonce": "0x2",
    "blockHash": "0x21548fd82a924d6004a043713cd1d5ccf6d76a7d94d4abdd4712385c62c0d49b",
    "blockNumber": "0x69",
    "transactionIndex": "0x0",
    "from": "0x00d695cd9b0ff4edc8ce55b493aec495b597e235",
    "to": "0x001ca0bb54fcc1d736ccd820f14316dedaafd772",
    "value": "0x3b9aca00",
    "gas": "0xe57e0",
    "gasPrice": "0x0",
    "input": "0x",
    "contractAddress": null,
    "cumulativeGasUsed": "0x5208",
    "gasUsed": "0x5208",
    "logsBloom": "0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "status": "0x1",
    "logs": []
  }
}
```  

#### Transaction message  - call smart contract  

```
{
  "metadata": {
    "networkName": "Private",
    "nodeName": "ParityNode01"
  },
  "transaction": {
    "hash": "0xc6ef2fc5426d6ad6fd9e2a26abeab0aa2411b7ab17f30a99d3cb96aed1d1055b",
    "nonce": "0x0",
    "blockHash": "0xbeab0aa2411b7ab17f30a99d3cb9c6ef2fc5426d6ad6fd9e2a26a6aed1d1055b",
    "blockNumber": "0x15df",
    "transactionIndex": "0x1",
    "from": "0x407d73d8a49eeb85d32cf465507dd71d507100c1",
    "to": "0x853f43d8a49eeb85d32cf465507dd71d507100c1",
    "value": "0x7f110",
    "gas": "0x7f110",
    "gasPrice": "0x09184e72a000",
    "input": "0x603880600c6000396000f300603880600c6000396000f3603880600c6000396000f360",
    "contractAddress": "0x471a8bf3fd0dfbe20658a97155388cec674190bf",
    "cumulativeGasUsed": "0x158e33",
    "gasUsed": "0xba2e6",
    "logsBloom": "0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "status": "0x1",
    "logs": [
      {
        "removed": false,
        "logIndex": "0x21",
        "data": "",
        "topics": [
          "0xcd60aa75dea3072fbc07ae6d7d856b5dc5f4eee88854f5b4abf7b680ef8bc50f",
          "0x000000000000000000000000e031657a4661340522d1441104dcdae345537ef6"
        ]
      }
    ]
  }
}
```
