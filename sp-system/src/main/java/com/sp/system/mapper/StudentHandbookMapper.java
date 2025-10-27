package com.sp.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sp.system.entity.StudentHandbook;

import java.util.List;

/**
 * 学生手册Mapper接口
 *
 */
public interface StudentHandbookMapper extends BaseMapper<StudentHandbook> {
    /**
     * 查询学生手册列表
     *
     * @param studentHandbook 学生手册信息
     * @return 学生手册集合
     */
    List<StudentHandbook> selectStudentHandbookList(StudentHandbook studentHandbook);
}