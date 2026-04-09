package com.creator.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("follow")
public class Follow {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long creatorId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
