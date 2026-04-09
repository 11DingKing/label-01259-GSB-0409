package com.creator.controller;

import com.creator.annotation.OperLog;
import com.creator.common.BusinessException;
import com.creator.common.PageResult;
import com.creator.common.Result;
import com.creator.entity.Content;
import com.creator.entity.Creator;
import com.creator.entity.OperationLog;
import com.creator.entity.User;
import com.creator.service.AnalyticsService;
import com.creator.service.ContentService;
import com.creator.service.CreatorService;
import com.creator.service.UserService;
import com.creator.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final UserService userService;
    private final CreatorService creatorService;
    private final ContentService contentService;
    private final AnalyticsService analyticsService;
    
    private void checkAdmin() {
        Integer role = UserContext.getRole();
        if (role == null || role != 2) {
            throw new BusinessException(403, "无权访问");
        }
    }
    
    @GetMapping("/users")
    public Result<PageResult<User>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        checkAdmin();
        return Result.success(userService.getUserList(page, size, keyword));
    }
    
    @PutMapping("/user/{id}/status")
    @OperLog("更新用户状态")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        checkAdmin();
        userService.updateUserStatus(id, status);
        return Result.success();
    }
    
    @GetMapping("/creators")
    public Result<PageResult<Creator>> getPendingCreators(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        checkAdmin();
        return Result.success(creatorService.getPendingCreators(page, size));
    }
    
    @PutMapping("/creator/{id}/audit")
    @OperLog("审核创作者")
    public Result<Void> auditCreator(@PathVariable Long id, @RequestParam Integer status) {
        checkAdmin();
        creatorService.auditCreator(id, status);
        return Result.success();
    }
    
    @GetMapping("/contents")
    public Result<PageResult<Content>> getContentList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        checkAdmin();
        return Result.success(contentService.getAllContents(page, size, status, keyword));
    }
    
    @GetMapping("/content/{id}")
    public Result<Content> getContentDetail(@PathVariable Long id) {
        checkAdmin();
        return Result.success(contentService.getContentDetailForAdmin(id));
    }
    
    @PutMapping("/content/{id}/status")
    @OperLog("更新内容状态")
    public Result<Void> updateContentStatus(@PathVariable Long id, @RequestParam Integer status) {
        checkAdmin();
        contentService.updateContentStatus(id, status);
        return Result.success();
    }
    
    @DeleteMapping("/content/{id}")
    @OperLog("删除内容")
    public Result<Void> deleteContent(@PathVariable Long id) {
        checkAdmin();
        contentService.deleteContent(id);
        return Result.success();
    }
    
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        checkAdmin();
        return Result.success(analyticsService.getAdminStatistics());
    }
    
    @GetMapping("/logs")
    public Result<PageResult<OperationLog>> getOperationLogs(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        checkAdmin();
        return Result.success(analyticsService.getOperationLogs(page, size, keyword));
    }
}
