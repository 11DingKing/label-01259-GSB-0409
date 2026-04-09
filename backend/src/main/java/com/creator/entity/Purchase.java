package com.creator.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("purchase")
public class Purchase {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long contentId;
    
    private BigDecimal amount;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
