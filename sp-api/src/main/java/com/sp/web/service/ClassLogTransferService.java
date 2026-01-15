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

        // 逐个处理每条记录
        for (ClassLog classLog : classLogs) {
            // 尝试根据ID查询现有记录
            ClassLog existingLog = classLogMapper.selectClassLogById(classLog.getId());
            
            if (existingLog != null) {
                // 如果记录存在，则更新
                updateClassLog(classLog);
            } else {
                // 如果记录不存在，则插入
                insertClassLog(classLog);
            }
        }

        logger.info("成功传输 {} 条课程日志数据到目标数据库", classLogs.size());
    }
    
    private void insertClassLog(ClassLog classLog) {
        classLogMapper.insertClassLog(classLog);
    }
    
    private void updateClassLog(ClassLog classLog) {
        classLogMapper.updateClassLogById(classLog);
    }
}