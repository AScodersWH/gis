package org.sirnple.gis.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.Map;

/**
 * Created by: sirnple
 * Created in: 2020-06-06
 * Description:
 */
public interface FileService {
    String storeFile(Path dir, MultipartFile file);

    Resource loadFileAsResource(Path dir, String fileName);

    Map<String, String[]> loadAll();
}
