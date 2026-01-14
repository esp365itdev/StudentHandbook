package com.sp.system.mapper;

import com.sp.system.entity.Department;
import java.util.List;

/**
 * 部门数据访问层
 */
public interface DepartmentMapper {

    /**
     * 插入部门信息
     * @param department 部门信息
     */
    void insertDepartment(Department department);

    /**
     * 根据部门ID查询部门信息
     * @param id 部门ID
     * @return 部门信息
     */
    Department selectById(Long id);

    /**
     * 查询所有部门信息
     * @return 部门列表
     */
    List<Department> selectAll();

    /**
     * 根据父部门ID查询子部门
     * @param parentId 父部门ID
     * @return 子部门列表
     */
    List<Department> selectByParentId(Integer parentId);

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
     * 批量插入部门信息
     * @param departments 部门列表
     */
    void batchInsertDepartments(List<Department> departments);
    
    /**
     * 根据部门ID判断是否存在
     * @param id 部门ID
     * @return 是否存在
     */
    int countById(Long id);
}