-- ============================================================
-- 统一测试数据（3角色体系 + 完整权限树）
-- ============================================================
USE training_plan;

-- ===== 角色（3个）=====
INSERT INTO sys_role (id, role_code, role_name, role_desc, sort_order) VALUES
(1, 'ROLE_ADMIN', '系统管理员', '拥有全部权限', 1),
(2, 'ROLE_TEACHER', '教师', '查看授课课程、提交教学大纲', 2),
(3, 'ROLE_STUDENT', '学生', '查看培养计划、课程修读进度、学业预警', 3);

-- ===== 用户（密码: 123456）=====
INSERT INTO sys_user (id, username, password, real_name, email, phone, user_type, status, college_id) VALUES
(1, 'admin', '$2b$10$OPkR4cjKMeoiisoBziRKL.N.oRaAPMJ78Ne4dzvSL.YePxrkqPrU.', '系统管理员', 'admin@hfk.edu.cn', '13800000001', 'admin', 1, NULL),
(2, 'teacher01', '$2b$10$OPkR4cjKMeoiisoBziRKL.N.oRaAPMJ78Ne4dzvSL.YePxrkqPrU.', '刘老师', 'liuteacher@hfk.edu.cn', '13800000005', 'teacher', 1, 1),
(3, 'teacher02', '$2b$10$OPkR4cjKMeoiisoBziRKL.N.oRaAPMJ78Ne4dzvSL.YePxrkqPrU.', '陈老师', 'chenteacher@hfk.edu.cn', '13800000006', 'teacher', 1, 1),
(4, '20230101001', '$2b$10$OPkR4cjKMeoiisoBziRKL.N.oRaAPMJ78Ne4dzvSL.YePxrkqPrU.', '赵同学', 'zhao@stu.hfk.edu.cn', '13900000001', 'student', 1, 1),
(5, '20230101002', '$2b$10$OPkR4cjKMeoiisoBziRKL.N.oRaAPMJ78Ne4dzvSL.YePxrkqPrU.', '钱同学', 'qian@stu.hfk.edu.cn', '13900000002', 'student', 1, 1),
(6, '20230101003', '$2b$10$OPkR4cjKMeoiisoBziRKL.N.oRaAPMJ78Ne4dzvSL.YePxrkqPrU.', '孙同学', 'sun@stu.hfk.edu.cn', '13900000003', 'student', 1, 1),
(7, '20230101004', '$2b$10$OPkR4cjKMeoiisoBziRKL.N.oRaAPMJ78Ne4dzvSL.YePxrkqPrU.', '李同学', 'li@stu.hfk.edu.cn', '13900000004', 'student', 1, 1),
(8, '20230201001', '$2b$10$OPkR4cjKMeoiisoBziRKL.N.oRaAPMJ78Ne4dzvSL.YePxrkqPrU.', '周同学', 'zhou@stu.hfk.edu.cn', '13900000005', 'student', 1, 2),
(9, '20230201002', '$2b$10$OPkR4cjKMeoiisoBziRKL.N.oRaAPMJ78Ne4dzvSL.YePxrkqPrU.', '吴同学', 'wu@stu.hfk.edu.cn', '13900000006', 'student', 1, 2),
(10,'20230301001', '$2b$10$OPkR4cjKMeoiisoBziRKL.N.oRaAPMJ78Ne4dzvSL.YePxrkqPrU.', '郑同学', 'zheng@stu.hfk.edu.cn', '13900000007', 'student', 1, 2);

-- ===== 用户角色分配 =====
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1,1),                          -- admin → 管理员
(2,2),(3,2),                    -- 教师
(4,3),(5,3),(6,3),(7,3),(8,3),(9,3),(10,3);  -- 学生

