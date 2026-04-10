package com.creator.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.creator.entity.Creator;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

@Mapper
public interface CreatorMapper extends BaseMapper<Creator> {

    /**
     * 增量更新创作者收益
     * @param creatorId 创作者ID
     * @param amount 增加的收益金额
     * @return 影响行数
     */
    @Update("UPDATE creator SET total_income = total_income + #{amount} WHERE id = #{creatorId}")
    int incrementTotalIncome(@Param("creatorId") Long creatorId, @Param("amount") BigDecimal amount);
}
