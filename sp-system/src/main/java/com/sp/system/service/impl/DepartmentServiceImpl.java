package com.sp.system.service.impl;

import com.sp.system.entity.Department;
import com.sp.system.mapper.DepartmentMapper;
import com.sp.system.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 部门业务逻辑实现类
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentMapper departmentMapper;
    

    @Override
    public void saveDepartment(Department department) {
        // 检查是否已存在该部门，如果存在则更新，否则插入
        Department existingDepartment = departmentMapper.selectById(department.getId());
        if (existingDepartment != null) {
            departmentMapper.updateDepartment(department);
        } else {
            departmentMapper.insertDepartment(department);
        }
    }

    @Override
    public Department findById(Long id) {
        return departmentMapper.selectById(id);
    }

    @Override
    public List<Department> findAll() {
        return departmentMapper.selectAll();
    }

    @Override
    public List<Department> findByParentId(Integer parentId) {
        return departmentMapper.selectByParentId(parentId);
    }

    @Override
    public void updateDepartment(Department department) {
        departmentMapper.updateDepartment(department);
    }

    @Override
    public void deleteById(Long id) {
        departmentMapper.deleteById(id);
    }

    @Override
    public void batchSaveDepartments(List<Department> departments) {
        if (departments != null && !departments.isEmpty()) {
            // 批量插入或更新部门信息（根据ID判断存在与否）
            departmentMapper.batchInsertDepartments(departments);
        }
    }
    
    @Override
    public boolean existsById(Long id) {
        return departmentMapper.countById(id) > 0;
    }

}