-- ===== 权限树 =====
INSERT INTO sys_permission (id, parent_id, perm_name, perm_code, perm_type, path, component, icon, sort_order) VALUES
(1,0,'首页','dashboard','menu','/dashboard','','HomeFilled',0),
(2,0,'系统管理','system','menu','/system','','Setting',1),
(3,0,'基础数据','data','menu','/data','','DataAnalysis',2),
(4,0,'课程管理','course','menu','/course','','Reading',3),
(5,0,'培养计划','plan','menu','/training-plan','','Notebook',4),
(6,0,'学业管理','student','menu','/student','','User',5),
(7,0,'统计报表','statistics','menu','/statistics','','TrendCharts',6),
-- 系统管理
(21,2,'用户管理','system:user:list','menu','/system/user','system/user/List','UserFilled',1),
(211,21,'新增用户','system:user:add','button','','','',1),
(212,21,'编辑用户','system:user:edit','button','','','',2),
(213,21,'删除用户','system:user:delete','button','','','',3),
(22,2,'角色管理','system:role:list','menu','/system/role','system/role/List','Avatar',2),
(221,22,'新增角色','system:role:add','button','','','',1),
(222,22,'编辑角色','system:role:edit','button','','','',2),
(223,22,'删除角色','system:role:delete','button','','','',3),
-- 基础数据
(31,3,'学院管理','data:college:list','menu','/data/college','system/college/List','OfficeBuilding',1),
(311,31,'新增学院','data:college:add','button','','','',1),
(312,31,'编辑学院','data:college:edit','button','','','',2),
(313,31,'删除学院','data:college:delete','button','','','',3),
(32,3,'专业管理','data:major:list','menu','/data/major','system/major/List','Collection',2),
(321,32,'新增专业','data:major:add','button','','','',1),
(322,32,'编辑专业','data:major:edit','button','','','',2),
(323,32,'删除专业','data:major:delete','button','','','',3),
(33,3,'班级管理','data:class:list','menu','/data/class','system/class/List','School',3),
(331,33,'新增班级','data:class:add','button','','','',1),
(332,33,'编辑班级','data:class:edit','button','','','',2),
(333,33,'删除班级','data:class:delete','button','','','',3),
(34,3,'学生管理','data:student:list','menu','/data/student','system/student/List','User',4),
(341,34,'新增学生','data:student:add','button','','','',1),
(342,34,'编辑学生','data:student:edit','button','','','',2),
(343,34,'删除学生','data:student:delete','button','','','',3),
(35,3,'教师管理','data:teacher:list','menu','/data/teacher','system/teacher/List','Avatar',5),
(351,35,'新增教师','data:teacher:add','button','','','',1),
(352,35,'编辑教师','data:teacher:edit','button','','','',2),
(353,35,'删除教师','data:teacher:delete','button','','','',3),
-- 课程管理
(41,4,'课程库','course:list','menu','/course/list','course/List','Document',1),
(411,41,'新增课程','course:add','button','','','',1),
(412,41,'编辑课程','course:edit','button','','','',2),
(413,41,'删除课程','course:delete','button','','','',3),
(42,4,'教学大纲','course:syllabus','menu','/course/syllabus','course/Syllabus','Edit',2),
(421,42,'新增大纲','syllabus:add','button','','','',1),
(422,42,'编辑大纲','syllabus:edit','button','','','',2),
-- 培养计划
(51,5,'培养计划列表','plan:list','menu','/training-plan','','Notebook',1),
(511,51,'新增计划','plan:add','button','','','',1),
(512,51,'编辑计划','plan:edit','button','','','',2),
(513,51,'删除计划','plan:delete','button','','','',3),
(514,51,'复制计划','plan:copy','button','','','',4),
(517,51,'发布计划','plan:publish','button','','','',7),
-- 学业管理
(61,6,'个人培养计划','student:myplan','menu','/student/my-plan','student/MyPlan','Notebook',1),
(62,6,'修读进度','student:progress','menu','/student/progress','student/Progress','DataLine',2),
(63,6,'学业预警','student:warning','menu','/student/warning','student/Warning','WarningFilled',3),
(631,63,'生成预警','warning:generate','button','','','',1),
(632,63,'处理预警','warning:resolve','button','','','',2),
-- 统计
(71,7,'统计报表','statistics:view','menu','/statistics','statistics/Index','TrendCharts',1),
(711,71,'导出报表','statistics:export','button','','','',1);

-- ===== 角色权限分配 =====
INSERT INTO sys_role_permission (role_id, permission_id) SELECT 1, id FROM sys_permission;
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(2,1),(2,4),(2,41),(2,42),(2,421),(2,422),(2,51),(2,62),
(3,1),(3,5),(3,51),(3,6),(3,61),(3,62),(3,63);

-- ===== 学院 =====
INSERT INTO college (id, college_code, college_name, dean, phone, email) VALUES
(1, 'CS', '计算机与信息工程学院', '张院长', '010-1001', 'cs@hfk.edu.cn'),
(2, 'EE', '电子信息工程学院', '李院长', '010-1002', 'ee@hfk.edu.cn');

-- ===== 专业 =====
INSERT INTO major (id, major_code, major_name, college_id, discipline_category, degree_type, duration, level, total_credits) VALUES
(1, '080901', '计算机科学与技术', 1, '工学', '工学学士', 4, '本科', 160),
(2, '080902', '软件工程', 1, '工学', '工学学士', 4, '本科', 158),
(3, '080703', '通信工程', 2, '工学', '工学学士', 4, '本科', 160),
(4, '080910', '数据科学与大数据技术', 2, '工学', '工学学士', 4, '本科', 158);

