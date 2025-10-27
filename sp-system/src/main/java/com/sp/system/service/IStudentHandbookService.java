package com.sp.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sp.system.entity.StudentHandbook;

import java.util.List;

/**
 * 学生手册Service接口
 *
 */
public interface IStudentHandbookService extends IService<StudentHandbook> {
    /**
     * 查询学生手册列表
     *
     * @param studentHandbook 学生手册信息
     * @return 学生手册集合
     */
    List<StudentHandbook> selectStudentHandbookList(StudentHandbook studentHandbook);
}