package com.sp.system.service.impl;

import com.sp.common.core.page.TableDataInfo;
import com.sp.system.entity.ClassLog;
import com.sp.system.mapper.ClassLogMapper;
import com.sp.system.service.IClassLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 课程日志Service业务层处理
 */
@Service
public class ClassLogServiceImpl implements IClassLogService {

    @Autowired
    private ClassLogMapper classLogMapper;

    /**
     * 查询课程日志列表
     *
     * @param classLog 课程日志信息
     * @return 课程日志集合
     */
    @Override
    public TableDataInfo selectClassLogList(ClassLog classLog) {
        List<ClassLog> classLogs = classLogMapper.selectAllClassLogs();
        TableDataInfo dataTable = new TableDataInfo();
        dataTable.setCode(200);
        dataTable.setRows(classLogs);
        dataTable.setTotal(classLogs.size());
        return dataTable;
    }

    /**
     * 根据ID获取课程日志信息
     *
     * @param id 课程日志ID
     * @return 课程日志信息
     */
    @Override
    public ClassLog getClassLogById(String id) {
        return classLogMapper.selectClassLogById(id);
    }

    /**
     * 查询所有课程日志
     *
     * @return 课程日志列表
     */
    @Override
    public List<ClassLog> selectAllClassLogs() {
        return classLogMapper.selectAllClassLogs();
    }
}