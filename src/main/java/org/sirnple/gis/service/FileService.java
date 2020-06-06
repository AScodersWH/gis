package org.sirnple.gis.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by: sirnple
 * Created in: 2020-06-06
 * Description:
 */
public interface FileService {
    String storeFile(MultipartFile file);

    Resource loadFileAsResource(String fileName);
}
