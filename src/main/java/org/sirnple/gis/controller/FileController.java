package org.sirnple.gis.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.sirnple.gis.config.FileStorageProperties;
import org.sirnple.gis.exception.FileUploadException;
import org.sirnple.gis.payload.UploadFileResponse;
import org.sirnple.gis.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by: sirnple
 * Created in: 2020-06-06
 * Description:
 */
@RestController
@RequestMapping("/file")
@Api(tags = "文件传输")
public class FileController {
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    @Autowired
    private FileService fileService;
    @Autowired
    private FileStorageProperties fileStorageProperties;

    @PostMapping("/uploadFile")
    @ApiOperation(value = "上传文件", httpMethod = "POST", notes = "单个文件上传")
    public UploadFileResponse uploadFile(@RequestParam("dir") @ApiParam(allowableValues = "pore_pressure, flow_rate, seabed_sliding, wave") @NotNull String dir, @RequestParam("file") MultipartFile file) {
        if (dir.equals(this.fileStorageProperties.getPorePressureDir())
                || dir.equals(this.fileStorageProperties.getFlowRateDir())
                || dir.equals(this.fileStorageProperties.getSeabedSlidingDir())
                || dir.equals(this.fileStorageProperties.getWaveDir())) {

            String fileName = fileService.storeFile(Paths.get(this.fileStorageProperties.getUploadDir(), dir), file);

            String fileDownloadUri =
                    ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/downloadFile/")
                            .path(fileName)
                            .toUriString();

            return new UploadFileResponse(fileName, fileDownloadUri,
                    file.getContentType(), file.getSize());
        }
        throw new FileUploadException("参数错误，不能上传到dir=" + dir);
    }

    @PostMapping("/uploadMultipleFiles")
    @ApiOperation(value = "上传多个文件", httpMethod = "POST", notes = "多个文件上传")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("dir") @ApiParam(allowableValues = "pore_pressure, flow_rate, seabed_sliding, wave") @NotNull String dir, @RequestParam("file") MultipartFile[] files) {
        return Arrays.stream(files)
                .map(file -> uploadFile(dir, file))
                .collect(Collectors.toList());
    }

    @GetMapping("/downloadFile")
    @ApiOperation(value = "下载文件", httpMethod = "GET", notes = "单个文件下载")
    public ResponseEntity<Resource> downloadFile(@RequestParam @ApiParam(allowableValues = "pore_pressure, flow_rate, seabed_sliding, wave") @NotNull String dir, @RequestParam String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileService.loadFileAsResource(Paths.get(this.fileStorageProperties.getUploadDir(), dir), fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/uploads")
    public Map<String, String[]> listAllFile() {
        return fileService.loadAll();
    }
}
