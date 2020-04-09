# webapp - Instruction


## :pencil: Frameworks
This application is based on:
 * Springboot & , connected with MySQL. 
 * Hibernate
 * MySQL
 * JDK 1.8 +
 
## :pencil: Pre-requisite
### maven installed 
   you could install maven by the command : 
```shell script
    unzip apache-maven-3.6.3-bin.zip

```
* Add the bin directory of the created directory apache-maven-3.6.3 to the PATH environment variable

* Confirm with mvn -v in a new shell. The result should look similar to
```shell script
Apache Maven 3.6.3 (cecedd343002696d0abb50b32b541b8a6ba2883f)
Maven home: /opt/apache-maven-3.6.3
Java version: 1.8.0_45, vendor: Oracle Corporation
Java home: /Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre
Default locale: en_US, platform encoding: UTF-8
OS name: "mac os x", version: "10.8.5", arch: "x86_64", family: "mac"
    
```

### MySQL installed
### IDE (intelliJ or Eclipse)

### run test command 

```shell script
    mvn integration-test

```
circleCi demo


3. Set up SSL Certificate for prod.mengchen-gao.me

    ```shell script
   
   1. generaet private key 
    openssl genrsa 2048 > private-key.pem
   
   2. get CRS
   openssl req -new -key private-key.pem -out csr.pem
   
   3. get Certificate on Namecheap
   
   4. import to ACM

   aws acm import-certificate --certificate file://Ce***te.pem
                              --certificate-chain file://Cer***Chain.pem
                              --private-key file://Pr***Key.pem
                              --profile p***


   -------------------------------
   other: 
   1. set IAM for certificate
   aws iam upload-server-certificate \
       --server-certificate-name ch***ame \
       --certificate-body file://pr***e.pem \
       --private-key file://private-key.pem \
       --profile p***
   
   2. check 
   aws iam get-server-certificate \
     --server-certificate-name ch***me \
     --profile p***
    
  
     ```
