package com.creator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ContentDTO {
    private Long id;
    
    @NotBlank(message = "标题不能为空")
    private String title;
    
    private String summary;
    
    @NotBlank(message = "内容不能为空")
    private String content;
    
    private String coverImage;
    
    private String mediaUrl; // 视频/音频/图集的媒体URL
    
    @NotNull(message = "内容类型不能为空")
    private Integer contentType;
    
    private Integer isPaid = 0;
    
    private BigDecimal price = BigDecimal.ZERO;
    
    private Integer status = 1;
}
