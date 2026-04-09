package com.creator.controller;

import com.creator.annotation.OperLog;
import com.creator.common.PageResult;
import com.creator.common.Result;
import com.creator.dto.ContentDTO;
import com.creator.entity.Content;
import com.creator.service.ContentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/content")
@RequiredArgsConstructor
public class ContentController {
    
    private final ContentService contentService;
    
    @PostMapping
    @OperLog("发布内容")
    public Result<Void> createContent(@Valid @RequestBody ContentDTO dto) {
        contentService.createContent(dto);
        return Result.success();
    }
    
    @PutMapping("/{id}")
    @OperLog("更新内容")
    public Result<Void> updateContent(@PathVariable Long id, @Valid @RequestBody ContentDTO dto) {
        contentService.updateContent(id, dto);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    @OperLog("删除内容")
    public Result<Void> deleteContent(@PathVariable Long id) {
        contentService.deleteContent(id);
        return Result.success();
    }
    
    @GetMapping("/{id}")
    public Result<Content> getContentDetail(@PathVariable Long id) {
        return Result.success(contentService.getContentDetail(id));
    }
    
    @GetMapping("/list")
    public Result<PageResult<Content>> getContentList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer contentType,
            @RequestParam(required = false) String keyword) {
        return Result.success(contentService.getContentList(page, size, contentType, keyword));
    }
    
    @GetMapping("/creator/{creatorId}")
    public Result<PageResult<Content>> getCreatorContents(
            @PathVariable Long creatorId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(contentService.getCreatorContents(creatorId, page, size));
    }
    
    @GetMapping("/my")
    public Result<PageResult<Content>> getMyContents(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        return Result.success(contentService.getMyContents(page, size, status));
    }
    
    @PostMapping("/{id}/like")
    @OperLog("点赞内容")
    public Result<Void> likeContent(@PathVariable Long id) {
        contentService.likeContent(id);
        return Result.success();
    }
    
    @PostMapping("/{id}/purchase")
    @OperLog("购买内容")
    public Result<Void> purchaseContent(@PathVariable Long id) {
        contentService.purchaseContent(id);
        return Result.success();
    }
}
