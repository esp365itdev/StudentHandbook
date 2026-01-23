package com.sp.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sp.system.entity.DepartmentParentBinding;
import com.sp.system.mapper.DepartmentParentBindingMapper;
import com.sp.system.service.DepartmentParentBindingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 部门家长绑定服务层实现类
 *
 */
@Service
public class DepartmentParentBindingServiceImpl extends ServiceImpl<DepartmentParentBindingMapper, DepartmentParentBinding> implements DepartmentParentBindingService {

    @Autowired
    private DepartmentParentBindingMapper departmentParentBindingMapper;

    @Override
    public int insert(DepartmentParentBinding binding) {
        return departmentParentBindingMapper.insert(binding);
    }

    @Override
    public boolean updateById(DepartmentParentBinding binding) {
        return departmentParentBindingMapper.updateById(binding) > 0;
    }

    @Override
    public int deleteById(Long id) {
        return departmentParentBindingMapper.deleteById(id);
    }

    @Override
    public int deleteByDepartmentId(Long departmentId) {
        return departmentParentBindingMapper.deleteByDepartmentId(departmentId);
    }

    @Override
    public boolean insertIfNotExists(DepartmentParentBinding binding) {
        // 使用 INSERT IGNORE 来避免重复键错误
        departmentParentBindingMapper.insertIgnore(binding);
        // INSERT IGNORE 如果记录已存在则不会插入，影响行数为0；如果插入新记录则影响行数为1
        return true; // 总是返回true，因为无论是否实际插入，操作都是成功的
    }
}