-- ============================================================================
-- 测试数据插入脚本
-- ============================================================================
USE training_plan;

-- ============================================================================
-- 1. 角色数据 (6种角色)
-- ============================================================================
INSERT INTO sys_role (id, role_code, role_name, role_desc, sort_order) VALUES
(1, 'ROLE_ADMIN', '系统管理员', '管理系统用户、角色、权限、基础数据', 1),
(2, 'ROLE_DEAN', '教务处管理员', '审核培养计划、发布通知、数据统计', 2),
(3, 'ROLE_SECRETARY', '学院教学秘书', '管理本学院专业、课程、培养方案', 3),
(4, 'ROLE_MAJOR_LEADER', '专业负责人', '制定和修订本专业培养计划', 4),
(5, 'ROLE_TEACHER', '教师', '查看授课课程、提交教学大纲', 5),
(6, 'ROLE_STUDENT', '学生', '查看培养计划、课程修读进度', 6);

-- ============================================================================
-- 2. 用户数据 (密码均为 BCrypt 加密的 "123456")
-- BCrypt($2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh)
-- ============================================================================
INSERT INTO sys_user (id, username, password, real_name, email, phone, user_type, status, college_id) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '系统管理员', 'admin@hfk.edu.cn', '13800000001', 'admin', 1, NULL),
(2, 'jiaowu01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '张教务', 'zhangjw@hfk.edu.cn', '13800000002', 'admin', 1, NULL),
(3, 'mishu01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '李秘书', 'lims@hfk.edu.cn', '13800000003', 'admin', 1, 1),
(4, 'fuzeren01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '王教授', 'wangfzr@hfk.edu.cn', '13800000004', 'teacher', 1, 1),
(5, 'teacher01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '刘老师', 'liuteacher@hfk.edu.cn', '13800000005', 'teacher', 1, 1),
(6, 'teacher02', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '陈老师', 'chenteacher@hfk.edu.cn', '13800000006', 'teacher', 1, 1),
(7, '20230101001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '赵同学', 'zhao@stu.hfk.edu.cn', '13900000001', 'student', 1, 1),
(8, '20230101002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '钱同学', 'qian@stu.hfk.edu.cn', '13900000002', 'student', 1, 1),
(9, '20230101003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '孙同学', 'sun@stu.hfk.edu.cn', '13900000003', 'student', 1, 1),
(10, '20230101004', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '李同学', 'li@stu.hfk.edu.cn', '13900000004', 'student', 1, 1),
(11, '20230201001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '周同学', 'zhou@stu.hfk.edu.cn', '13900000005', 'student', 1, 2),
(12, '20230201002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '吴同学', 'wu@stu.hfk.edu.cn', '13900000006', 'student', 1, 2),
(13, '20230301001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '郑同学', 'zheng@stu.hfk.edu.cn', '13900000007', 'student', 1, 3),
(14, '20230301002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '王同学', 'wang@stu.hfk.edu.cn', '13900000008', 'student', 1, 3),
(15, '20230401001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '冯同学', 'feng@stu.hfk.edu.cn', '13900000009', 'student', 1, 4),
(16, '20230401002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '陈同学', 'chen@stu.hfk.edu.cn', '13900000010', 'student', 1, 4);

-- ============================================================================
-- 3. 用户角色关联
-- ============================================================================
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1),   -- admin → 系统管理员
(2, 2),   -- jiaowu01 → 教务处管理员
(3, 3),   -- mishu01 → 学院教学秘书
(4, 4),   -- fuzeren01 → 专业负责人
(5, 5), (6, 5),   -- 教师
(7, 6), (8, 6), (9, 6), (10, 6),   -- 学生(计算机)
(11, 6), (12, 6),                     -- 学生(软件)
(13, 6), (14, 6),                     -- 学生(通信)
(15, 6), (16, 6);                     -- 学生(大数据)

-- ============================================================================
-- 4. 学院数据 (2个学院)
-- ============================================================================
INSERT INTO college (id, college_code, college_name, dean, phone, email) VALUES
(1, 'CS', '计算机与信息工程学院', '张院长', '010-1001', 'cs@hfk.edu.cn'),
(2, 'EE', '电子信息工程学院', '李院长', '010-1002', 'ee@hfk.edu.cn');

-- ============================================================================
-- 5. 专业数据 (4个专业)
-- ============================================================================
INSERT INTO major (id, major_code, major_name, college_id, discipline_category, degree_type, duration, level, total_credits, training_objective, graduation_requirements) VALUES
(1, '080901', '计算机科学与技术', 1, '工学', '工学学士', 4, '本科', 160,
 '培养具有良好的科学素养，系统掌握计算机科学与技术的基础理论、基本知识和基本技能，能在科研、教育、企业、事业和行政管理等部门从事计算机教学、科学研究和应用的高级专门人才。',
 '修满160学分，其中通识教育课不低于40学分，专业核心课不低于50学分，完成毕业论文并通过答辩。'),
(2, '080902', '软件工程', 1, '工学', '工学学士', 4, '本科', 158,
 '培养掌握软件工程领域基础理论知识和先进开发技术，具备软件开发和管理能力，能在软件企业从事软件项目开发、管理和服务的高级工程技术人才。',
 '修满158学分，其中通识教育课不低于40学分，专业核心课不低于50学分，完成毕业设计并通过答辩。'),
(3, '080703', '通信工程', 2, '工学', '工学学士', 4, '本科', 160,
 '培养具备通信技术、通信系统和通信网等方面的知识，能在通信领域从事研究、设计、制造和运营的高级工程技术人才。',
 '修满160学分，完成毕业论文并通过答辩。'),
(4, '080910', '数据科学与大数据技术', 2, '工学', '工学学士', 4, '本科', 158,
 '培养掌握数据科学基础理论和大数据处理技术，具备大数据分析、处理和可视化能力的高级专门人才。',
 '修满158学分，完成毕业设计并通过答辩。');

-- ============================================================================
-- 6. 班级数据
-- ============================================================================
INSERT INTO class (id, class_code, class_name, major_id, grade, head_teacher, student_count) VALUES
(1, 'CS2301', '计算机科学与技术2023级1班', 1, 2023, '刘老师', 35),
(2, 'CS2302', '计算机科学与技术2023级2班', 1, 2023, '陈老师', 33),
(3, 'SE2301', '软件工程2023级1班', 2, 2023, '王教授', 30),
(4, 'CE2301', '通信工程2023级1班', 3, 2023, '赵老师', 28),
(5, 'DS2301', '数据科学2023级1班', 4, 2023, '孙老师', 25);

-- ============================================================================
-- 7. 学生数据 (10名)
-- ============================================================================
INSERT INTO student (id, user_id, student_no, real_name, gender, enrollment_year, class_id, major_id, college_id, training_plan_id, status) VALUES
(1, 7,  '20230101001', '赵同学', 1, 2023, 1, 1, 1, NULL, '在读'),
(2, 8,  '20230101002', '钱同学', 2, 2023, 1, 1, 1, NULL, '在读'),
(3, 9,  '20230101003', '孙同学', 1, 2023, 2, 1, 1, NULL, '在读'),
(4, 10, '20230101004', '李同学', 2, 2023, 2, 1, 1, NULL, '在读'),
(5, 11, '20230201001', '周同学', 1, 2023, 3, 2, 1, NULL, '在读'),
(6, 12, '20230201002', '吴同学', 2, 2023, 3, 2, 1, NULL, '在读'),
(7, 13, '20230301001', '郑同学', 1, 2023, 4, 3, 2, NULL, '在读'),
(8, 14, '20230301002', '王同学', 2, 2023, 4, 3, 2, NULL, '在读'),
(9, 15, '20230401001', '冯同学', 1, 2023, 5, 4, 2, NULL, '在读'),
(10,16, '20230401002', '陈同学', 2, 2023, 5, 4, 2, NULL, '在读');

-- ============================================================================
-- 8. 教师数据
-- ============================================================================
INSERT INTO teacher (id, user_id, teacher_no, real_name, gender, title, college_id, major_direction) VALUES
(1, 4, 'T2010001', '王教授', 1, '教授', 1, '软件工程、人工智能'),
(2, 5, 'T2015002', '刘老师', 2, '讲师', 1, '数据结构、算法设计'),
(3, 6, 'T2018003', '陈老师', 1, '副教授', 1, '数据库系统、计算机网络');

-- ============================================================================
-- 9. 课程数据 (20门课程)
-- ============================================================================
INSERT INTO course (id, course_code, course_name, credit, class_hour, lecture_hour, lab_hour, course_type, course_category, college_id, semester, exam_type, description) VALUES
-- 通识教育课
(1, 'GE001', '高等数学A', 5.0, 80, 80, 0, 'REQUIRED', 'GENERAL', 1, 1, '考试', '函数、极限、微积分、级数等'),
(2, 'GE002', '大学物理B', 4.0, 64, 48, 16, 'REQUIRED', 'GENERAL', 1, 2, '考试', '力学、热学、电磁学基础'),
(3, 'GE003', '大学英语I', 3.0, 48, 48, 0, 'REQUIRED', 'GENERAL', 1, 1, '考试', '英语听说读写综合训练'),
(4, 'GE004', '大学英语II', 3.0, 48, 48, 0, 'REQUIRED', 'GENERAL', 1, 2, '考试', '英语进阶与学术英语'),
(5, 'GE005', '思想道德与法治', 3.0, 48, 48, 0, 'REQUIRED', 'GENERAL', 1, 1, '考查', '思想道德修养与法律基础'),
(6, 'GE006', '体育I', 1.0, 32, 0, 32, 'REQUIRED', 'GENERAL', 1, 1, '考查', '体能训练与体育技能'),

-- 学科基础课
(7, 'BAS001', '程序设计基础(C语言)', 4.0, 64, 32, 32, 'REQUIRED', 'BASIC', 1, 1, '考试', 'C语言语法、控制结构、函数、指针'),
(8, 'BAS002', '数据结构', 4.0, 64, 48, 16, 'REQUIRED', 'BASIC', 1, 2, '考试', '线性表、树、图、查找、排序'),
(9, 'BAS003', '计算机组成原理', 4.0, 64, 48, 16, 'REQUIRED', 'BASIC', 1, 3, '考试', '计算机硬件组成、指令系统、CPU设计'),
(10, 'BAS004', '操作系统', 3.0, 48, 40, 8, 'REQUIRED', 'BASIC', 1, 4, '考试', '进程管理、内存管理、文件系统'),
(11, 'BAS005', '计算机网络', 3.0, 48, 40, 8, 'REQUIRED', 'BASIC', 1, 4, '考试', 'TCP/IP协议栈、网络层、传输层'),

-- 专业核心课
(12, 'CORE001', '数据库系统原理', 3.5, 56, 40, 16, 'REQUIRED', 'CORE', 1, 3, '考试', '关系模型、SQL、范式、事务管理'),
(13, 'CORE002', '软件工程', 3.0, 48, 40, 8, 'REQUIRED', 'CORE', 1, 5, '考试', '软件生命周期、需求分析、设计模式'),
(14, 'CORE003', '编译原理', 3.0, 48, 40, 8, 'REQUIRED', 'CORE', 1, 5, '考试', '词法分析、语法分析、语义分析'),
(15, 'CORE004', '人工智能导论', 3.0, 48, 40, 8, 'REQUIRED', 'CORE', 1, 6, '考试', '搜索、知识表示、机器学习基础'),

-- 专业选修课
(16, 'ELE001', 'Web前端开发技术', 2.5, 40, 24, 16, 'ELECTIVE', 'ELECTIVE', 1, 4, '考查', 'HTML/CSS/JavaScript/Vue框架'),
(17, 'ELE002', '移动应用开发', 2.5, 40, 24, 16, 'ELECTIVE', 'ELECTIVE', 1, 5, '考查', 'Android/iOS应用开发基础'),
(18, 'ELE003', 'Python数据分析', 2.0, 32, 16, 16, 'ELECTIVE', 'ELECTIVE', 1, 6, '考查', 'NumPy/Pandas/Matplotlib基础'),

-- 实践教学
(19, 'PRAC001', '程序设计课程设计', 2.0, 40, 0, 40, 'REQUIRED', 'PRACTICE', 1, 1, '考查', '综合程序设计项目实践'),
(20, 'PRAC002', '毕业实习', 4.0, 80, 0, 80, 'REQUIRED', 'PRACTICE', 1, 8, '考查', '企业实习、毕业设计');

-- ============================================================================
-- 10. 课程先修关系
-- ============================================================================
INSERT INTO course_prerequisite (course_id, prerequisite_id, is_strict) VALUES
(8, 7, 1),   -- 数据结构 → 程序设计基础
(10, 8, 1),  -- 操作系统 → 数据结构
(12, 8, 1),  -- 数据库 → 数据结构
(13, 8, 1),  -- 软件工程 → 数据结构
(14, 8, 1),  -- 编译原理 → 数据结构
(15, 7, 1);  -- 人工智能 → 程序设计基础

-- ============================================================================
-- 11. 培养计划数据 (1套完整计划)
-- ============================================================================
INSERT INTO training_plan (id, plan_code, plan_name, major_id, enrollment_year_start, enrollment_year_end, duration, total_credits, required_credits, elective_credits, general_credits, core_credits, status, description) VALUES
(1, 'PLAN-CS-2023', '计算机科学与技术2023级培养计划', 1, 2023, 2027, 4, 160, 130, 30, 40, 50, 'DRAFT',
 '本培养计划适用于2023级计算机科学与技术专业本科生。培养具有扎实理论基础和实践能力的计算机专业人才。');

-- 培养计划版本
INSERT INTO plan_version (id, plan_id, version_no, status, submitter_id, change_log) VALUES
(1, 1, '2026-v1', 'DRAFT', 4, '初始版本，完成课程体系设计');

-- 计划课程安排 (按学期)
INSERT INTO plan_course (plan_version_id, course_id, semester, course_type_in_plan, course_category_in_plan, is_required, suggest_credit, sort_order) VALUES
-- 第1学期
(1, 1, 1, 'REQUIRED', 'GENERAL', 1, 5.0, 1),
(1, 3, 1, 'REQUIRED', 'GENERAL', 1, 3.0, 2),
(1, 5, 1, 'REQUIRED', 'GENERAL', 1, 3.0, 3),
(1, 6, 1, 'REQUIRED', 'GENERAL', 1, 1.0, 4),
(1, 7, 1, 'REQUIRED', 'BASIC', 1, 4.0, 5),
(1, 19, 1, 'REQUIRED', 'PRACTICE', 1, 2.0, 6),
-- 第2学期
(1, 2, 2, 'REQUIRED', 'GENERAL', 1, 4.0, 1),
(1, 4, 2, 'REQUIRED', 'GENERAL', 1, 3.0, 2),
(1, 8, 2, 'REQUIRED', 'BASIC', 1, 4.0, 3),
-- 第3学期
(1, 9, 3, 'REQUIRED', 'BASIC', 1, 4.0, 1),
(1, 12, 3, 'REQUIRED', 'CORE', 1, 3.5, 2),
-- 第4学期
(1, 10, 4, 'REQUIRED', 'BASIC', 1, 3.0, 1),
(1, 11, 4, 'REQUIRED', 'BASIC', 1, 3.0, 2),
(1, 16, 4, 'ELECTIVE', 'ELECTIVE', 0, 2.5, 3),
-- 第5学期
(1, 13, 5, 'REQUIRED', 'CORE', 1, 3.0, 1),
(1, 14, 5, 'REQUIRED', 'CORE', 1, 3.0, 2),
(1, 17, 5, 'ELECTIVE', 'ELECTIVE', 0, 2.5, 3),
-- 第6学期
(1, 15, 6, 'REQUIRED', 'CORE', 1, 3.0, 1),
(1, 18, 6, 'ELECTIVE', 'ELECTIVE', 0, 2.0, 2),
-- 第8学期
(1, 20, 8, 'REQUIRED', 'PRACTICE', 1, 4.0, 1);

-- ============================================================================
-- 12. 学生选课成绩记录 (模拟已修课程)
-- ============================================================================
INSERT INTO student_course_record (student_id, course_id, semester, score, grade_point, is_pass, status) VALUES
-- 赵同学 2023级第1、2学期课程
(1, 1, '2023-1', 85, 3.5, 1, '已修'),
(1, 3, '2023-1', 78, 2.8, 1, '已修'),
(1, 5, '2023-1', 88, 3.8, 1, '已修'),
(1, 6, '2023-1', 90, 4.0, 1, '已修'),
(1, 7, '2023-1', 82, 3.2, 1, '已修'),
(1, 19, '2023-1', 80, 3.0, 1, '已修'),
(1, 2, '2023-2', 75, 2.5, 1, '已修'),
(1, 4, '2023-2', 80, 3.0, 1, '已修'),
(1, 8, '2023-2', 70, 2.0, 1, '已修'),
-- 钱同学 (部分挂科)
(2, 1, '2023-1', 55, 0, 0, '已修'),
(2, 3, '2023-1', 68, 1.8, 1, '已修'),
(2, 5, '2023-1', 82, 3.2, 1, '已修'),
(2, 6, '2023-1', 85, 3.5, 1, '已修'),
(2, 7, '2023-1', 45, 0, 0, '已修'),
(2, 2, '2023-2', 60, 1.0, 1, '已修'),
(2, 4, '2023-2', 72, 2.2, 1, '已修'),
(2, 8, '2023-2', 50, 0, 0, '已修');

-- ============================================================================
-- 13. 学业预警记录
-- ============================================================================
INSERT INTO academic_warning (student_id, warning_type, warning_level, warning_content, is_resolved) VALUES
(2, 'FAIL_COURSE', '橙色', '2023-1学期：高等数学A(55分)、程序设计基础(45分)不及格；2023-2学期：数据结构(50分)不及格', 0);

-- ============================================================================
-- 14. 权限数据 (基础菜单权限)
-- ============================================================================
INSERT INTO sys_permission (id, parent_id, perm_name, perm_code, perm_type, path, component, icon, sort_order) VALUES
-- 一级菜单
(1, 0, '系统管理', 'system', 'menu', '/system', NULL, 'Setting', 1),
(2, 0, '基础数据', 'data', 'menu', '/data', NULL, 'DataAnalysis', 2),
(3, 0, '课程管理', 'course', 'menu', '/course', NULL, 'Reading', 3),
(4, 0, '培养计划', 'plan', 'menu', '/training-plan', NULL, 'Notebook', 4),
(5, 0, '学业管理', 'student', 'menu', '/student', NULL, 'User', 5),
(6, 0, '统计报表', 'statistics', 'menu', '/statistics', NULL, 'TrendCharts', 6),
-- 二级 - 系统管理
(11, 1, '用户管理', 'system:user', 'menu', '/system/user', 'system/user/List', 'UserFilled', 1),
(12, 1, '角色管理', 'system:role', 'menu', '/system/role', 'system/role/List', 'Avatar', 2),
(13, 1, '权限管理', 'system:permission', 'menu', '/system/permission', NULL, 'Lock', 3),
-- 二级 - 基础数据
(21, 2, '学院管理', 'data:college', 'menu', '/data/college', 'system/college/List', 'OfficeBuilding', 1),
(22, 2, '专业管理', 'data:major', 'menu', '/data/major', 'system/major/List', 'Collection', 2),
(23, 2, '班级管理', 'data:class', 'menu', '/data/class', 'system/class/List', 'School', 3),
-- 二级 - 课程管理
(31, 3, '课程库', 'course:list', 'menu', '/course/list', 'course/List', 'Document', 1),
(32, 3, '教学大纲', 'course:syllabus', 'menu', '/course/syllabus', NULL, 'Edit', 2),
-- 二级 - 学业管理
(51, 5, '学生选课', 'student:course', 'menu', '/student/course', 'student/course/List', 'Tickets', 1),
(52, 5, '修读进度', 'student:progress', 'menu', '/student/progress', NULL, 'DataLine', 2),
(53, 5, '学业预警', 'student:warning', 'menu', '/student/warning', 'student/warning/List', 'WarningFilled', 3);
