package com.sp.system.service.impl;

import com.sp.system.entity.ParentStudentRelation;
import com.sp.system.mapper.ParentStudentRelationMapper;
import com.sp.system.service.IParentStudentRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 家长学生关系Service业务层处理
 *
 */
@Service
public class ParentStudentRelationServiceImpl implements IParentStudentRelationService {

    @Autowired
    private ParentStudentRelationMapper parentStudentRelationMapper;

    /**
     * 查询家长学生关系列表
     *
     * @param parentStudentRelation 家长学生关系信息
     * @return 家长学生关系集合
     */
    @Override
    public List<ParentStudentRelation> selectParentStudentRelationList(ParentStudentRelation parentStudentRelation) {
        return parentStudentRelationMapper.selectParentStudentRelationList(parentStudentRelation);
    }

    /**
     * 根据家长ID和学生ID查询关系
     *
     * @param parentUserId 家长用户ID
     * @param studentUserId 学生用户ID
     * @return 家长学生关系
     */
    @Override
    public ParentStudentRelation selectByParentAndStudent(String parentUserId, String studentUserId) {
        return parentStudentRelationMapper.selectByParentAndStudent(parentUserId, studentUserId);
    }

    /**
     * 根据家长ID查询学生列表
     *
     * @param parentUserId 家长用户ID
     * @return 家长学生关系集合
     */
    @Override
    public List<ParentStudentRelation> selectByParentId(String parentUserId) {
        return parentStudentRelationMapper.selectByParentId(parentUserId);
    }

    /**
     * 绑定家长与学生关系
     *
     * @param parentUserId 家长用户ID
     * @param studentUserId 学生用户ID
     * @param relationDesc 关系描述
     * @return 成功或失败
     */
    @Override
    public boolean bindParentStudentRelation(String parentUserId, String studentUserId, String relationDesc) {
        return bindParentStudentRelation(parentUserId, studentUserId, relationDesc, null);
    }

    /**
     * 绑定家长与学生关系（带学生姓名）
     *
     * @param parentUserId 家长用户ID
     * @param studentUserId 学生用户ID
     * @param relationDesc 关系描述
     * @param studentName 学生姓名
     * @return 成功或失败
     */
    @Override
    public boolean bindParentStudentRelation(String parentUserId, String studentUserId, String relationDesc, String studentName) {
        // 检查是否已经存在关系
        ParentStudentRelation existingRelation = selectByParentAndStudent(parentUserId, studentUserId);
        
        if (existingRelation != null) {
            // 更新现有关系
            existingRelation.setRelationDesc(relationDesc);
            existingRelation.setStudentName(studentName);
            int result = parentStudentRelationMapper.updateById(existingRelation);
            return result > 0;
        } else {
            // 创建新关系
            ParentStudentRelation newRelation = new ParentStudentRelation();
            newRelation.setParentUserId(parentUserId);
            newRelation.setStudentUserId(studentUserId);
            newRelation.setRelationDesc(relationDesc);
            newRelation.setStudentName(studentName);
            int result = parentStudentRelationMapper.insert(newRelation);
            return result > 0;
        }
    }

    /**
     * 解绑家长与学生关系
     *
     * @param parentUserId 家长用户ID
     * @param studentUserId 学生用户ID
     * @return 成功或失败
     */
    @Override
    public boolean unbindParentStudentRelation(String parentUserId, String studentUserId) {
        ParentStudentRelation relation = selectByParentAndStudent(parentUserId, studentUserId);
        if (relation != null) {
            int result = parentStudentRelationMapper.deleteById(relation.getId());
            return result > 0;
        }
        return false;
    }
}