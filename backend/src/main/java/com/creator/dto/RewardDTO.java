package com.creator.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class RewardDTO {
    @NotNull(message = "创作者ID不能为空")
    private Long creatorId;
    
    private Long contentId;
    
    @NotNull(message = "打赏金额不能为空")
    @DecimalMin(value = "0.01", message = "打赏金额最少0.01元")
    private BigDecimal amount;
    
    private String message;
}
