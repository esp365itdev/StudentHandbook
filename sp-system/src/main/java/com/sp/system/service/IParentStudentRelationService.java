package com.sp.system.service;

import com.sp.system.entity.ParentStudentRelation;

import java.util.List;

/**
 * 家长学生关系Service接口
 *
 */
public interface IParentStudentRelationService {
    /**
     * 查询家长学生关系列表
     *
     * @param parentStudentRelation 家长学生关系信息
     * @return 家长学生关系集合
     */
    List<ParentStudentRelation> selectParentStudentRelationList(ParentStudentRelation parentStudentRelation);

    /**
     * 根据家长ID和学生ID查询关系
     *
     * @param parentUserId 家长用户ID
     * @param studentUserId 学生用户ID
     * @return 家长学生关系
     */
    ParentStudentRelation selectByParentAndStudent(String parentUserId, String studentUserId);

    /**
     * 根据家长ID查询学生列表
     *
     * @param parentUserId 家长用户ID
     * @return 家长学生关系集合
     */
    List<ParentStudentRelation> selectByParentId(String parentUserId);

    /**
     * 绑定家长与学生关系
     *
     * @param parentUserId 家长用户ID
     * @param studentUserId 学生用户ID
     * @param relationDesc 关系描述
     * @return 成功或失败
     */
    boolean bindParentStudentRelation(String parentUserId, String studentUserId, String relationDesc);

    /**
     * 绑定家长与学生关系（带学生姓名）
     *
     * @param parentUserId 家长用户ID
     * @param studentUserId 学生用户ID
     * @param relationDesc 关系描述
     * @param studentName 学生姓名
     * @return 成功或失败
     */
    boolean bindParentStudentRelation(String parentUserId, String studentUserId, String relationDesc, String studentName);

    /**
     * 解绑家长与学生关系
     *
     * @param parentUserId 家长用户ID
     * @param studentUserId 学生用户ID
     * @return 成功或失败
     */
    boolean unbindParentStudentRelation(String parentUserId, String studentUserId);
}