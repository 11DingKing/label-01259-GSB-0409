package com.creator.controller;

import com.creator.common.BusinessException;
import com.creator.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Value("${upload.path:uploads}")
    private String uploadPath;

    @Value("${upload.base-url:/uploads}")
    private String baseUrl;

    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        return Result.success(saveFile(file, "images", new String[]{"jpg", "jpeg", "png", "gif", "webp"}, 10));
    }

    @PostMapping("/video")
    public Result<String> uploadVideo(@RequestParam("file") MultipartFile file) {
        // 视频文件限制 500MB
        if (file.getSize() > 500 * 1024 * 1024) {
            throw new BusinessException("视频文件大小不能超过500MB");
        }
        return Result.success(saveFile(file, "videos", new String[]{"mp4", "webm", "mov", "avi"}, 500));
    }

    @PostMapping("/audio")
    public Result<String> uploadAudio(@RequestParam("file") MultipartFile file) {
        // 音频文件限制 100MB
        if (file.getSize() > 100 * 1024 * 1024) {
            throw new BusinessException("音频文件大小不能超过100MB");
        }
        return Result.success(saveFile(file, "audios", new String[]{"mp3", "wav", "ogg", "m4a"}, 100));
    }

    @PostMapping("/images")
    public Result<List<String>> uploadImages(@RequestParam("files") MultipartFile[] files) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            urls.add(saveFile(file, "images", new String[]{"jpg", "jpeg", "png", "gif", "webp"}, 10));
        }
        return Result.success(urls);
    }

    private String saveFile(MultipartFile file, String subDir, String[] allowedExtensions, int maxSizeMB) {
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename).toLowerCase();

        boolean allowed = false;
        for (String ext : allowedExtensions) {
            if (ext.equals(extension)) {
                allowed = true;
                break;
            }
        }
        if (!allowed) {
            throw new BusinessException("不支持的文件格式: " + extension);
        }

        // 限制文件大小
        if (file.getSize() > (long) maxSizeMB * 1024 * 1024) {
            throw new BusinessException("文件大小不能超过" + maxSizeMB + "MB");
        }

        String filename = UUID.randomUUID().toString() + "." + extension;
        Path dirPath = Paths.get(uploadPath, subDir);
        Path filePath = dirPath.resolve(filename);

        try {
            Files.createDirectories(dirPath);
            Files.write(filePath, file.getBytes());
            log.info("文件上传成功: {}", filePath);
            return baseUrl + "/" + subDir + "/" + filename;
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException("文件上传失败");
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
