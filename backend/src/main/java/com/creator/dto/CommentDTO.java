package com.creator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentDTO {
    @NotNull(message = "内容ID不能为空")
    private Long contentId;
    
    private Long parentId = 0L;
    
    @NotBlank(message = "评论内容不能为空")
    private String commentText;
}
