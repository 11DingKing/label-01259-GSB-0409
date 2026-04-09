package com.creator.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatorApplyDTO {
    @NotBlank(message = "笔名不能为空")
    private String penName;
    
    private String bio;
    
    @NotBlank(message = "创作领域不能为空")
    private String category;
    
    private String coverImage;
}
