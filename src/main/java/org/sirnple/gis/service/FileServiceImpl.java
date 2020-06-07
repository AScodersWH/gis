package org.sirnple.gis.service;

import org.sirnple.gis.config.FileStorageProperties;
import org.sirnple.gis.exception.FileNotFoundException;
import org.sirnple.gis.exception.FileStorageException;
import org.sirnple.gis.global.constant.Dir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by: sirnple
 * Created in: 2020-06-06
 * Description:
 */
@Service
public class FileServiceImpl implements FileService {
    private final Path fileStorageLocation;

    @Autowired
    public FileServiceImpl(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
    }

    @Override
    public String storeFile(Dir dir, MultipartFile file) {
        Path p = this.fileStorageLocation.resolve(dir.getDirName()).toAbsolutePath().normalize();
        return storeFile(p, file);
    }

    private String storeFile(Path dir, MultipartFile file) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = dir.resolve(fileName);
            checkDir(targetLocation);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    @Override
    public Resource loadFileAsResource(Dir dir, String fileName) {
        Path p = this.fileStorageLocation.resolve(dir.getDirName()).toAbsolutePath().normalize();
        return loadFileAsResource(p, fileName);
    }

    private Resource loadFileAsResource(Path dir, String fileName) {
        try {
            Path filePath = dir.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new FileNotFoundException("File not found " + fileName, ex);
        }
    }

    @Override
    public Map<String, String[]> loadAll() {
        Map<String, String[]> allFiles = new HashMap<>();
        Arrays.stream(Dir.values())
                .forEach(dir -> allFiles.put(dir.getDirName(), this.fileStorageLocation.resolve(dir.getDirName()).toFile().list()));
        return allFiles;
    }

    @Override
    public void deleteFile(Dir dir, String fileName) {
        try {
            Files.delete(this.fileStorageLocation.resolve(dir.getDirName()).resolve(fileName));
        } catch (IOException e) {
            throw new FileStorageException("删除文件失败 - " + dir.name() + "/" + fileName, e);
        }
    }

    private void checkDir(Path dir) {
        if (!dir.toFile().exists()) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                throw new FileStorageException("创建文件夹失败 - " + dir);
            }
        }
    }
}
