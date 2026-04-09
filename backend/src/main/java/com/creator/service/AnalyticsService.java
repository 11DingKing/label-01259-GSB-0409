package com.creator.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.creator.common.BusinessException;
import com.creator.common.PageResult;
import com.creator.entity.*;
import com.creator.mapper.*;
import com.creator.util.UserContext;
import com.creator.vo.AnalyticsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {
    
    private final CreatorMapper creatorMapper;
    private final ContentMapper contentMapper;
    private final FollowMapper followMapper;
    private final RewardMapper rewardMapper;
    private final PurchaseMapper purchaseMapper;
    private final UserMapper userMapper;
    private final OperationLogMapper operationLogMapper;
    
    public AnalyticsVO getOverview() {
        Long userId = UserContext.getUserId();
        
        Creator creator = creatorMapper.selectOne(
                new LambdaQueryWrapper<Creator>()
                        .eq(Creator::getUserId, userId)
                        .eq(Creator::getStatus, 1)
        );
        
        if (creator == null) {
            // 返回空数据而不是抛出异常
            AnalyticsVO vo = new AnalyticsVO();
            vo.setTotalIncome(BigDecimal.ZERO);
            vo.setTotalFollowers(0);
            vo.setTotalContents(0);
            vo.setTotalViews(0);
            vo.setTotalLikes(0);
            vo.setTotalComments(0);
            vo.setIncomeChart(new ArrayList<>());
            vo.setViewChart(new ArrayList<>());
            vo.setFansChart(new ArrayList<>());
            return vo;
        }
        
        AnalyticsVO vo = new AnalyticsVO();
        vo.setTotalIncome(creator.getTotalIncome());
        vo.setTotalFollowers(creator.getFollowerCount());
        vo.setTotalContents(creator.getContentCount());
        
        List<Content> contents = contentMapper.selectList(
                new LambdaQueryWrapper<Content>()
                        .eq(Content::getCreatorId, creator.getId())
        );
        
        int totalViews = 0;
        int totalLikes = 0;
        int totalComments = 0;
        for (Content c : contents) {
            totalViews += c.getViewCount();
            totalLikes += c.getLikeCount();
            totalComments += c.getCommentCount();
        }
        
        vo.setTotalViews(totalViews);
        vo.setTotalLikes(totalLikes);
        vo.setTotalComments(totalComments);
        
        vo.setIncomeChart(getIncomeChart(creator.getId()));
        vo.setViewChart(getViewChart(creator.getId()));
        vo.setFansChart(getFansChart(creator.getId()));
        
        return vo;
    }
    
    private List<AnalyticsVO.ChartData> getIncomeChart(Long creatorId) {
        List<AnalyticsVO.ChartData> data = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
            
            List<Reward> rewards = rewardMapper.selectList(
                    new LambdaQueryWrapper<Reward>()
                            .eq(Reward::getCreatorId, creatorId)
                            .ge(Reward::getCreatedAt, startOfDay)
                            .lt(Reward::getCreatedAt, endOfDay)
            );
            
            List<Purchase> purchases = purchaseMapper.selectList(
                    new LambdaQueryWrapper<Purchase>()
                            .ge(Purchase::getCreatedAt, startOfDay)
                            .lt(Purchase::getCreatedAt, endOfDay)
            );
            
            BigDecimal dayIncome = BigDecimal.ZERO;
            for (Reward r : rewards) {
                dayIncome = dayIncome.add(r.getAmount());
            }
            
            List<Content> creatorContents = contentMapper.selectList(
                    new LambdaQueryWrapper<Content>()
                            .eq(Content::getCreatorId, creatorId)
            );
            List<Long> contentIds = creatorContents.stream().map(Content::getId).toList();
            
            for (Purchase p : purchases) {
                if (contentIds.contains(p.getContentId())) {
                    dayIncome = dayIncome.add(p.getAmount());
                }
            }
            
            data.add(new AnalyticsVO.ChartData(date.format(formatter), dayIncome));
        }
        
        return data;
    }
    
    private List<AnalyticsVO.ChartData> getViewChart(Long creatorId) {
        List<AnalyticsVO.ChartData> data = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        
        List<Content> contents = contentMapper.selectList(
                new LambdaQueryWrapper<Content>()
                        .eq(Content::getCreatorId, creatorId)
        );
        
        int avgDailyViews = 0;
        if (!contents.isEmpty()) {
            int totalViews = contents.stream().mapToInt(Content::getViewCount).sum();
            avgDailyViews = totalViews / 7;
        }
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            int views = avgDailyViews + (int) (Math.random() * 50);
            data.add(new AnalyticsVO.ChartData(date.format(formatter), views));
        }
        
        return data;
    }
    
    private List<AnalyticsVO.ChartData> getFansChart(Long creatorId) {
        List<AnalyticsVO.ChartData> data = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
            
            Long newFans = followMapper.selectCount(
                    new LambdaQueryWrapper<Follow>()
                            .eq(Follow::getCreatorId, creatorId)
                            .ge(Follow::getCreatedAt, startOfDay)
                            .lt(Follow::getCreatedAt, endOfDay)
            );
            
            data.add(new AnalyticsVO.ChartData(date.format(formatter), newFans));
        }
        
        return data;
    }
    
    public Map<String, Object> getAdminStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalUsers", userMapper.selectCount(null));
        stats.put("totalCreators", creatorMapper.selectCount(
                new LambdaQueryWrapper<Creator>().eq(Creator::getStatus, 1)
        ));
        stats.put("totalContents", contentMapper.selectCount(
                new LambdaQueryWrapper<Content>().eq(Content::getStatus, 1)
        ));
        stats.put("pendingCreators", creatorMapper.selectCount(
                new LambdaQueryWrapper<Creator>().eq(Creator::getStatus, 0)
        ));
        
        List<Reward> allRewards = rewardMapper.selectList(null);
        BigDecimal totalRewards = BigDecimal.ZERO;
        for (Reward r : allRewards) {
            totalRewards = totalRewards.add(r.getAmount());
        }
        stats.put("totalRewards", totalRewards);
        
        List<Purchase> allPurchases = purchaseMapper.selectList(null);
        BigDecimal totalPurchases = BigDecimal.ZERO;
        for (Purchase p : allPurchases) {
            totalPurchases = totalPurchases.add(p.getAmount());
        }
        stats.put("totalPurchases", totalPurchases);
        
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        stats.put("newUsersThisWeek", userMapper.selectCount(
                new LambdaQueryWrapper<User>().ge(User::getCreatedAt, weekAgo)
        ));
        stats.put("newContentsThisWeek", contentMapper.selectCount(
                new LambdaQueryWrapper<Content>().ge(Content::getCreatedAt, weekAgo)
        ));
        
        return stats;
    }
    
    public PageResult<OperationLog> getOperationLogs(Integer page, Integer size, String keyword) {
        Page<OperationLog> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(OperationLog::getOperation, keyword)
                    .or()
                    .like(OperationLog::getUsername, keyword);
        }
        
        wrapper.orderByDesc(OperationLog::getCreatedAt);
        
        Page<OperationLog> result = operationLogMapper.selectPage(pageParam, wrapper);
        
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }
}
