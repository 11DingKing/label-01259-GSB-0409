package com.creator.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.creator.common.BusinessException;
import com.creator.common.PageResult;
import com.creator.dto.ContentDTO;
import com.creator.entity.*;
import com.creator.mapper.*;
import com.creator.util.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentService {
    
    private final ContentMapper contentMapper;
    private final CreatorMapper creatorMapper;
    private final UserMapper userMapper;
    private final ContentLikeMapper contentLikeMapper;
    private final PurchaseMapper purchaseMapper;
    
    @Transactional
    public void createContent(ContentDTO dto) {
        Long userId = UserContext.getUserId();
        
        Creator creator = creatorMapper.selectOne(
                new LambdaQueryWrapper<Creator>()
                        .eq(Creator::getUserId, userId)
                        .eq(Creator::getStatus, 1)
        );
        
        if (creator == null) {
            throw new BusinessException("您还不是创作者，请先申请入驻");
        }
        
        Content content = new Content();
        content.setCreatorId(creator.getId());
        content.setTitle(dto.getTitle());
        content.setSummary(dto.getSummary());
        content.setContent(dto.getContent());
        content.setCoverImage(dto.getCoverImage());
        content.setMediaUrl(dto.getMediaUrl());
        content.setContentType(dto.getContentType());
        content.setIsPaid(dto.getIsPaid());
        content.setPrice(dto.getPrice() != null ? dto.getPrice() : BigDecimal.ZERO);
        content.setViewCount(0);
        content.setLikeCount(0);
        content.setCommentCount(0);
        content.setStatus(dto.getStatus());
        
        contentMapper.insert(content);
        
        creator.setContentCount(creator.getContentCount() + 1);
        creatorMapper.updateById(creator);
        
        log.info("创作者发布内容: creatorId={}, title={}", creator.getId(), dto.getTitle());
    }
    
    @Transactional
    public void updateContent(Long id, ContentDTO dto) {
        Long userId = UserContext.getUserId();
        
        Content content = contentMapper.selectById(id);
        if (content == null) {
            throw new BusinessException("内容不存在");
        }
        
        Creator creator = creatorMapper.selectById(content.getCreatorId());
        if (creator == null || !creator.getUserId().equals(userId)) {
            throw new BusinessException("无权修改此内容");
        }
        
        content.setTitle(dto.getTitle());
        content.setSummary(dto.getSummary());
        content.setContent(dto.getContent());
        content.setCoverImage(dto.getCoverImage());
        content.setMediaUrl(dto.getMediaUrl());
        content.setContentType(dto.getContentType());
        content.setIsPaid(dto.getIsPaid());
        content.setPrice(dto.getPrice());
        content.setStatus(dto.getStatus());
        
        contentMapper.updateById(content);
        log.info("创作者更新内容: contentId={}", id);
    }
    
    @Transactional
    public void deleteContent(Long id) {
        Long userId = UserContext.getUserId();
        
        Content content = contentMapper.selectById(id);
        if (content == null) {
            throw new BusinessException("内容不存在");
        }
        
        Creator creator = creatorMapper.selectById(content.getCreatorId());
        if (creator == null || !creator.getUserId().equals(userId)) {
            Integer role = UserContext.getRole();
            if (role != 2) {
                throw new BusinessException("无权删除此内容");
            }
        }
        
        contentMapper.deleteById(id);
        
        if (creator != null) {
            creator.setContentCount(Math.max(0, creator.getContentCount() - 1));
            creatorMapper.updateById(creator);
        }
        
        log.info("删除内容: contentId={}", id);
    }
    
    public Content getContentDetail(Long id) {
        Content content = contentMapper.selectById(id);
        
        if (content == null || content.getStatus() == 0) {
            throw new BusinessException("内容不存在");
        }
        
        content.setViewCount(content.getViewCount() + 1);
        contentMapper.updateById(content);
        
        Creator creator = creatorMapper.selectById(content.getCreatorId());
        if (creator != null) {
            content.setCreatorName(creator.getPenName());
            User user = userMapper.selectById(creator.getUserId());
            if (user != null) {
                content.setCreatorAvatar(user.getAvatar());
            }
        }
        
        Long currentUserId = UserContext.getUserId();
        if (currentUserId != null) {
            Long likeCount = contentLikeMapper.selectCount(
                    new LambdaQueryWrapper<ContentLike>()
                            .eq(ContentLike::getUserId, currentUserId)
                            .eq(ContentLike::getContentId, id)
            );
            content.setIsLiked(likeCount > 0);
            
            if (content.getIsPaid() == 1) {
                Long purchaseCount = purchaseMapper.selectCount(
                        new LambdaQueryWrapper<Purchase>()
                                .eq(Purchase::getUserId, currentUserId)
                                .eq(Purchase::getContentId, id)
                );
                content.setIsPurchased(purchaseCount > 0);
                
                if (purchaseCount == 0) {
                    Creator contentCreator = creatorMapper.selectById(content.getCreatorId());
                    if (contentCreator == null || !contentCreator.getUserId().equals(currentUserId)) {
                        content.setContent("此内容为付费内容，请购买后查看");
                    }
                }
            }
        } else if (content.getIsPaid() == 1) {
            content.setContent("此内容为付费内容，请登录后购买查看");
        }
        
        return content;
    }
    
    public PageResult<Content> getContentList(Integer page, Integer size, Integer contentType, String keyword) {
        Page<Content> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Content> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(Content::getStatus, 1);
        
        if (contentType != null) {
            wrapper.eq(Content::getContentType, contentType);
        }
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Content::getTitle, keyword);
        }
        
        wrapper.orderByDesc(Content::getCreatedAt);
        
        Page<Content> result = contentMapper.selectPage(pageParam, wrapper);
        
        for (Content c : result.getRecords()) {
            Creator creator = creatorMapper.selectById(c.getCreatorId());
            if (creator != null) {
                c.setCreatorName(creator.getPenName());
                User user = userMapper.selectById(creator.getUserId());
                if (user != null) {
                    c.setCreatorAvatar(user.getAvatar());
                }
            }
            c.setContent(null);
        }
        
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }
    
    public PageResult<Content> getCreatorContents(Long creatorId, Integer page, Integer size) {
        Page<Content> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Content> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(Content::getCreatorId, creatorId)
                .eq(Content::getStatus, 1)
                .orderByDesc(Content::getCreatedAt);
        
        Page<Content> result = contentMapper.selectPage(pageParam, wrapper);
        
        Creator creator = creatorMapper.selectById(creatorId);
        for (Content c : result.getRecords()) {
            if (creator != null) {
                c.setCreatorName(creator.getPenName());
            }
            c.setContent(null);
        }
        
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }
    
    public PageResult<Content> getMyContents(Integer page, Integer size, Integer status) {
        Long userId = UserContext.getUserId();
        
        Creator creator = creatorMapper.selectOne(
                new LambdaQueryWrapper<Creator>()
                        .eq(Creator::getUserId, userId)
                        .eq(Creator::getStatus, 1)
        );
        
        if (creator == null) {
            return PageResult.of(java.util.Collections.emptyList(), 0L, (long) page, (long) size);
        }
        
        Page<Content> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Content> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(Content::getCreatorId, creator.getId());
        
        if (status != null) {
            wrapper.eq(Content::getStatus, status);
        }
        
        wrapper.orderByDesc(Content::getCreatedAt);
        
        Page<Content> result = contentMapper.selectPage(pageParam, wrapper);
        
        for (Content c : result.getRecords()) {
            c.setCreatorName(creator.getPenName());
            c.setContent(null);
        }
        
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }
    
    @Transactional
    public void likeContent(Long id) {
        Long userId = UserContext.getUserId();
        
        Content content = contentMapper.selectById(id);
        if (content == null) {
            throw new BusinessException("内容不存在");
        }
        
        Long count = contentLikeMapper.selectCount(
                new LambdaQueryWrapper<ContentLike>()
                        .eq(ContentLike::getUserId, userId)
                        .eq(ContentLike::getContentId, id)
        );
        
        if (count > 0) {
            contentLikeMapper.delete(
                    new LambdaQueryWrapper<ContentLike>()
                            .eq(ContentLike::getUserId, userId)
                            .eq(ContentLike::getContentId, id)
            );
            content.setLikeCount(Math.max(0, content.getLikeCount() - 1));
        } else {
            ContentLike like = new ContentLike();
            like.setUserId(userId);
            like.setContentId(id);
            contentLikeMapper.insert(like);
            content.setLikeCount(content.getLikeCount() + 1);
        }
        
        contentMapper.updateById(content);
    }
    
    @Transactional
    public void purchaseContent(Long id) {
        Long userId = UserContext.getUserId();
        
        Content content = contentMapper.selectById(id);
        if (content == null) {
            throw new BusinessException("内容不存在");
        }
        
        if (content.getIsPaid() != 1) {
            throw new BusinessException("该内容为免费内容");
        }
        
        Long count = purchaseMapper.selectCount(
                new LambdaQueryWrapper<Purchase>()
                        .eq(Purchase::getUserId, userId)
                        .eq(Purchase::getContentId, id)
        );
        
        if (count > 0) {
            throw new BusinessException("您已购买过该内容");
        }
        
        User user = userMapper.selectById(userId);
        if (user.getBalance().compareTo(content.getPrice()) < 0) {
            throw new BusinessException("余额不足，请先充值");
        }
        
        BigDecimal newBalance = user.getBalance().subtract(content.getPrice());
        LambdaUpdateWrapper<User> userUpdateWrapper = Wrappers.lambdaUpdate();
        userUpdateWrapper.eq(User::getId, userId)
                         .eq(User::getVersion, user.getVersion())
                         .ge(User::getBalance, content.getPrice());
        user.setBalance(newBalance);
        int updateCount = userMapper.update(user, userUpdateWrapper);
        if (updateCount != 1) {
            throw new BusinessException("当前用户余额已变更，请刷新后重试");
        }
        
        Creator creator = creatorMapper.selectById(content.getCreatorId());
        LambdaUpdateWrapper<Creator> creatorUpdateWrapper = Wrappers.lambdaUpdate();
        creatorUpdateWrapper.eq(Creator::getId, creator.getId())
                            .eq(Creator::getVersion, creator.getVersion());
        creator.setTotalIncome(creator.getTotalIncome().add(content.getPrice()));
        int incomeUpdateCount = creatorMapper.update(creator, creatorUpdateWrapper);
        if (incomeUpdateCount != 1) {
            throw new BusinessException("更新创作者收益失败");
        }
        
        User creatorUser = userMapper.selectById(creator.getUserId());
        LambdaUpdateWrapper<User> creatorBalanceWrapper = Wrappers.lambdaUpdate();
        creatorBalanceWrapper.eq(User::getId, creatorUser.getId())
                             .eq(User::getVersion, creatorUser.getVersion());
        creatorUser.setBalance(creatorUser.getBalance().add(content.getPrice()));
        int creatorBalanceUpdate = userMapper.update(creatorUser, creatorBalanceWrapper);
        if (creatorBalanceUpdate != 1) {
            throw new BusinessException("更新创作者账户余额失败");
        }
        
        Purchase purchase = new Purchase();
        purchase.setUserId(userId);
        purchase.setContentId(id);
        purchase.setAmount(content.getPrice());
        purchaseMapper.insert(purchase);
        
        log.info("用户购买内容: userId={}, contentId={}, amount={}", userId, id, content.getPrice());
    }
    
    public PageResult<Content> getAllContents(Integer page, Integer size, Integer status, String keyword) {
        Page<Content> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Content> wrapper = new LambdaQueryWrapper<>();
        
        if (status != null) {
            wrapper.eq(Content::getStatus, status);
        }
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Content::getTitle, keyword);
        }
        
        wrapper.orderByDesc(Content::getCreatedAt);
        
        Page<Content> result = contentMapper.selectPage(pageParam, wrapper);
        
        for (Content c : result.getRecords()) {
            Creator creator = creatorMapper.selectById(c.getCreatorId());
            if (creator != null) {
                c.setCreatorName(creator.getPenName());
            }
            c.setContent(null);
        }
        
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }
    
    @Transactional
    public void updateContentStatus(Long id, Integer status) {
        Content content = contentMapper.selectById(id);
        if (content == null) {
            throw new BusinessException("内容不存在");
        }
        
        content.setStatus(status);
        contentMapper.updateById(content);
        
        log.info("更新内容状态: contentId={}, status={}", id, status);
    }
    
    /**
     * 管理员查看内容详情（不受付费限制）
     */
    public Content getContentDetailForAdmin(Long id) {
        Content content = contentMapper.selectById(id);
        
        if (content == null) {
            throw new BusinessException("内容不存在");
        }
        
        Creator creator = creatorMapper.selectById(content.getCreatorId());
        if (creator != null) {
            content.setCreatorName(creator.getPenName());
            User user = userMapper.selectById(creator.getUserId());
            if (user != null) {
                content.setCreatorAvatar(user.getAvatar());
            }
        }
        
        return content;
    }
}
