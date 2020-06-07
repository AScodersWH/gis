package org.sirnple.gis.service;

import org.sirnple.gis.config.FileStorageProperties;
import org.sirnple.gis.exception.FileNotFoundException;
import org.sirnple.gis.exception.FileStorageException;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by: sirnple
 * Created in: 2020-06-06
 * Description:
 */
@Service
public class FileServiceImpl implements FileService {
    private final Path porePressureDir;
    private final Path flowRateDir;
    private final Path seabedSlidingDir;
    private final Path waveDir;

    @Autowired
    public FileServiceImpl(FileStorageProperties fileStorageProperties) {
        Path fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
        this.porePressureDir = Paths.get(fileStorageProperties.getUploadDir(), fileStorageProperties.getPorePressureDir())
                .toAbsolutePath().normalize();

        this.flowRateDir = Paths.get(fileStorageProperties.getUploadDir(), fileStorageProperties.getFlowRateDir())
                .toAbsolutePath().normalize();
        this.seabedSlidingDir = Paths.get(fileStorageProperties.getUploadDir(), fileStorageProperties.getSeabedSlidingDir())
                .toAbsolutePath().normalize();
        this.waveDir = Paths.get(fileStorageProperties.getUploadDir(), fileStorageProperties.getWaveDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(fileStorageLocation);
            Files.createDirectories(this.porePressureDir);
            Files.createDirectories(this.flowRateDir);
            Files.createDirectories(this.seabedSlidingDir);
            Files.createDirectories(this.waveDir);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Override
    public String storeFile(Path dir, MultipartFile file) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = dir.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    @Override
    public Resource loadFileAsResource(Path dir, String fileName) {
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
        allFiles.put(this.porePressureDir.toFile().getName(), this.porePressureDir.toFile().list());
        allFiles.put(this.flowRateDir.toFile().getName(), this.flowRateDir.toFile().list());
        allFiles.put(this.seabedSlidingDir.toFile().getName(), this.seabedSlidingDir.toFile().list());
        allFiles.put(this.waveDir.toFile().getName(), this.waveDir.toFile().list());
        return allFiles;
    }
}
