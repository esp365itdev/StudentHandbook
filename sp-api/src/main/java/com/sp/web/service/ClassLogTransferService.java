package com.sp.web.service;

import com.sp.system.entity.ClassLog;
import com.sp.system.mapper.ClassLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 课程日志数据服务
 */
@Service
public class ClassLogTransferService {

    private static final Logger logger = LoggerFactory.getLogger(ClassLogTransferService.class);

    @Autowired
    private ClassLogMapper classLogMapper;

    /**
     * 从本地数据库获取所有ClassLog数据
     */
    public List<ClassLog> getAllClassLogsFromExternal() {
        try {
            return classLogMapper.selectAllClassLogs();
        } catch (Exception e) {
            logger.error("从本地数据库获取课程日志数据失败: {}", e.getMessage());
            // 返回空列表而不是抛出异常，以防止前端页面因后端错误而空白
            return java.util.Collections.emptyList();
        }
    }

    /**
     * 批量插入或更新ClassLog数据到目标数据库
     */
    public void batchUpsertClassLogs(List<ClassLog> classLogs) {
        if (classLogs == null || classLogs.isEmpty()) {
            logger.info("没有需要传输的课程日志数据");
            return;
        }

        // 提取所有ID
        List<String> ids = classLogs.stream()
                .map(ClassLog::getId)
                .filter(id -> id != null && !id.trim().isEmpty())
                .distinct()
                .collect(java.util.stream.Collectors.toList());

        if (ids.isEmpty()) {
            logger.info("没有有效的课程日志ID需要处理");
            return;
        }

        // 批量查询现有记录
        List<ClassLog> existingLogs = classLogMapper.selectClassLogsByIds(ids);

        // 将现有记录放入Map便于快速查找
        java.util.Map<String, ClassLog> existingLogsMap = new java.util.HashMap<>();
        for (ClassLog existingLog : existingLogs) {
            if (existingLog != null && existingLog.getId() != null) {
                existingLogsMap.put(existingLog.getId(), existingLog);
            }
        }

        // 分离需要插入和更新的记录
        List<ClassLog> toInsert = new java.util.ArrayList<>();
        List<ClassLog> toUpdate = new java.util.ArrayList<>();

        for (ClassLog classLog : classLogs) {
            if (classLog.getId() == null || classLog.getId().trim().isEmpty()) {
                continue; // 跳过ID为空的记录
            }

            if (existingLogsMap.containsKey(classLog.getId())) {
                // 记录已存在，需要更新
                toUpdate.add(classLog);
            } else {
                // 记录不存在，需要插入
                toInsert.add(classLog);
            }
        }

        // 批量插入新记录
        if (!toInsert.isEmpty()) {
            for (ClassLog classLog : toInsert) {
                try {
                    insertClassLog(classLog);
                } catch (Exception e) {
                    logger.error("插入课程日志数据时发生错误，ID: {}，错误: {}", classLog.getId(), e.getMessage());
                }
            }
            logger.info("成功插入 {} 条新课程日志数据", toInsert.size());
        }

        // 批量更新现有记录
        if (!toUpdate.isEmpty()) {
            for (ClassLog classLog : toUpdate) {
                try {
                    updateClassLog(classLog);
                } catch (Exception e) {
                    logger.error("更新课程日志数据时发生错误，ID: {}，错误: {}", classLog.getId(), e.getMessage());
                }
            }
            logger.info("成功更新 {} 条课程日志数据", toUpdate.size());
        }

        logger.info("成功传输 {} 条课程日志数据到目标数据库 ({} 条插入, {} 条更新)",
                classLogs.size(), toInsert.size(), toUpdate.size());
    }

    private void insertClassLog(ClassLog classLog) {
        classLogMapper.insertClassLog(classLog);
    }

    private void updateClassLog(ClassLog classLog) {
        classLogMapper.updateClassLogById(classLog);
    }
}