package com.creator.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.creator.entity.Creator;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

@Mapper
public interface CreatorMapper extends BaseMapper<Creator> {
    
    @Update("UPDATE creator SET total_income = total_income + #{amount}, version = version + 1 " +
            "WHERE id = #{creatorId}")
    int incrementTotalIncome(@Param("creatorId") Long creatorId, @Param("amount") BigDecimal amount);
}
