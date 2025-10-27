package com.sp.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sp.system.mapper.StudentHandbookMapper;
import com.sp.system.entity.StudentHandbook;
import com.sp.system.service.IStudentHandbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 学生手册Service业务层处理
 *
 */
@Service
public class StudentHandbookServiceImpl extends ServiceImpl<StudentHandbookMapper, StudentHandbook> implements IStudentHandbookService {

    @Autowired
    private StudentHandbookMapper studentHandbookMapper;

    /**
     * 查询学生手册列表
     *
     * @param studentHandbook 学生手册信息
     * @return 学生手册集合
     */
    @Override
    public List<StudentHandbook> selectStudentHandbookList(StudentHandbook studentHandbook) {
        return studentHandbookMapper.selectStudentHandbookList(studentHandbook);
    }
}