-- ===== 班级 =====
INSERT INTO class_info (id, class_code, class_name, major_id, grade, head_teacher, student_count) VALUES
(1, 'CS2301', '计算机2023级1班', 1, 2023, '刘老师', 35),
(2, 'CS2302', '计算机2023级2班', 1, 2023, '陈老师', 33),
(3, 'SE2301', '软件工程2023级1班', 2, 2023, '王教授', 30),
(4, 'CE2301', '通信工程2023级1班', 3, 2023, '赵老师', 28);

-- ===== 学生 =====
INSERT INTO student (id, user_id, student_no, real_name, gender, enrollment_year, class_id, major_id, college_id, status) VALUES
(1, 4, '20230101001', '赵同学', 1, 2023, 1, 1, 1, '在读'),
(2, 5, '20230101002', '钱同学', 2, 2023, 1, 1, 1, '在读'),
(3, 6, '20230101003', '孙同学', 1, 2023, 2, 1, 1, '在读'),
(4, 7, '20230101004', '李同学', 2, 2023, 2, 1, 1, '在读'),
(5, 8, '20230201001', '周同学', 1, 2023, 3, 2, 1, '在读'),
(6, 9, '20230201002', '吴同学', 2, 2023, 3, 2, 1, '在读'),
(7,10, '20230301001', '郑同学', 1, 2023, 4, 3, 2, '在读');

-- ===== 教师 =====
INSERT INTO teacher (id, user_id, teacher_no, real_name, gender, title, college_id, major_direction) VALUES
(1, 2, 'T2015002', '刘老师', 2, '讲师', 1, '数据结构、算法设计'),
(2, 3, 'T2018003', '陈老师', 1, '副教授', 1, '数据库系统、计算机网络');

-- ===== 课程（20门）=====
INSERT INTO course (id, course_code, course_name, credit, class_hour, lecture_hour, lab_hour, course_type, course_category, college_id, semester, exam_type, description) VALUES
(1, 'GE001', '高等数学A', 5.0, 80, 80, 0, 'REQUIRED', 'GENERAL', 1, 1, '考试', '函数、极限、微积分、级数等'),
(2, 'GE002', '大学物理B', 4.0, 64, 48, 16, 'REQUIRED', 'GENERAL', 1, 2, '考试', '力学、热学、电磁学基础'),
(3, 'GE003', '大学英语I', 3.0, 48, 48, 0, 'REQUIRED', 'GENERAL', 1, 1, '考试', '英语听说读写综合训练'),
(4, 'GE004', '大学英语II', 3.0, 48, 48, 0, 'REQUIRED', 'GENERAL', 1, 2, '考试', '英语进阶与学术英语'),
(5, 'GE005', '思想道德与法治', 3.0, 48, 48, 0, 'REQUIRED', 'GENERAL', 1, 1, '考查', '思想道德修养与法律基础'),
(6, 'GE006', '体育I', 1.0, 32, 0, 32, 'REQUIRED', 'GENERAL', 1, 1, '考查', '体能训练与体育技能'),
(7, 'BAS001', '程序设计基础(C语言)', 4.0, 64, 32, 32, 'REQUIRED', 'BASIC', 1, 1, '考试', 'C语言语法、控制结构、函数、指针'),
(8, 'BAS002', '数据结构', 4.0, 64, 48, 16, 'REQUIRED', 'BASIC', 1, 2, '考试', '线性表、树、图、查找、排序'),
(9, 'BAS003', '计算机组成原理', 4.0, 64, 48, 16, 'REQUIRED', 'BASIC', 1, 3, '考试', '计算机硬件组成、指令系统、CPU设计'),
(10,'BAS004', '操作系统', 3.0, 48, 40, 8, 'REQUIRED', 'BASIC', 1, 4, '考试', '进程管理、内存管理、文件系统'),
(11,'BAS005', '计算机网络', 3.0, 48, 40, 8, 'REQUIRED', 'BASIC', 1, 4, '考试', 'TCP/IP协议栈、网络层、传输层'),
(12,'CORE001','数据库系统原理', 3.5, 56, 40, 16, 'REQUIRED', 'CORE', 1, 3, '考试', '关系模型、SQL、范式、事务管理'),
(13,'CORE002','软件工程', 3.0, 48, 40, 8, 'REQUIRED', 'CORE', 1, 5, '考试', '软件生命周期、需求分析、设计模式'),
(14,'CORE003','编译原理', 3.0, 48, 40, 8, 'REQUIRED', 'CORE', 1, 5, '考试', '词法分析、语法分析、语义分析'),
(15,'CORE004','人工智能导论', 3.0, 48, 40, 8, 'REQUIRED', 'CORE', 1, 6, '考试', '搜索、知识表示、机器学习基础'),
(16,'ELE001', 'Web前端开发技术', 2.5, 40, 24, 16, 'ELECTIVE', 'ELECTIVE', 1, 4, '考查', 'HTML/CSS/JavaScript/Vue框架'),
(17,'ELE002', '移动应用开发', 2.5, 40, 24, 16, 'ELECTIVE', 'ELECTIVE', 1, 5, '考查', 'Android/iOS应用开发基础'),
(18,'ELE003', 'Python数据分析', 2.0, 32, 16, 16, 'ELECTIVE', 'ELECTIVE', 1, 6, '考查', 'NumPy/Pandas/Matplotlib基础'),
(19,'PRAC001','程序设计课程设计', 2.0, 40, 0, 40, 'REQUIRED', 'PRACTICE', 1, 1, '考查', '综合程序设计项目实践'),
(20,'PRAC002','毕业实习', 4.0, 80, 0, 80, 'REQUIRED', 'PRACTICE', 1, 8, '考查', '企业实习、毕业设计');

