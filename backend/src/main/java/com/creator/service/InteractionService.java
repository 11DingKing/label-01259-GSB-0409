package com.creator.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.creator.common.BusinessException;
import com.creator.common.PageResult;
import com.creator.dto.CommentDTO;
import com.creator.dto.RewardDTO;
import com.creator.entity.*;
import com.creator.mapper.*;
import com.creator.util.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InteractionService {
    
    private final FollowMapper followMapper;
    private final CreatorMapper creatorMapper;
    private final UserMapper userMapper;
    private final CommentMapper commentMapper;
    private final ContentMapper contentMapper;
    private final RewardMapper rewardMapper;
    
    @Transactional
    public void follow(Long creatorId) {
        Long userId = UserContext.getUserId();
        
        Creator creator = creatorMapper.selectById(creatorId);
        if (creator == null || creator.getStatus() != 1) {
            throw new BusinessException("创作者不存在");
        }
        
        if (creator.getUserId().equals(userId)) {
            throw new BusinessException("不能关注自己");
        }
        
        Long count = followMapper.selectCount(
                new LambdaQueryWrapper<Follow>()
                        .eq(Follow::getUserId, userId)
                        .eq(Follow::getCreatorId, creatorId)
        );
        
        if (count > 0) {
            throw new BusinessException("您已关注该创作者");
        }
        
        Follow follow = new Follow();
        follow.setUserId(userId);
        follow.setCreatorId(creatorId);
        followMapper.insert(follow);
        
        creator.setFollowerCount(creator.getFollowerCount() + 1);
        creatorMapper.updateById(creator);
        
        log.info("用户关注创作者: userId={}, creatorId={}", userId, creatorId);
    }
    
    @Transactional
    public void unfollow(Long creatorId) {
        Long userId = UserContext.getUserId();
        
        int deleted = followMapper.delete(
                new LambdaQueryWrapper<Follow>()
                        .eq(Follow::getUserId, userId)
                        .eq(Follow::getCreatorId, creatorId)
        );
        
        if (deleted > 0) {
            Creator creator = creatorMapper.selectById(creatorId);
            if (creator != null) {
                creator.setFollowerCount(Math.max(0, creator.getFollowerCount() - 1));
                creatorMapper.updateById(creator);
            }
            log.info("用户取消关注: userId={}, creatorId={}", userId, creatorId);
        }
    }
    
    public PageResult<Creator> getFollowList(Integer page, Integer size) {
        Long userId = UserContext.getUserId();
        
        Page<Follow> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follow::getUserId, userId)
                .orderByDesc(Follow::getCreatedAt);
        
        Page<Follow> followPage = followMapper.selectPage(pageParam, wrapper);
        
        List<Creator> creators = new ArrayList<>();
        for (Follow f : followPage.getRecords()) {
            Creator creator = creatorMapper.selectById(f.getCreatorId());
            if (creator != null) {
                User user = userMapper.selectById(creator.getUserId());
                if (user != null) {
                    creator.setAvatar(user.getAvatar());
                }
                creator.setIsFollowed(true);
                creators.add(creator);
            }
        }
        
        return PageResult.of(creators, followPage.getTotal(), followPage.getCurrent(), followPage.getSize());
    }
    
    @Transactional
    public void reward(RewardDTO dto) {
        Long userId = UserContext.getUserId();
        
        Creator creator = creatorMapper.selectById(dto.getCreatorId());
        if (creator == null || creator.getStatus() != 1) {
            throw new BusinessException("创作者不存在");
        }
        
        User user = userMapper.selectById(userId);
        if (user.getBalance().compareTo(dto.getAmount()) < 0) {
            throw new BusinessException("余额不足，请先充值");
        }
        
        user.setBalance(user.getBalance().subtract(dto.getAmount()));
        userMapper.updateById(user);
        
        creator.setTotalIncome(creator.getTotalIncome().add(dto.getAmount()));
        creatorMapper.updateById(creator);
        
        User creatorUser = userMapper.selectById(creator.getUserId());
        creatorUser.setBalance(creatorUser.getBalance().add(dto.getAmount()));
        userMapper.updateById(creatorUser);
        
        Reward reward = new Reward();
        reward.setUserId(userId);
        reward.setCreatorId(dto.getCreatorId());
        reward.setContentId(dto.getContentId());
        reward.setAmount(dto.getAmount());
        reward.setMessage(dto.getMessage());
        rewardMapper.insert(reward);
        
        log.info("用户打赏: userId={}, creatorId={}, amount={}", userId, dto.getCreatorId(), dto.getAmount());
    }
    
    public PageResult<Comment> getComments(Long contentId, Integer page, Integer size) {
        Page<Comment> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(Comment::getContentId, contentId)
                .eq(Comment::getParentId, 0)
                .eq(Comment::getStatus, 1)
                .orderByDesc(Comment::getCreatedAt);
        
        Page<Comment> result = commentMapper.selectPage(pageParam, wrapper);
        
        List<Long> commentIds = result.getRecords().stream()
                .map(Comment::getId)
                .collect(Collectors.toList());
        
        Map<Long, List<Comment>> repliesMap = new java.util.HashMap<>();
        if (!commentIds.isEmpty()) {
            List<Comment> replies = commentMapper.selectList(
                    new LambdaQueryWrapper<Comment>()
                            .in(Comment::getParentId, commentIds)
                            .eq(Comment::getStatus, 1)
                            .orderByAsc(Comment::getCreatedAt)
            );
            
            for (Comment reply : replies) {
                User user = userMapper.selectById(reply.getUserId());
                if (user != null) {
                    reply.setUsername(user.getNickname());
                    reply.setAvatar(user.getAvatar());
                }
            }
            
            repliesMap = replies.stream()
                    .collect(Collectors.groupingBy(Comment::getParentId));
        }
        
        for (Comment c : result.getRecords()) {
            User user = userMapper.selectById(c.getUserId());
            if (user != null) {
                c.setUsername(user.getNickname());
                c.setAvatar(user.getAvatar());
            }
            c.setReplies(repliesMap.getOrDefault(c.getId(), new ArrayList<>()));
        }
        
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }
    
    @Transactional
    public void addComment(CommentDTO dto) {
        Long userId = UserContext.getUserId();
        
        Content content = contentMapper.selectById(dto.getContentId());
        if (content == null) {
            throw new BusinessException("内容不存在");
        }
        
        Comment comment = new Comment();
        comment.setContentId(dto.getContentId());
        comment.setUserId(userId);
        comment.setParentId(dto.getParentId());
        comment.setCommentText(dto.getCommentText());
        comment.setLikeCount(0);
        comment.setStatus(1);
        
        commentMapper.insert(comment);
        
        content.setCommentCount(content.getCommentCount() + 1);
        contentMapper.updateById(content);
        
        log.info("用户评论: userId={}, contentId={}", userId, dto.getContentId());
    }
    
    @Transactional
    public void deleteComment(Long id) {
        Long userId = UserContext.getUserId();
        Integer role = UserContext.getRole();
        
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        
        if (!comment.getUserId().equals(userId) && role != 2) {
            throw new BusinessException("无权删除此评论");
        }
        
        commentMapper.deleteById(id);
        
        Content content = contentMapper.selectById(comment.getContentId());
        if (content != null) {
            content.setCommentCount(Math.max(0, content.getCommentCount() - 1));
            contentMapper.updateById(content);
        }
        
        log.info("删除评论: commentId={}", id);
    }
    
    public PageResult<Reward> getRewardList(Integer page, Integer size) {
        Long userId = UserContext.getUserId();
        
        Creator creator = creatorMapper.selectOne(
                new LambdaQueryWrapper<Creator>()
                        .eq(Creator::getUserId, userId)
                        .eq(Creator::getStatus, 1)
        );
        
        if (creator == null) {
            return PageResult.of(new ArrayList<>(), 0L, (long) page, (long) size);
        }
        
        Page<Reward> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Reward> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reward::getCreatorId, creator.getId())
                .orderByDesc(Reward::getCreatedAt);
        
        Page<Reward> result = rewardMapper.selectPage(pageParam, wrapper);
        
        for (Reward r : result.getRecords()) {
            User user = userMapper.selectById(r.getUserId());
            if (user != null) {
                r.setUsername(user.getNickname());
                r.setAvatar(user.getAvatar());
            }
        }
        
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }
}
