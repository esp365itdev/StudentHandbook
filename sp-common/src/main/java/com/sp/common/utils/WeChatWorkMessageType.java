package com.sp.common.utils;

/**
 * 企业微信消息类型枚举
 */
public enum WeChatWorkMessageType {
    
    /**
     * 文本消息
     */
    TEXT("text"),
    
    /**
     * 图片消息
     */
    IMAGE("image"),
    
    /**
     * 语音消息
     */
    VOICE("voice"),
    
    /**
     * 视频消息
     */
    VIDEO("video"),
    
    /**
     * 文件消息
     */
    FILE("file"),
    
    /**
     * 文本卡片消息
     */
    TEXTCARD("textcard"),
    
    /**
     * 图文消息
     */
    NEWS("news"),
    
    /**
     * 图文消息（mpnews）
     */
    MPNEWS("mpnews"),
    
    /**
     * markdown消息
     */
    MARKDOWN("markdown"),
    
    /**
     * 小程序通知消息
     */
    MINIPROGRAM_NOTICE("miniprogram_notice");

    private final String value;

    WeChatWorkMessageType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}