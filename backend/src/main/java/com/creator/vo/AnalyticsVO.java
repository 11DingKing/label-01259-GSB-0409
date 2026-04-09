package com.creator.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class AnalyticsVO {
    private BigDecimal totalIncome;
    private Integer totalViews;
    private Integer totalLikes;
    private Integer totalFollowers;
    private Integer totalContents;
    private Integer totalComments;
    
    private List<ChartData> incomeChart;
    private List<ChartData> viewChart;
    private List<ChartData> fansChart;
    
    @Data
    public static class ChartData {
        private String date;
        private Object value;
        
        public ChartData(String date, Object value) {
            this.date = date;
            this.value = value;
        }
    }
}
