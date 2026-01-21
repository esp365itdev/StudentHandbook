package com.sp.system.mapper;

import com.sp.system.entity.ClassLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程日志数据访问层
 */
public interface ClassLogMapper {
    /**
     * 查询所有课程日志数据
     * @return 课程日志列表
     */
    List<ClassLog> selectAllClassLogs();

    /**
     * 根据ID查询课程日志
     * @param id 课程日志ID
     * @return 课程日志信息
     */
    ClassLog selectClassLogById(@Param("id") String id);
    
    /**
     * 根据ID查询课程日志列表（用于处理可能的重复记录）
     * @param id 课程日志ID
     * @return 课程日志列表
     */
    List<ClassLog> selectClassLogsById(@Param("id") String id);
    
    /**
     * 根据ID列表批量查询课程日志
     * @param ids 课程日志ID列表
     * @return 课程日志列表
     */
    List<ClassLog> selectClassLogsByIds(@Param("ids") List<String> ids);
    
    /**
     * 插入课程日志数据
     * @param classLog 课程日志信息
     * @return 影响行数
     */
    int insertClassLog(ClassLog classLog);
    
    /**
     * 更新课程日志数据
     * @param classLog 课程日志信息
     * @return 影响行数
     */
    int updateClassLogById(ClassLog classLog);
    
    /**
     * 查询过去一个月的课程日志数据
     * @return 课程日志列表
     */
    List<ClassLog> selectPastMonthClassLogs();
}