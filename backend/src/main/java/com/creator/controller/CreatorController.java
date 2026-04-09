package com.creator.controller;

import com.creator.annotation.OperLog;
import com.creator.common.PageResult;
import com.creator.common.Result;
import com.creator.dto.CreatorApplyDTO;
import com.creator.entity.Creator;
import com.creator.service.CreatorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/creator")
@RequiredArgsConstructor
public class CreatorController {
    
    private final CreatorService creatorService;
    
    @PostMapping("/apply")
    @OperLog("申请成为创作者")
    public Result<Void> apply(@Valid @RequestBody CreatorApplyDTO dto) {
        creatorService.apply(dto);
        return Result.success();
    }
    
    @GetMapping("/profile")
    public Result<Creator> getMyProfile() {
        Creator creator = creatorService.getMyCreatorProfile();
        return Result.success(creator);
    }
    
    @PutMapping("/profile")
    @OperLog("更新创作者资料")
    public Result<Void> updateProfile(@RequestBody Creator creator) {
        creatorService.updateProfile(creator);
        return Result.success();
    }
    
    @GetMapping("/list")
    public Result<PageResult<Creator>> getCreatorList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        return Result.success(creatorService.getCreatorList(page, size, category, keyword));
    }
    
    @GetMapping("/{id}")
    public Result<Creator> getCreatorDetail(@PathVariable Long id) {
        return Result.success(creatorService.getCreatorDetail(id));
    }
    
    @GetMapping("/categories")
    public Result<List<String>> getCategories() {
        return Result.success(creatorService.getCategories());
    }
}
