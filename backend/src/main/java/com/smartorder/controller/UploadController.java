package com.smartorder.controller;

import com.smartorder.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin
public class UploadController {
    
    @Value("${upload.path:uploads}")
    private String uploadPath;
    
    @Value("${server.port:8081}")
    private String serverPort;
    
    @Value("${server.host:106.15.90.212}")
    private String serverHost;
    
    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }
        
        try {
            String contentType = file.getContentType();
            if (contentType == null || (!contentType.startsWith("image/"))) {
                return Result.error("只能上传图片文件");
            }
            
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            
            String dateFolder = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String fileName = UUID.randomUUID().toString() + extension;
            
            Path uploadDir = Paths.get(uploadPath, dateFolder);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            
            Path filePath = uploadDir.resolve(fileName);
            Files.write(filePath, file.getBytes());
            
            String fileUrl = "http://localhost:8081/uploads/" + dateFolder + "/" + fileName;
            return Result.success(fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("上传失败：" + e.getMessage());
        }
    }
}