-- ===== 课程先修关系 =====
INSERT INTO course_prerequisite (course_id, prerequisite_id, is_strict) VALUES
(8, 7, 1), (10, 8, 1), (12, 8, 1), (13, 8, 1), (14, 8, 1), (15, 7, 1);

-- ===== 培养计划 =====
INSERT INTO training_plan (id, plan_code, plan_name, major_id, enrollment_year_start, enrollment_year_end, duration, total_credits, required_credits, elective_credits, general_credits, core_credits, status) VALUES
(1, 'PLAN-CS-2023', '计算机科学与技术2023级培养计划', 1, 2023, 2027, 4, 160, 130, 30, 40, 50, 'DRAFT');


-- 计划课程安排（按学期）
INSERT INTO plan_course (plan_id, course_id, semester, course_type_in_plan, course_category_in_plan, is_required, sort_order) VALUES
(1, 1, 1, 'REQUIRED', 'GENERAL', 1, 1),
(1, 3, 1, 'REQUIRED', 'GENERAL', 1, 2),
(1, 5, 1, 'REQUIRED', 'GENERAL', 1, 3),
(1, 6, 1, 'REQUIRED', 'GENERAL', 1, 4),
(1, 7, 1, 'REQUIRED', 'BASIC', 1, 5),
(1,19, 1, 'REQUIRED', 'PRACTICE', 1, 6),
(1, 2, 2, 'REQUIRED', 'GENERAL', 1, 1),
(1, 4, 2, 'REQUIRED', 'GENERAL', 1, 2),
(1, 8, 2, 'REQUIRED', 'BASIC', 1, 3),
(1, 9, 3, 'REQUIRED', 'BASIC', 1, 1),
(1,12, 3, 'REQUIRED', 'CORE', 1, 2),
(1,10, 4, 'REQUIRED', 'BASIC', 1, 1),
(1,11, 4, 'REQUIRED', 'BASIC', 1, 2),
(1,16, 4, 'ELECTIVE', 'ELECTIVE', 0, 3),
(1,13, 5, 'REQUIRED', 'CORE', 1, 1),
(1,14, 5, 'REQUIRED', 'CORE', 1, 2),
(1,17, 5, 'ELECTIVE', 'ELECTIVE', 0, 3),
(1,15, 6, 'REQUIRED', 'CORE', 1, 1),
(1,18, 6, 'ELECTIVE', 'ELECTIVE', 0, 2),
(1,20, 8, 'REQUIRED', 'PRACTICE', 1, 1);

-- ===== 学生成绩记录 =====
INSERT INTO student_course_record (student_id, course_id, semester, score, grade_point, is_pass, status) VALUES
-- 赵同学
(1, 1, '2023-1', 85, 3.5, 1, '已修'),
(1, 3, '2023-1', 78, 2.8, 1, '已修'),
(1, 5, '2023-1', 88, 3.8, 1, '已修'),
(1, 6, '2023-1', 90, 4.0, 1, '已修'),
(1, 7, '2023-1', 82, 3.2, 1, '已修'),
(1,19, '2023-1', 80, 3.0, 1, '已修'),
(1, 2, '2023-2', 75, 2.5, 1, '已修'),
(1, 4, '2023-2', 80, 3.0, 1, '已修'),
(1, 8, '2023-2', 70, 2.0, 1, '已修'),
-- 钱同学（部分挂科）
(2, 1, '2023-1', 55, 0, 0, '已修'),
(2, 3, '2023-1', 68, 1.8, 1, '已修'),
(2, 5, '2023-1', 82, 3.2, 1, '已修'),
(2, 7, '2023-1', 45, 0, 0, '已修'),
(2, 2, '2023-2', 60, 1.0, 1, '已修'),
(2, 8, '2023-2', 50, 0, 0, '已修');

-- ===== 学业预警 =====
INSERT INTO academic_warning (student_id, warning_type, warning_level, warning_content, is_resolved) VALUES
(2, 'FAIL_COURSE', '橙色', '高等数学A(55分)、程序设计基础(45分)、数据结构(50分)不及格', 0);
