package com.creator.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("comment")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long contentId;
    
    private Long userId;
    
    private Long parentId;
    
    private String commentText;
    
    private Integer likeCount;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableLogic
    private Integer deleted;
    
    // 非数据库字段
    @TableField(exist = false)
    private String username;
    
    @TableField(exist = false)
    private String avatar;
    
    @TableField(exist = false)
    private List<Comment> replies;
}
