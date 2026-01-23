package com.sp.system.service;

import com.sp.system.entity.DepartmentParentBinding;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 部门家长绑定服务层
 *
 */
public interface DepartmentParentBindingService extends IService<DepartmentParentBinding> {

    /**
     * 新增部门家长绑定
     * @param binding 部门家长绑定对象
     * @return 结果
     */
    int insert(DepartmentParentBinding binding);

    /**
     * 修改部门家长绑定
     * @param binding 部门家长绑定对象
     * @return 结果
     */
    boolean updateById(DepartmentParentBinding binding);

    /**
     * 根据ID删除部门家长绑定
     * @param id 部门家长绑定ID
     * @return 结果
     */
    int deleteById(Long id);

    /**
     * 根据部门ID删除部门家长绑定
     * @param departmentId 部门ID
     * @return 结果
     */
    int deleteByDepartmentId(Long departmentId);

    /**
     * 安全插入部门家长绑定记录（如果不存在）
     * @param binding 部门家长绑定对象
     * @return 结果
     */
    boolean insertIfNotExists(DepartmentParentBinding binding);
}