package com.sp.web.controller;

import com.sp.common.annotation.Anonymous;
import com.sp.common.annotation.Log;
import com.sp.common.core.controller.BaseController;
import com.sp.common.core.domain.AjaxResult;
import com.sp.common.core.page.TableDataInfo;
import com.sp.common.enums.BusinessType;
import com.sp.system.entity.ClassLog;
import com.sp.web.service.ClassLogTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学生手册Controller
 *
 */
@RestController
@RequestMapping("/system/handbook")
public class StudentHandbookController extends BaseController {
    
    @Autowired
    private ClassLogTransferService classLogTransferService;

    @Log(title = "查询课程日志列表", businessType = BusinessType.SELECT)
    @GetMapping("/list")
    public TableDataInfo list() {
        try {
            // 从外部class_log表获取数据
            List<ClassLog> classLogs = classLogTransferService.getAllClassLogsFromExternal();
            
            // 构造TableDataInfo返回
            TableDataInfo dataTable = new TableDataInfo();
            dataTable.setCode(200);
            dataTable.setRows(classLogs);
            dataTable.setTotal(classLogs.size());
            
            return dataTable;
        } catch (Exception e) {
            logger.error("获取课程日志列表失败: {}", e.getMessage());
            
            // 构造TableDataInfo返回空数据
            TableDataInfo dataTable = new TableDataInfo();
            dataTable.setCode(200);
            dataTable.setRows(java.util.Collections.emptyList());
            dataTable.setTotal(0);
            
            return dataTable;
        }
    }
    
    @Log(title = "获取课程日志详细信息", businessType = BusinessType.SELECT)
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        // 从外部class_log表获取指定ID的数据
        List<ClassLog> classLogs = classLogTransferService.getAllClassLogsFromExternal();
        ClassLog classLog = classLogs.stream()
            .filter(log -> id.equals(log.getId()))
            .findFirst()
            .orElse(null);
        
        return AjaxResult.success(classLog);
    }

}