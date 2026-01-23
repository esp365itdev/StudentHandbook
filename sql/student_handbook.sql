-- 创建class_log表
DROP TABLE IF EXISTS class_log;
CREATE TABLE `class_log` (
                             `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                             `studentClass` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                             `teacher` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                             `course` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                             `courseType` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                             `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
                             `startDate` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                             `endDate` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                             `updateDate` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

-- 部门表
-- ----------------------------
drop table if exists sys_department;
CREATE TABLE `sys_department` (
                                  `id` bigint NOT NULL COMMENT '部门id',
                                  `parent_id` int DEFAULT '0' COMMENT '父亲部门id',
                                  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '部门名称',
                                  `type` int DEFAULT '0' COMMENT '部门类型：1-班级, 2-年级, 3-学段, 4-校区, 5-学校',
                                  `register_year` int DEFAULT NULL COMMENT '入学年份',
                                  `standard_grade` int DEFAULT NULL COMMENT '标准年级',
                                  `order_num` int DEFAULT '0' COMMENT '排序值',
                                  `is_graduated` tinyint(1) DEFAULT '0' COMMENT '是否毕业：1-是, 0-否',
                                  `open_group_chat` tinyint(1) DEFAULT '0' COMMENT '是否开启班级群：1-是, 0-否',
                                  `group_chat_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '班级群id',
                                  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表'

-- 家长学生关系表
-- ----------------------------
drop table if exists sys_parent_student_relation;
create table sys_parent_student_relation (
  id                bigint(20)      not null auto_increment    comment '主键ID',
  parent_user_id    varchar(64)     not null                   comment '家长用户ID',
  student_user_id   varchar(64)     not null                   comment '学生用户ID',
  student_name      varchar(100)    default null               comment '学生姓名',
  relation_desc     varchar(50)     default '家长'               comment '关系描述',
  mobile            varchar(20)     default null               comment '家长手机号',
  external_userid   varchar(64)     default null               comment '家长外部用户ID',
  create_time       datetime                                   comment '创建时间',
  update_time       datetime                                   comment '更新时间',
  primary key (id),
  unique key uk_parent_student (parent_user_id, student_user_id)
) engine=innodb auto_increment=1 comment = '家长学生关系表';

-- 部门家长绑定表
-- ----------------------------
drop table if exists sys_department_parent_binding;
create table sys_department_parent_binding (
  id                bigint(20)      not null auto_increment    comment '主键ID',
  department_id     bigint(20)      not null                   comment '部门ID',
  parent_user_id    varchar(64)     not null                   comment '家长用户ID',
  create_time       datetime                                   comment '创建时间',
  update_time       datetime                                   comment '更新时间',
  primary key (id),
  unique key uk_dept_parent (department_id, parent_user_id)
) engine=innodb auto_increment=1 comment = '部门家长绑定表';

-- Token表
-- ----------------------------
drop table if exists sys_token;
CREATE TABLE `sys_token` (
                             `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                             `user_id` bigint NOT NULL COMMENT '用户ID',
                             `parent_user_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                             `token` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Token值',
                             `expire_time` datetime NOT NULL COMMENT '过期时间',
                             `create_time` datetime NOT NULL COMMENT '创建时间',
                             `update_time` datetime NOT NULL COMMENT '更新时间',
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `token_value` (`token`),
                             KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=400 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Token表'

