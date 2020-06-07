package org.sirnple.gis.service;

import org.sirnple.gis.global.constant.Dir;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Created by: sirnple
 * Created in: 2020-06-06
 * Description:
 */
public interface FileService {
    String storeFile(Dir dir, MultipartFile file);

    Resource loadFileAsResource(Dir dir, String fileName);

    Map<String, String[]> loadAll();

    void deleteFile(Dir dir, String fileName);
}
