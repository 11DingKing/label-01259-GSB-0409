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
            "WHERE id = #{userId} AND version = #{version} AND balance >= #{amount}")
    int updateBalanceWithCAS(@Param("userId") Long userId, @Param("amount") BigDecimal amount, @Param("version") Integer version);
    
    @Update("UPDATE sys_user SET balance = balance + #{amount}, version = version + 1 " +
            "WHERE id = #{userId} AND version = #{version}")
    int addBalanceWithCAS(@Param("userId") Long userId, @Param("amount") BigDecimal amount, @Param("version") Integer version);
}
