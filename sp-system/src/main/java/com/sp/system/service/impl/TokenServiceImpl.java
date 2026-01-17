package com.sp.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sp.system.entity.Token;
import com.sp.system.mapper.TokenMapper;
import com.sp.system.service.TokenService;
import com.sp.common.utils.uuid.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * TokenService实现类
 *
 */
@Service
public class TokenServiceImpl extends ServiceImpl<TokenMapper, Token> implements TokenService {

    @Autowired
    private TokenMapper tokenMapper;
    
    @Value("${sp.token.expireTime:7}")
    private int expireTimeInDays;

    @Override
    public boolean validateToken(String tokenValue) {
        if (tokenValue == null || tokenValue.isEmpty()) {
            return false;
        }

        Token token = this.baseMapper.selectByTokenValue(tokenValue);
        if (token == null) {
            return false;
        }

        // 检查token是否过期
        if (token.getExpireTime().isBefore(LocalDateTime.now())) {
            // 删除过期token
            this.baseMapper.deleteById(token.getId());
            return false;
        }

        return true;
    }

    @Override
    public String createToken(Long userId) {
        // 先删除该用户之前的token
        this.tokenMapper.deleteByUserId(userId);

        // 创建新token
        String tokenValue = UUID.randomUUID().toString();

        Token token = new Token();
        token.setUserId(userId);
        token.setToken(tokenValue);
        token.setCreateTime(LocalDateTime.now());
        token.setUpdateTime(LocalDateTime.now());
        token.setExpireTime(LocalDateTime.now().plusDays(expireTimeInDays));

        // 使用自定义的insertToken方法
        this.tokenMapper.insertToken(token);

        return tokenValue;
    }

    @Override
    public boolean removeTokenByUserId(Long userId) {
        return this.tokenMapper.deleteByUserId(userId) > 0;
    }
    
    /**
     * 为家长用户创建token
     *
     * @param userId 用户ID
     * @param parentUserId 家长用户ID
     * @return token值
     */
    public String createTokenWithParentUserId(Long userId, String parentUserId) {
        // 先删除该用户之前的token
        this.tokenMapper.deleteByUserId(userId);

        // 创建新token
        String tokenValue = UUID.randomUUID().toString();

        Token token = new Token();
        token.setUserId(userId);
        token.setParentUserId(parentUserId);
        token.setToken(tokenValue);
        token.setCreateTime(LocalDateTime.now());
        token.setUpdateTime(LocalDateTime.now());
        token.setExpireTime(LocalDateTime.now().plusDays(expireTimeInDays));

        // 使用自定义的insertToken方法
        this.tokenMapper.insertToken(token);

        return tokenValue;
    }
}