package com.sp.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sp.system.entity.DepartmentParentBinding;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 部门家长绑定数据层
 *
 */
public interface DepartmentParentBindingMapper extends BaseMapper<DepartmentParentBinding> {

    /**
     * 根据部门ID查询绑定的家长
     * @param departmentId 部门ID
     * @return 家长用户ID列表
     */
    List<String> selectByDepartmentId(@Param("departmentId") Long departmentId);

    /**
     * 根据家长ID查询绑定的部门
     * @param parentUserId 家长用户ID
     * @return 部门ID列表
     */
    List<Long> selectByParentUserId(@Param("parentUserId") String parentUserId);

    /**
     * 根据部门ID删除绑定记录
     * @param departmentId 部门ID
     */
    int deleteByDepartmentId(@Param("departmentId") Long departmentId);

    /**
     * 根据家长ID删除绑定记录
     * @param parentUserId 家长用户ID
     */
    int deleteByParentUserId(@Param("parentUserId") String parentUserId);

    /**
     * 检查部门家长绑定记录是否存在
     * @param departmentId 部门ID
     * @param parentUserId 家长用户ID
     * @return 记录ID，如果不存在则返回null
     */
    Long checkBindingExists(@Param("departmentId") Long departmentId, @Param("parentUserId") String parentUserId);

    /**
     * 安全插入部门家长绑定记录（忽略重复）
     * @param binding 部门家长绑定对象
     * @return 影响的行数
     */
    int insertIgnore(DepartmentParentBinding binding);
}