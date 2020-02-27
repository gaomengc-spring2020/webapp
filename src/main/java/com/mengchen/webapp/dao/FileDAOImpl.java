package com.mengchen.webapp.dao;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.mengchen.webapp.entity.Bill;
import com.mengchen.webapp.entity.File;
import com.mengchen.webapp.exceptions.FileStorageException;
import com.mengchen.webapp.properties.FileStorageProperties;
import com.mengchen.webapp.repository.BillRepository;
import com.mengchen.webapp.repository.FileRepository;
import com.mengchen.webapp.service.BillService;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.xml.bind.DatatypeConverter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

@Repository
public class FileDAOImpl implements FileDAO{

//    @Autowired
//    Environment env;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    BillRepository billRepository;

    private org.jboss.logging.Logger logger = org.jboss.logging.Logger.getLogger(org.jboss.logging.Logger.class);


    @Value("${aws.s3.bucket.name}")
    private String s3BucketName;

    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.s3.bucket.url}")
    private String s3BucketUrl;

    private EntityManager entityManager;


    @Autowired
    public FileDAOImpl (EntityManager theEntityManager){
//        this.rootLocation = Paths.get(fileStorageProperties.getLocation());
        this.entityManager = theEntityManager;
    }

    @Override
    public File storeFile(MultipartFile file, Bill theBill) {
//        Path uploadPath = Paths.get(this.rootLocation + "/" + theBill.getBill_id());

//        try {
//            if (file.isEmpty()) {
//                throw new FileStorageException("Failed to store empty file " + file.getOriginalFilename());
//            }
//            uploadPath.toFile().mkdir();
//
//            //Path uploadPath = this.rootLocation.
//            Files.copy(file.getInputStream(), uploadPath.resolve(file.getOriginalFilename()));
//        } catch (IOException e) {
//            throw new FileStorageException("Failed to store file " + file.getOriginalFilename(), e);
//        }

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());


        AmazonS3 s3client = AmazonS3ClientBuilder
                .standard()
                .withRegion(awsRegion)
                .build();

        if(this.s3BucketName == null){
            this.s3BucketName = System.getenv("AWS_S3_BUCKET_NAME");
        }
        String fileKey = "uploads/"+ theBill.getBill_id() + "/" + fileName;
        try {
            s3client.putObject(
                    s3BucketName,
                    fileKey,
                    convert(file)
            );
        } catch (IOException ex) {
            throw new FileStorageException("Failed to store file " + file.getOriginalFilename(), ex);
        }


        // Check if the file's name contains invalid characters
        if(fileName.contains("..")) {
            System.out.println("Sorry! Filename contains invalid path sequence " + fileName);
        }

        File theFile = new File();

        theFile.setFileName(fileName);
        theFile.setSize(file.getSize());
        theFile.setUrl(s3BucketUrl+fileKey);
        theFile.setOriginName(file.getOriginalFilename());
        theFile.setContentType(file.getContentType());
        theFile.setHash(getMD5(file));
        theFile.setOwnerEmail(theBill.getOwner_id());

        theBill.setAttachment(theFile);

        theFile.setBill(theBill);
        billRepository.save(theBill);

        return theFile;
    }

    @Override
    public File uploadFile(File theFile) {
        Session currentSession = entityManager.unwrap(Session.class);
        currentSession.save(theFile);
        return theFile;
    }

    @Override
    public File findFile(String theFileId) {

        Session currentSession = entityManager.unwrap(Session.class);
        Query<File> theQuery =
                currentSession.createQuery("from File where id=:fileId", File.class);
        theQuery.setParameter("fileId", theFileId);

        return theQuery.uniqueResultOptional().orElse(null);
    }

    @Override
    public void deleteFile(String theFileId) throws NullPointerException , IOException {

        AmazonS3 s3client = AmazonS3ClientBuilder
                .standard()
                .withRegion(awsRegion)
                .build();

        if(this.s3BucketName == null){
            this.s3BucketName = System.getenv("AWS_S3_BUCKET_NAME");
        }

//        Path filePath = Paths.get(this.rootLocation + "/" + findFile(theFileId).getBill().getBill_id());

        String fileKey = "uploads/" + findFile(theFileId).getBill().getBill_id() +"/" + findFile(theFileId).getFileName();


        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest( s3BucketName ).withKeys( fileKey );
        s3client.deleteObjects(deleteObjectsRequest);

//        FileSystemUtils.deleteRecursively(filePath);


        fileRepository.deleteById(theFileId);
        logger.info(">>>>>> FileDAO: deleteFile: " + "deleted");

    }


    private String getMD5(MultipartFile file){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(file.getBytes());
            byte[] digest = md.digest();
            return DatatypeConverter
                    .printHexBinary(digest).toUpperCase();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public java.io.File convert(MultipartFile file) throws IOException {
        java.io.File convFile = new java.io.File(file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}
