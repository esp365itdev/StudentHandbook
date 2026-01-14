package com.sp.system.service;

import com.sp.system.entity.Department;
import java.util.List;

/**
 * 部门业务逻辑接口
 */
public interface DepartmentService {

    /**
     * 保存部门信息
     * @param department 部门信息
     */
    void saveDepartment(Department department);

    /**
     * 根据部门ID查询部门信息
     * @param id 部门ID
     * @return 部门信息
     */
    Department findById(Long id);

    /**
     * 查询所有部门信息
     * @return 部门列表
     */
    List<Department> findAll();

    /**
     * 根据父部门ID查询子部门
     * @param parentId 父部门ID
     * @return 子部门列表
     */
    List<Department> findByParentId(Integer parentId);

    /**
     * 更新部门信息
     * @param department 部门信息
     */
    void updateDepartment(Department department);

    /**
     * 删除部门信息
     * @param id 部门ID
     */
    void deleteById(Long id);

    /**
     * 批量保存部门信息
     * @param departments 部门列表
     */
    void batchSaveDepartments(List<Department> departments);
    
    /**
     * 判断部门是否存在
     * @param id 部门ID
     * @return 是否存在
     */
    boolean existsById(Long id);

}