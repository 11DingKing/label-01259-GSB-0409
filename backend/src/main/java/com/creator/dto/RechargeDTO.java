package com.creator.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class RechargeDTO {
    @NotNull(message = "充值金额不能为空")
    @DecimalMin(value = "1", message = "充值金额最少1元")
    private BigDecimal amount;
}
