package com.sp.system.service;

import com.sp.system.entity.DepartmentParentBinding;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 部门家长绑定服务层
 *
 */
public interface DepartmentParentBindingService extends IService<DepartmentParentBinding> {

    /**
     * 根据部门ID删除部门家长绑定
     *
     * @param departmentId 部门ID
     */
    void deleteByDepartmentId(Long departmentId);

    /**
     * 安全插入部门家长绑定记录（如果不存在）
     * @param binding 部门家长绑定对象
     * @return 结果
     */
    boolean insertIfNotExists(DepartmentParentBinding binding);
}