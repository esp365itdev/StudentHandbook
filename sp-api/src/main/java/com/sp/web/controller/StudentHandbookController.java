package com.sp.web.controller;

import com.sp.common.annotation.Log;
import com.sp.common.core.controller.BaseController;
import com.sp.common.core.domain.AjaxResult;
import com.sp.common.core.page.TableDataInfo;
import com.sp.common.enums.BusinessType;
import com.sp.system.entity.StudentHandbook;
import com.sp.system.service.IStudentHandbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 学生手册Controller
 *
 */
@RestController
@RequestMapping("/system/handbook")
public class StudentHandbookController extends BaseController {
    
    @Autowired
    private IStudentHandbookService studentHandbookService;

    @Log(title = "查询学生手册列表", businessType = BusinessType.SELECT)
    @GetMapping("/list")
    public TableDataInfo list(StudentHandbook studentHandbook) {
        return studentHandbookService.selectStudentHandbookList(studentHandbook);
    }
    
    @Log(title = "获取学生手册详细信息", businessType = BusinessType.SELECT)
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(studentHandbookService.getById(id));
    }

}