package com.mengchen.webapp.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class FileStorageProperties {

    /**
     * Folder location for storing files
     */
    private String location = "src/main/resources/upload-dir";

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
