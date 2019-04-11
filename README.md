# wallet-grpc
Hiring exercise from betpawa regarding wallet transactions

The task consists of a wallet server and a wallet client. 
The wallet server keep track of a users monetary balance in the system. 
The client emulate users depositing and withdrawing funds.

The wallet server have exposed the interface via gRPC.

Before running please configure the database properties in hibernate.cfg.xml file.

The following technologies have been be used
•	Java
•	gRPC
•	MySQL
•	Gradle
•	JUnit
•	SLF4J
•	Hibernate

Initially, I intended to create server and client are seperate modules but gradle build was not working out.
Hence, moved back to single module.

Once the database properties are set in above file.
Please create a database in MySQL named wallet, if not present.

Fire the server via gradle
```
gradle clean runServer
```
Once the server is up, one can run the client via gradle too
```
gradle clean runClient -Pmyargs=<num_of_users>,<concurrent_threads_per_user>,<rounds_per_thread>
```

For simplicity, we are choosing randomly one of the below 3 Round of wallet transaction 

•	Round A

  •	Deposit 100 USD
  
  •	Withdraw 200 USD
  
  •	Deposit 100 EUR
  
  •	Get Balance
  
  •	Withdraw 100 USD
  
  •	Get Balance
  
  •	Withdraw 100 USD
 

•	Round B

  •	Withdraw 100 GBP
  
  •	Deposit 300 GPB
  
  •	Withdraw 100 GBP
  
  •	Withdraw 100 GBP
  
  •	Withdraw 100 GBP
  

•	Round C

  •	Get Balance
  
  •	Deposit 100 USD
  
  •	Deposit 100 USD
  
  •	Withdraw 100 USD
  
  •	Depsoit 100 USD
  
  •	Get Balance
  
  •	Withdraw 200 USD
  
  •	Get Balance
  

In client, before making wallet transaction calls, we create 3 wallets for a single user with 3 currencies, namely USD, GBP, & EUR, each as a single user can have multiple currency wallets.
Since each credit/debit transaction has a currency with it, the server side transaction will happen only with the wallet of that currency only.

While testing locally we found that wallet can handle 35 sync calls per user per thread per second.
We tested this scenario of 1 user and 1 thread for 100 rounds of call.

