package com.sp.system.service;

/**
 * TokenService接口
 *
 */
public interface TokenService {
    /**
     * 验证token是否有效
     *
     * @param tokenValue token值
     * @return 是否有效
     */
    boolean validateToken(String tokenValue);

    /**
     * 创建新的token
     *
     * @param userId 用户ID
     * @return token值
     */
    String createToken(Long userId);

    /**
     * 删除用户的token
     *
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean removeTokenByUserId(Long userId);
    
    /**
     * 为家长用户创建token
     *
     * @param userId 用户ID
     * @param parentUserId 家长用户ID
     * @return token值
     */
    String createTokenWithParentUserId(Long userId, String parentUserId);
    
    /**
     * 根据token值获取家长用户ID
     *
     * @param tokenValue token值
     * @return 家长用户ID
     */
    String getParentUserIdByToken(String tokenValue);
    
    /**
     * 根据token值获取用户ID
     *
     * @param tokenValue token值
     * @return 用户ID
     */
    Long getUserIdByToken(String tokenValue);
}