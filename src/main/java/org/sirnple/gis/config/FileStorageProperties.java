package org.sirnple.gis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by: sirnple
 * Created in: 2020-06-06
 * Description:
 */
@ConfigurationProperties(prefix = "file")
@Configuration
public class FileStorageProperties {
    private String uploadDir;

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }
}
