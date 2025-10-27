package com.sp.web.controller;

import com.sp.common.annotation.Anonymous;
import com.sp.common.annotation.Log;
import com.sp.common.core.controller.BaseController;
import com.sp.common.core.domain.AjaxResult;
import com.sp.common.core.page.TableDataInfo;
import com.sp.common.enums.BusinessType;
import com.sp.system.entity.StudentHandbook;
import com.sp.system.service.IStudentHandbookService;
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
    private IStudentHandbookService studentHandbookService;

    @Anonymous
    @Log(title = "查询学生手册列表", businessType = BusinessType.SELECT)
    @GetMapping("/list")
    public TableDataInfo list(StudentHandbook studentHandbook) {
        // 不使用分页，直接返回所有数据
        List<StudentHandbook> list = studentHandbookService.selectStudentHandbookList(studentHandbook);
        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setCode(0);
        tableDataInfo.setRows(list);
        tableDataInfo.setTotal(list.size());
        return tableDataInfo;
    }

    @Anonymous
    @Log(title = "获取学生手册详细信息", businessType = BusinessType.SELECT)
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(studentHandbookService.getById(id));
    }

}