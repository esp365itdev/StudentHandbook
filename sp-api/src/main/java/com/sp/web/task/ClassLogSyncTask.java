package com.sp.web.task;

import com.sp.system.entity.ClassLog;
import com.sp.web.service.ClassLogTransferService;
import com.sp.web.service.ExternalClassLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 课程日志数据同步定时任务
 * 每天下午5点30分同步外部服务器的class_log数据
 */
@Component
public class ClassLogSyncTask {

    private static final Logger logger = LoggerFactory.getLogger(ClassLogSyncTask.class);

    @Autowired
    private ExternalClassLogService externalClassLogService;
    
    @Autowired
    private ClassLogTransferService classLogTransferService;

    /**
     * 每天下午5点40分执行课程日志数据同步（北京时间）
     */
    @Scheduled(cron = "0 40 17 ? * MON-FRI", zone = "Asia/Shanghai")
    public void syncClassLogData() {
        logger.info("开始执行课程日志数据同步任务");

        try {
            // 从外部数据库获取所有课程日志数据
            List<ClassLog> classLogs = externalClassLogService.getAllClassLogsFromExternal();
            
            if (classLogs != null && !classLogs.isEmpty()) {
                logger.info("从外部数据库获取到 {} 条课程日志数据", classLogs.size());

                // 将数据传输到目标数据库
                classLogTransferService.batchUpsertClassLogs(classLogs);

                logger.info("课程日志数据同步任务完成");
            } else {
                logger.info("外部数据库中没有课程日志数据需要同步");
            }
        } catch (Exception e) {
            logger.error("同步课程日志数据失败", e);
        }

        logger.info("课程日志数据同步任务执行完成");
    }
}