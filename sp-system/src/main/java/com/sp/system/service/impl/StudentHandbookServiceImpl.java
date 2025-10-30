package com.sp.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sp.system.mapper.StudentHandbookMapper;
import com.sp.system.entity.StudentHandbook;
import com.sp.system.service.IStudentHandbookService;
import com.sp.common.core.page.TableDataInfo;
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
    public TableDataInfo selectStudentHandbookList(StudentHandbook studentHandbook) {
        List<StudentHandbook> list = studentHandbookMapper.selectStudentHandbookList(studentHandbook);
        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setCode(0);
        tableDataInfo.setRows(list);
        tableDataInfo.setTotal(list.size());
        return tableDataInfo;
    }
}