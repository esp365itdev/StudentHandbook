-- ----------------------------
-- 16、学生手册表
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
insert into sys_student_handbook values(1, '数学', '作业', '2/9/2025', '2/9/2025', '完成复习题1-10页', 'admin', sysdate(), '', null);
insert into sys_student_handbook values(2, '语文', '通知', '2/15/2025', '2/15/2025', '下周五学校组织春游，请同学们做好准备', 'admin', sysdate(), '', null);
insert into sys_student_handbook values(3, '英语', '考试', '3/1/2025', '3/1/2025', '期中考试将于三月第一周举行，请同学们提前复习', 'admin', sysdate(), '', null);