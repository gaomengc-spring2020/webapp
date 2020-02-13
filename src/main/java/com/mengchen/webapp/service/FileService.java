package com.mengchen.webapp.service;

import com.mengchen.webapp.entity.Bill;
import com.mengchen.webapp.entity.File;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    File storeFile(MultipartFile file, Bill theBill);
    File uploadFile(File theFile);
    File findFile(String theFileId);
    void deleteFile(String theFileId);
}
