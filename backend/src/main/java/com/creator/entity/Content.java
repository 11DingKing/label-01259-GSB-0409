package com.creator.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("content")
public class Content {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long creatorId;
    
    private String title;
    
    private String summary;
    
    private String content;
    
    private String coverImage;
    
    private String mediaUrl; // 视频/音频/图集的媒体URL
    
    private Integer contentType; // 1:文章 2:视频 3:音频 4:图集
    
    private Integer isPaid; // 0:免费 1:付费
    
    private BigDecimal price;
    
    private Integer viewCount;
    
    private Integer likeCount;
    
    private Integer commentCount;
    
    private Integer status; // 0:草稿 1:已发布 2:已下架
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    private Integer deleted;
    
    // 非数据库字段
    @TableField(exist = false)
    private String creatorName;
    
    @TableField(exist = false)
    private String creatorAvatar;
    
    @TableField(exist = false)
    private Boolean isLiked;
    
    @TableField(exist = false)
    private Boolean isPurchased;
}
