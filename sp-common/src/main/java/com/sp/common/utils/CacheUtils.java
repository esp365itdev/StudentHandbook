package com.sp.common.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cache工具类（基于内存缓存实现）
 *
 * @author spring framework
 */
public class CacheUtils {
    private static Logger logger = LoggerFactory.getLogger(CacheUtils.class);

    /**
     * 缓存容器
     */
    private static ConcurrentMap<String, Object> cacheMap = new ConcurrentHashMap<>();

    private static final String SYS_CACHE = "sys-cache";

    /**
     * 获取SYS_CACHE缓存
     *
     * @param key 键
     * @return 值
     */
    public static Object get(String key) {
        return get(SYS_CACHE, key);
    }

    /**
     * 获取SYS_CACHE缓存
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 值
     */
    public static Object get(String key, Object defaultValue) {
        Object value = get(key);
        return value != null ? value : defaultValue;
    }

    /**
     * 写入SYS_CACHE缓存
     *
     * @param key   键
     * @param value 值
     */
    public static void put(String key, Object value) {
        put(SYS_CACHE, key, value);
    }

    /**
     * 从SYS_CACHE缓存中移除
     *
     * @param key 键
     */
    public static void remove(String key) {
        remove(SYS_CACHE, key);
    }

    /**
     * 获取缓存
     *
     * @param cacheName 缓存名
     * @param key       键
     * @return 值
     */
    public static Object get(String cacheName, String key) {
        return cacheMap.get(getKey(cacheName, key));
    }

    /**
     * 获取缓存
     *
     * @param cacheName    缓存名
     * @param key          键
     * @param defaultValue 默认值
     * @return 值
     */
    public static Object get(String cacheName, String key, Object defaultValue) {
        Object value = get(cacheName, key);
        return value != null ? value : defaultValue;
    }

    /**
     * 写入缓存
     *
     * @param cacheName 缓存名
     * @param key       键
     * @param value     值
     */
    public static void put(String cacheName, String key, Object value) {
        cacheMap.put(getKey(cacheName, key), value);
    }

    /**
     * 从缓存中移除
     *
     * @param cacheName 缓存名
     * @param key       键
     */
    public static void remove(String cacheName, String key) {
        cacheMap.remove(getKey(cacheName, key));
    }

    /**
     * 从缓存中移除所有
     *
     * @param cacheName 缓存名
     */
    public static void removeAll(String cacheName) {
        cacheMap.clear();
        logger.info("清理缓存： {}", cacheName);
    }

    /**
     * 获取缓存键名
     *
     * @param cacheName 缓存名
     * @param key 键
     * @return 完整键名
     */
    private static String getKey(String cacheName, String key) {
        return cacheName + ":" + key;
    }

    /**
     * 获得缓存组中所有键名
     *
     * @return 键名数组
     */
    public static String[] getCacheNames() {
        return cacheMap.keySet().toArray(new String[0]);
    }
}