package com.creator.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class LoginVO {
    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private String email;
    private BigDecimal balance;
    private Integer role;
    private String token;
    private Long creatorId;
}
