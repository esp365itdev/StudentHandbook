package com.sp.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sp.system.entity.Token;
import org.apache.ibatis.annotations.Param;

/**
 * TokenMapper接口
 *
 */
public interface TokenMapper extends BaseMapper<Token> {
    /**
     * 根据token值查询Token信息
     *
     * @param tokenValue token值
     * @return Token信息
     */
    Token selectByTokenValue(@Param("tokenValue") String tokenValue);

    /**
     * 根据用户ID删除Token
     *
     * @param userId 用户ID
     * @return 删除记录数
     */
    int deleteByUserId(@Param("userId") Long userId);
    
    /**
     * 插入Token信息
     * 
     * @param token Token信息
     * @return 插入记录数
     */
    int insertToken(Token token);
}