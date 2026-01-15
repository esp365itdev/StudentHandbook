package com.sp.system.service;

import com.sp.system.entity.ClassLog;
import com.sp.common.core.page.TableDataInfo;

import java.util.List;

/**
 * 课程日志Service接口
 */
public interface IClassLogService {
    /**
     * 查询课程日志列表
     *
     * @param classLog 课程日志信息
     * @return 课程日志集合
     */
    TableDataInfo selectClassLogList(ClassLog classLog);

    /**
     * 根据ID获取课程日志信息
     *
     * @param id 课程日志ID
     * @return 课程日志信息
     */
    ClassLog getClassLogById(String id);

    /**
     * 查询所有课程日志
     *
     * @return 课程日志列表
     */
    List<ClassLog> selectAllClassLogs();
}