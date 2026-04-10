package com.creator.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("creator")
public class Creator {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String penName;
    
    private String bio;
    
    private String category;
    
    private String coverImage;
    
    private Integer status; // 0:待审核 1:已通过 2:已拒绝
    
    private Integer followerCount;
    
    private Integer contentCount;
    
    private BigDecimal totalIncome;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    private Integer deleted;
    
    @Version
    private Integer version;
    
    // 非数据库字段
    @TableField(exist = false)
    private String username;
    
    @TableField(exist = false)
    private String avatar;
    
    @TableField(exist = false)
    private Boolean isFollowed;
    
    @TableField(exist = false)
    private Integer totalLikes;
}
