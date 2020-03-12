package com.mengchen.webapp.dao;

import com.mengchen.webapp.entity.Bill;
import com.mengchen.webapp.entity.File;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileDAO {
    File storeFile(MultipartFile file, Bill theBill);
    File uploadFile(File theFile);
    File findFile(String theFileId);
    void deleteFile(String theFileId) throws IOException;
    void deleteFileInS3(String theFileId);
}
