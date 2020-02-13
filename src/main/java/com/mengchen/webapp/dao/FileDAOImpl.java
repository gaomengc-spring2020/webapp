package com.mengchen.webapp.dao;

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
import org.springframework.stereotype.Repository;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

@Repository
public class FileDAOImpl implements FileDAO{

    @Autowired
    FileRepository fileRepository;

    @Autowired
    BillRepository billRepository;

    private org.jboss.logging.Logger logger = org.jboss.logging.Logger.getLogger(org.jboss.logging.Logger.class);



    private EntityManager entityManager;

    private Path rootLocation;

    @Autowired
    public FileDAOImpl (FileStorageProperties fileStorageProperties,EntityManager theEntityManager){
        this.rootLocation = Paths.get(fileStorageProperties.getLocation());
        this.entityManager = theEntityManager;
    }

    @Override
    public File storeFile(MultipartFile file, Bill theBill) {
        Path uploadPath = Paths.get(this.rootLocation + "/" + theBill.getBill_id());

        try {
            if (file.isEmpty()) {
                throw new FileStorageException("Failed to store empty file " + file.getOriginalFilename());
            }
            uploadPath.toFile().mkdir();

            //            Path uploadPath = this.rootLocation.
            Files.copy(file.getInputStream(), uploadPath.resolve(file.getOriginalFilename()));
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file " + file.getOriginalFilename(), e);
        }

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        // Check if the file's name contains invalid characters
        if(fileName.contains("..")) {
            System.out.println("Sorry! Filename contains invalid path sequence " + fileName);
        }

        File theFile = new File();

        theFile.setFileName(fileName);
        theFile.setSize(file.getSize());
        theFile.setUrl(uploadPath.toString());

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

        File theFile = theQuery.uniqueResultOptional().orElse(null);
        if(theFile == null ) return null;

        return theFile;
    }

    @Override
    public void deleteFile(String theFileId) {

        try{
            Path filePath = Paths.get(this.rootLocation + "/" + findFile(theFileId).getBill().getBill_id());
            FileSystemUtils.deleteRecursively(filePath);

        }catch (NullPointerException | IOException ex){
            ex.printStackTrace();
        }


        fileRepository.deleteById(theFileId);
        logger.info(">>>>>> FileDAO: deleteFile: " + "deleted");

    }
}
