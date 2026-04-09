package com.creator.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.creator.common.BusinessException;
import com.creator.common.PageResult;
import com.creator.dto.CreatorApplyDTO;
import com.creator.entity.Content;
import com.creator.entity.Creator;
import com.creator.entity.Follow;
import com.creator.entity.User;
import com.creator.mapper.ContentMapper;
import com.creator.mapper.CreatorMapper;
import com.creator.mapper.FollowMapper;
import com.creator.mapper.UserMapper;
import com.creator.util.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreatorService {
    
    private final CreatorMapper creatorMapper;
    private final UserMapper userMapper;
    private final FollowMapper followMapper;
    private final ContentMapper contentMapper;
    
    @Transactional
    public void apply(CreatorApplyDTO dto) {
        Long userId = UserContext.getUserId();
        
        if (userId == null) {
            throw new BusinessException("用户未登录，请先登录");
        }
        
        Long count = creatorMapper.selectCount(
                new LambdaQueryWrapper<Creator>()
                        .eq(Creator::getUserId, userId)
                        .ne(Creator::getStatus, 2)
        );
        
        if (count > 0) {
            throw new BusinessException("您已提交过申请，请勿重复提交");
        }
        
        Creator creator = new Creator();
        creator.setUserId(userId);
        creator.setPenName(dto.getPenName());
        creator.setBio(dto.getBio());
        creator.setCategory(dto.getCategory());
        creator.setCoverImage(dto.getCoverImage());
        creator.setStatus(0);
        creator.setFollowerCount(0);
        creator.setContentCount(0);
        creator.setTotalIncome(BigDecimal.ZERO);
        
        creatorMapper.insert(creator);
        log.info("用户申请成为创作者: userId={}, penName={}", userId, dto.getPenName());
    }
    
    public Creator getMyCreatorProfile() {
        try {
            Long userId = UserContext.getUserId();
            log.info("获取创作者信息, userId={}", userId);
            
            // 如果用户未登录，返回null而不是抛出异常
            if (userId == null) {
                log.info("用户未登录，返回null");
                return null;
            }
            
            // 查询该用户的创作者记录（包括所有状态：待审核、已通过、已拒绝）
            // 按创建时间倒序，取最新的一条
            List<Creator> creators = creatorMapper.selectList(
                    new LambdaQueryWrapper<Creator>()
                            .eq(Creator::getUserId, userId)
                            .orderByDesc(Creator::getCreatedAt)
            );
            
            log.info("查询结果: creators.size={}", creators.size());
            
            if (creators.isEmpty()) {
                return null;
            }
            
            Creator creator = creators.get(0);
            log.info("返回创作者: id={}, status={}", creator.getId(), creator.getStatus());
            
            User user = userMapper.selectById(userId);
            if (user != null) {
                creator.setUsername(user.getUsername());
                creator.setAvatar(user.getAvatar());
            }
            
            return creator;
        } catch (Exception e) {
            log.error("获取创作者信息失败", e);
            return null;
        }
    }
    
    @Transactional
    public void updateProfile(Creator creator) {
        Long userId = UserContext.getUserId();
        
        Creator existCreator = creatorMapper.selectOne(
                new LambdaQueryWrapper<Creator>()
                        .eq(Creator::getUserId, userId)
        );
        
        if (existCreator == null) {
            throw new BusinessException("创作者信息不存在");
        }
        
        existCreator.setPenName(creator.getPenName());
        existCreator.setBio(creator.getBio());
        existCreator.setCategory(creator.getCategory());
        existCreator.setCoverImage(creator.getCoverImage());
        
        creatorMapper.updateById(existCreator);
        log.info("创作者更新资料: {}", existCreator.getPenName());
    }
    
    public PageResult<Creator> getCreatorList(Integer page, Integer size, String category, String keyword) {
        Page<Creator> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Creator> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(Creator::getStatus, 1);
        
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Creator::getCategory, category);
        }
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Creator::getPenName, keyword);
        }
        
        wrapper.orderByDesc(Creator::getFollowerCount);
        
        Page<Creator> result = creatorMapper.selectPage(pageParam, wrapper);
        
        Long currentUserId = UserContext.getUserId();
        for (Creator c : result.getRecords()) {
            User user = userMapper.selectById(c.getUserId());
            if (user != null) {
                c.setAvatar(user.getAvatar());
            }
            
            // 计算创作者所有内容的总点赞数
            List<Content> contents = contentMapper.selectList(
                    new LambdaQueryWrapper<Content>()
                            .eq(Content::getCreatorId, c.getId())
            );
            int totalLikes = contents.stream().mapToInt(Content::getLikeCount).sum();
            c.setTotalLikes(totalLikes);
            
            if (currentUserId != null) {
                Long followCount = followMapper.selectCount(
                        new LambdaQueryWrapper<Follow>()
                                .eq(Follow::getUserId, currentUserId)
                                .eq(Follow::getCreatorId, c.getId())
                );
                c.setIsFollowed(followCount > 0);
            }
        }
        
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }
    
    public Creator getCreatorDetail(Long id) {
        Creator creator = creatorMapper.selectById(id);
        
        if (creator == null || creator.getStatus() != 1) {
            throw new BusinessException("创作者不存在");
        }
        
        User user = userMapper.selectById(creator.getUserId());
        if (user != null) {
            creator.setAvatar(user.getAvatar());
        }
        
        // 计算创作者所有内容的总点赞数
        List<Content> contents = contentMapper.selectList(
                new LambdaQueryWrapper<Content>()
                        .eq(Content::getCreatorId, id)
        );
        int totalLikes = contents.stream().mapToInt(Content::getLikeCount).sum();
        creator.setTotalLikes(totalLikes);
        
        Long currentUserId = UserContext.getUserId();
        if (currentUserId != null) {
            Long followCount = followMapper.selectCount(
                    new LambdaQueryWrapper<Follow>()
                            .eq(Follow::getUserId, currentUserId)
                            .eq(Follow::getCreatorId, id)
            );
            creator.setIsFollowed(followCount > 0);
        }
        
        return creator;
    }
    
    public PageResult<Creator> getPendingCreators(Integer page, Integer size) {
        Page<Creator> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Creator> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(Creator::getStatus, 0)
                .orderByAsc(Creator::getCreatedAt);
        
        Page<Creator> result = creatorMapper.selectPage(pageParam, wrapper);
        
        for (Creator c : result.getRecords()) {
            User user = userMapper.selectById(c.getUserId());
            if (user != null) {
                c.setUsername(user.getUsername());
                c.setAvatar(user.getAvatar());
            }
        }
        
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }
    
    @Transactional
    public void auditCreator(Long id, Integer status) {
        Creator creator = creatorMapper.selectById(id);
        
        if (creator == null) {
            throw new BusinessException("创作者申请不存在");
        }
        
        creator.setStatus(status);
        creatorMapper.updateById(creator);
        
        if (status == 1) {
            User user = userMapper.selectById(creator.getUserId());
            user.setRole(1);
            userMapper.updateById(user);
        }
        
        log.info("审核创作者: id={}, status={}", id, status);
    }
    
    public List<String> getCategories() {
        return List.of("科技", "生活", "娱乐", "教育", "财经", "健康", "旅行", "美食", "时尚", "体育");
    }
}
