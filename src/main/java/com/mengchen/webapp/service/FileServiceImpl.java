package com.mengchen.webapp.service;

import com.mengchen.webapp.dao.FileDAO;
import com.mengchen.webapp.entity.Bill;
import com.mengchen.webapp.entity.File;
import com.mengchen.webapp.properties.FileStorageProperties;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Validated
@Service
public class FileServiceImpl implements FileService{

    FileStorageProperties fileStorageProperties;

    private FileDAO fileDAO;

    public FileServiceImpl(FileDAO fileDAO) {
        this.fileDAO = fileDAO;
    }

    public File storeFile(MultipartFile file, Bill theBill){
        return fileDAO.storeFile(file, theBill);
    }

    @Override
    public File uploadFile(File theFile) {
       return fileDAO.uploadFile(theFile);
    }

    @Override
    public File findFile(String theFileId) {

        return fileDAO.findFile(theFileId);
    }

    @Override
    public void deleteFile(String theFileId) throws IOException {
        fileDAO.deleteFile(theFileId);
    }
}
