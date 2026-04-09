package com.creator.controller;

import com.creator.common.Result;
import com.creator.service.AnalyticsService;
import com.creator.vo.AnalyticsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    
    private final AnalyticsService analyticsService;
    
    @GetMapping("/overview")
    public Result<AnalyticsVO> getOverview() {
        return Result.success(analyticsService.getOverview());
    }
}
