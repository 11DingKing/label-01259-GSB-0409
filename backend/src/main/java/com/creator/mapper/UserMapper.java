package com.creator.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.creator.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * CAS 更新用户余额
     * @param userId 用户ID
     * @param amount 扣减金额（正数）
     * @param oldVersion 旧版本号
     * @return 影响行数，0表示更新失败（版本号冲突）
     */
    @Update("UPDATE sys_user SET balance = balance - #{amount}, version = version + 1 " +
            "WHERE id = #{userId} AND version = #{oldVersion} AND balance >= #{amount}")
    int deductBalanceWithVersion(@Param("userId") Long userId,
                                  @Param("amount") BigDecimal amount,
                                  @Param("oldVersion") Integer oldVersion);

    /**
     * CAS 增加用户余额
     * @param userId 用户ID
     * @param amount 增加金额（正数）
     * @param oldVersion 旧版本号
     * @return 影响行数，0表示更新失败（版本号冲突）
     */
    @Update("UPDATE sys_user SET balance = balance + #{amount}, version = version + 1 " +
            "WHERE id = #{userId} AND version = #{oldVersion}")
    int addBalanceWithVersion(@Param("userId") Long userId,
                               @Param("amount") BigDecimal amount,
                               @Param("oldVersion") Integer oldVersion);
}
