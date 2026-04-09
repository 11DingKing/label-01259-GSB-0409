package com.creator.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.creator.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    @Update("UPDATE sys_user SET balance = balance - #{amount}, version = version + 1 " +
            "WHERE id = #{userId} AND balance >= #{amount} AND version = #{version}")
    int decreaseBalanceWithVersion(@Param("userId") Long userId,
                                   @Param("amount") BigDecimal amount,
                                   @Param("version") Integer version);
    
    @Update("UPDATE sys_user SET balance = balance + #{amount}, version = version + 1 " +
            "WHERE id = #{userId} AND version = #{version}")
    int increaseBalanceWithVersion(@Param("userId") Long userId,
                                   @Param("amount") BigDecimal amount,
                                   @Param("version") Integer version);
    
    @Update("UPDATE sys_user SET balance = balance + #{amount} WHERE id = #{userId}")
    int increaseBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);
}
