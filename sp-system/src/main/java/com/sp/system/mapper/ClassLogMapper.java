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
}