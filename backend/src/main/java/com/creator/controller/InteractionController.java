package com.creator.controller;

import com.creator.annotation.OperLog;
import com.creator.common.PageResult;
import com.creator.common.Result;
import com.creator.dto.CommentDTO;
import com.creator.dto.RewardDTO;
import com.creator.entity.Comment;
import com.creator.entity.Creator;
import com.creator.entity.Reward;
import com.creator.service.InteractionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InteractionController {
    
    private final InteractionService interactionService;
    
    @PostMapping("/follow/{creatorId}")
    @OperLog("关注创作者")
    public Result<Void> follow(@PathVariable Long creatorId) {
        interactionService.follow(creatorId);
        return Result.success();
    }
    
    @DeleteMapping("/follow/{creatorId}")
    @OperLog("取消关注")
    public Result<Void> unfollow(@PathVariable Long creatorId) {
        interactionService.unfollow(creatorId);
        return Result.success();
    }
    
    @GetMapping("/follow/list")
    public Result<PageResult<Creator>> getFollowList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(interactionService.getFollowList(page, size));
    }
    
    @PostMapping("/reward")
    @OperLog("打赏创作者")
    public Result<Void> reward(@Valid @RequestBody RewardDTO dto) {
        interactionService.reward(dto);
        return Result.success();
    }
    
    @GetMapping("/reward/list")
    public Result<PageResult<Reward>> getRewardList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(interactionService.getRewardList(page, size));
    }
    
    @GetMapping("/comment/content/{contentId}")
    public Result<PageResult<Comment>> getComments(
            @PathVariable Long contentId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(interactionService.getComments(contentId, page, size));
    }
    
    @PostMapping("/comment")
    @OperLog("发表评论")
    public Result<Void> addComment(@Valid @RequestBody CommentDTO dto) {
        interactionService.addComment(dto);
        return Result.success();
    }
    
    @DeleteMapping("/comment/{id}")
    @OperLog("删除评论")
    public Result<Void> deleteComment(@PathVariable Long id) {
        interactionService.deleteComment(id);
        return Result.success();
    }
}
