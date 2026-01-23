-- ----------------------------
-- 学生手册表
-- ----------------------------
drop table if exists sys_student_handbook;
create table sys_student_handbook (
  id                bigint(20)      not null auto_increment    comment '主键ID',
  subject           varchar(255)    not null                   comment '科目',
  category          varchar(100)    not null                   comment '类别',
  start_time        varchar(50)     not null                   comment '开始时间',
  end_time          varchar(50)     not null                   comment '结束时间',
  content           text            default null               comment '内容',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  primary key (id)
) engine=innodb auto_increment=1 comment = '学生手册表';

-- ----------------------------
-- 初始化-学生手册表数据
-- ----------------------------
insert into sys_student_handbook values(1, '数学', '功课', '2/9/2025', '2/9/2025', '完成复习题1-10页', 'admin', sysdate(), '', null);
insert into sys_student_handbook values(2, '语文', '通知', '2/15/2025', '2/15/2025', '下周五学校组织春游，请同学们做好准备', 'admin', sysdate(), '', null);
insert into sys_student_handbook values(3, '英语', '考试', '3/1/2025', '3/1/2025', '期中考试将于三月第一周举行，请同学们提前复习', 'admin', sysdate(), '', null);

-- 创建class_log表
DROP TABLE IF EXISTS class_log;
CREATE TABLE class_log (
                           id                varchar(64)     NOT NULL                COMMENT '主键ID',
                           studentClass      varchar(255)    DEFAULT NULL            COMMENT '班级',
                           teacher           varchar(255)    DEFAULT NULL            COMMENT '教师',
                           course            varchar(255)    NOT NULL                COMMENT '课程',
                           courseType        varchar(100)    DEFAULT NULL            COMMENT '课程类型',
                           content           text            DEFAULT NULL            COMMENT '内容',
                           startDate         varchar(50)     DEFAULT NULL            COMMENT '开始日期',
                           endDate           varchar(50)     DEFAULT NULL            COMMENT '结束日期',
                           updateDate        varchar(50)     DEFAULT NULL            COMMENT '更新日期',
                           PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程日志表';

-- 部门表
-- ----------------------------
drop table if exists sys_department;
create table sys_department (
                                id                bigint(20)      not null                 comment '部门id',
                                parent_id         int(11)         default 0                comment '父亲部门id',
                                name              varchar(255)    not null                 comment '部门名称',
                                type              int(11)         default 0                comment '部门类型：1-班级, 2-年级, 3-学段, 4-校区, 5-学校',
                                register_year     int(11)         default null             comment '入学年份',
                                standard_grade    int(11)         default null             comment '标准年级',
                                order_num         int(11)         default 0                comment '排序值',
                                is_graduated      tinyint(1)      default 0                comment '是否毕业：1-是, 0-否',
                                open_group_chat   tinyint(1)      default 0                comment '是否开启班级群：1-是, 0-否',
                                group_chat_id     varchar(255)    default null             comment '班级群id',
                                primary key (id)
) engine=innodb comment = '部门表';

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


