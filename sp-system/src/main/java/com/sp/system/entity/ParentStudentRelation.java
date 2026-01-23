package com.sp.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.sp.common.core.domain.BaseEntity;

/**
 * 家长学生关系实体类
 *
 */
@TableName("sys_parent_student_relation")
public class ParentStudentRelation extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 家长用户ID */
    @TableField("parent_user_id")
    private String parentUserId;

    /** 学生用户ID */
    @TableField("student_user_id")
    private String studentUserId;

    /** 学生姓名 */
    @TableField("student_name")
    private String studentName;

    /** 关系描述 */
    @TableField("relation_desc")
    private String relationDesc;

    /** 家长手机号 */
    @TableField("mobile")
    private String mobile;

    /** 家长外部用户ID */
    @TableField("external_userid")
    private String externalUserid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParentUserId() {
        return parentUserId;
    }

    public void setParentUserId(String parentUserId) {
        this.parentUserId = parentUserId;
    }

    public String getStudentUserId() {
        return studentUserId;
    }

    public void setStudentUserId(String studentUserId) {
        this.studentUserId = studentUserId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRelationDesc() {
        return relationDesc;
    }

    public void setRelationDesc(String relationDesc) {
        this.relationDesc = relationDesc;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getExternalUserid() {
        return externalUserid;
    }

    public void setExternalUserid(String externalUserid) {
        this.externalUserid = externalUserid;
    }

}