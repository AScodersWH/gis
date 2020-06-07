package org.sirnple.gis.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.sirnple.gis.config.FileStorageProperties;
import org.sirnple.gis.global.constant.Dir;
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

        String fileName = this.fileService.storeFile(Dir.parseDir(dir), file);
        String fileDownloadUri =
                ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/downloadFile/")
                        .path(fileName)
                        .encode()
                        .toUriString();

        return new UploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());
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
        Resource resource = fileService.loadFileAsResource(Dir.parseDir(dir), fileName);

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
    @ApiOperation(value = "查询所有文件", httpMethod = "GET", notes = "查询所有文件")
    public Map<String, String[]> listAllFile() {
        return fileService.loadAll();
    }

    @DeleteMapping("/delete")
    @ApiOperation(value = "删除文件", httpMethod = "DELETE", notes = "删除文件")
    public void deleteFile(@RequestParam("dir") @ApiParam(allowableValues = "pore_pressure, flow_rate, seabed_sliding, wave") String dir, @RequestParam("fileName") String fileName) {
        this.fileService.deleteFile(Dir.parseDir(dir), fileName);
    }
}
