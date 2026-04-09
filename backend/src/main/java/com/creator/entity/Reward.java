package com.creator.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("reward")
public class Reward {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long creatorId;
    
    private Long contentId;
    
    private BigDecimal amount;
    
    private String message;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    // 非数据库字段
    @TableField(exist = false)
    private String username;
    
    @TableField(exist = false)
    private String avatar;
}
