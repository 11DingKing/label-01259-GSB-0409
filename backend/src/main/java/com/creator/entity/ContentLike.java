package com.creator.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("content_like")
public class ContentLike {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long contentId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
