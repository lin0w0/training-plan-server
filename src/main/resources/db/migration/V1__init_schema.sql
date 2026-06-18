-- ============================================================================
-- 大学学生专业培养计划管理系统 - 数据库初始化脚本
-- 数据库: training_plan | MySQL 8.0+ | 字符集: utf8mb4
-- ============================================================================
CREATE DATABASE IF NOT EXISTS training_plan
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE training_plan;

-- ============================================================================
-- 1. RBAC 权限体系（5表）
-- ============================================================================

-- 系统用户
CREATE TABLE sys_user (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username    VARCHAR(50)  NOT NULL COMMENT '用户名(学号/工号)',
    password    VARCHAR(255) NOT NULL COMMENT 'BCrypt加密',
    real_name   VARCHAR(50)  NOT NULL COMMENT '真实姓名',
    email       VARCHAR(100) COMMENT '邮箱',
    phone       VARCHAR(20)  COMMENT '手机号',
    avatar      VARCHAR(255) COMMENT '头像URL',
    gender      TINYINT DEFAULT 0 COMMENT '0-未知 1-男 2-女',
    user_type   VARCHAR(20)  NOT NULL COMMENT 'admin/teacher/student',
    status      TINYINT DEFAULT 1 COMMENT '0-禁用 1-启用',
    last_login_time DATETIME COMMENT '最后登录时间',
    college_id  BIGINT COMMENT '所属学院ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by   BIGINT,
    update_by   BIGINT,
    deleted     TINYINT DEFAULT 0 COMMENT '逻辑删除',
    remark      VARCHAR(500),
    UNIQUE KEY uk_username (username),
    INDEX idx_user_type (user_type),
    INDEX idx_college_id (college_id)
) ENGINE=InnoDB COMMENT='系统用户';

-- 角色
CREATE TABLE sys_role (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_code   VARCHAR(50)  NOT NULL COMMENT 'ROLE_ADMIN/ROLE_TEACHER/ROLE_STUDENT',
    role_name   VARCHAR(50)  NOT NULL COMMENT '角色名称',
    role_desc   VARCHAR(200) COMMENT '描述',
    sort_order  INT DEFAULT 0,
    status      TINYINT DEFAULT 1,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by   BIGINT,
    update_by   BIGINT,
    deleted     TINYINT DEFAULT 0,
    remark      VARCHAR(500),
    UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB COMMENT='角色';

-- 权限（菜单+按钮）
CREATE TABLE sys_permission (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id   BIGINT DEFAULT 0 COMMENT '0=顶级',
    perm_name   VARCHAR(50)  NOT NULL COMMENT '名称',
    perm_code   VARCHAR(100) NOT NULL COMMENT '编码如 system:user:list',
    perm_type   VARCHAR(20)  NOT NULL COMMENT 'menu/button',
    path        VARCHAR(200) COMMENT '前端路由',
    component   VARCHAR(200) COMMENT '前端组件',
    icon        VARCHAR(50),
    sort_order  INT DEFAULT 0,
    status      TINYINT DEFAULT 1,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by   BIGINT,
    update_by   BIGINT,
    deleted     TINYINT DEFAULT 0,
    remark      VARCHAR(500),
    UNIQUE KEY uk_perm_code (perm_code),
    INDEX idx_parent (parent_id)
) ENGINE=InnoDB COMMENT='权限';

-- 用户-角色关联
CREATE TABLE sys_user_role (
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB COMMENT='用户角色';

-- 角色-权限关联
CREATE TABLE sys_role_permission (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id       BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    UNIQUE KEY uk_role_perm (role_id, permission_id),
    INDEX idx_perm_id (permission_id)
) ENGINE=InnoDB COMMENT='角色权限';

-- ============================================================================
-- 2. 基础数据（5表）
-- ============================================================================

CREATE TABLE college (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    college_code VARCHAR(20)  NOT NULL,
    college_name VARCHAR(100) NOT NULL,
    dean         VARCHAR(50),
    phone        VARCHAR(20),
    email        VARCHAR(100),
    website      VARCHAR(200),
    sort_order   INT DEFAULT 0,
    status       TINYINT DEFAULT 1,
    create_time  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by    BIGINT,
    update_by    BIGINT,
    deleted      TINYINT DEFAULT 0,
    remark       VARCHAR(500),
    UNIQUE KEY uk_college_code (college_code),
    UNIQUE KEY uk_college_name (college_name)
) ENGINE=InnoDB COMMENT='学院';

CREATE TABLE major (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    major_code            VARCHAR(20)  NOT NULL,
    major_name            VARCHAR(100) NOT NULL,
    college_id            BIGINT NOT NULL,
    discipline_category   VARCHAR(50) COMMENT '工学/理学/管理学',
    degree_type           VARCHAR(50) COMMENT '工学学士',
    duration              INT DEFAULT 4 COMMENT '学制(年)',
    level                 VARCHAR(20) DEFAULT '本科',
    total_credits         INT DEFAULT 160,
    training_objective    TEXT,
    graduation_requirements TEXT,
    status                TINYINT DEFAULT 1,
    create_time           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by             BIGINT,
    update_by             BIGINT,
    deleted               TINYINT DEFAULT 0,
    remark                VARCHAR(500),
    UNIQUE KEY uk_major_code (major_code),
    INDEX idx_college_id (college_id)
) ENGINE=InnoDB COMMENT='专业';

CREATE TABLE class_info (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '班级ID',
    class_code    VARCHAR(30)  NOT NULL,
    class_name    VARCHAR(100) NOT NULL,
    major_id      BIGINT NOT NULL,
    grade         INT NOT NULL COMMENT '入学年份',
    head_teacher  VARCHAR(50),
    student_count INT DEFAULT 0,
    status        TINYINT DEFAULT 1,
    create_time   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by     BIGINT,
    update_by     BIGINT,
    deleted       TINYINT DEFAULT 0,
    remark        VARCHAR(500),
    UNIQUE KEY uk_class_code (class_code),
    INDEX idx_major (major_id),
    INDEX idx_grade (grade)
) ENGINE=InnoDB COMMENT='班级';

CREATE TABLE student (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT COMMENT '关联sys_user',
    student_no       VARCHAR(30) NOT NULL,
    real_name        VARCHAR(50) NOT NULL,
    gender           TINYINT DEFAULT 0,
    birth_date       DATE,
    id_card          VARCHAR(18),
    enrollment_year  INT NOT NULL,
    class_id         BIGINT,
    major_id         BIGINT NOT NULL,
    college_id       BIGINT NOT NULL,
    training_plan_id BIGINT COMMENT '执行的培养计划ID',
    status           VARCHAR(20) DEFAULT '在读' COMMENT '在读/休学/退学/毕业',
    phone            VARCHAR(20),
    email            VARCHAR(100),
    address          VARCHAR(255),
    create_time      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by        BIGINT,
    update_by        BIGINT,
    deleted          TINYINT DEFAULT 0,
    remark           VARCHAR(500),
    UNIQUE KEY uk_student_no (student_no),
    INDEX idx_class (class_id),
    INDEX idx_major (major_id),
    INDEX idx_college (college_id),
    INDEX idx_enrollment (enrollment_year)
) ENGINE=InnoDB COMMENT='学生';

CREATE TABLE teacher (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT COMMENT '关联sys_user',
    teacher_no      VARCHAR(30) NOT NULL,
    real_name       VARCHAR(50) NOT NULL,
    gender          TINYINT DEFAULT 0,
    title           VARCHAR(30) COMMENT '教授/副教授/讲师/助教',
    college_id      BIGINT NOT NULL,
    major_direction VARCHAR(200) COMMENT '主讲课程方向',
    phone           VARCHAR(20),
    email           VARCHAR(100),
    status          TINYINT DEFAULT 1,
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by       BIGINT,
    update_by       BIGINT,
    deleted         TINYINT DEFAULT 0,
    remark          VARCHAR(500),
    UNIQUE KEY uk_teacher_no (teacher_no),
    INDEX idx_college (college_id)
) ENGINE=InnoDB COMMENT='教师';

-- ============================================================================
-- 3. 课程相关（3表）
-- ============================================================================

CREATE TABLE course (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_code     VARCHAR(20)   NOT NULL,
    course_name     VARCHAR(200)  NOT NULL,
    en_name         VARCHAR(200),
    credit          DECIMAL(3,1)  NOT NULL,
    class_hour      INT NOT NULL COMMENT '总学时',
    lecture_hour    INT DEFAULT 0 COMMENT '理论学时',
    lab_hour        INT DEFAULT 0 COMMENT '实践学时',
    course_type     VARCHAR(30) NOT NULL COMMENT 'REQUIRED/ELECTIVE/GENERAL',
    course_category VARCHAR(30) NOT NULL COMMENT 'GENERAL/BASIC/CORE/ELECTIVE/PRACTICE',
    college_id      BIGINT,
    semester        INT COMMENT '建议开课学期 1-8',
    exam_type       VARCHAR(20) DEFAULT '考试' COMMENT '考试/考查',
    description     TEXT,
    status          TINYINT DEFAULT 1,
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by       BIGINT,
    update_by       BIGINT,
    deleted         TINYINT DEFAULT 0,
    remark          VARCHAR(500),
    UNIQUE KEY uk_course_code (course_code),
    INDEX idx_type (course_type),
    INDEX idx_category (course_category),
    INDEX idx_college (college_id)
) ENGINE=InnoDB COMMENT='课程';

CREATE TABLE course_prerequisite (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id       BIGINT NOT NULL,
    prerequisite_id BIGINT NOT NULL COMMENT '先修课程ID',
    is_strict       TINYINT DEFAULT 1 COMMENT '0-建议 1-强制',
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by       BIGINT,
    update_by       BIGINT,
    deleted         TINYINT DEFAULT 0,
    remark          VARCHAR(500),
    UNIQUE KEY uk_course_prereq (course_id, prerequisite_id),
    INDEX idx_prereq (prerequisite_id)
) ENGINE=InnoDB COMMENT='课程先修关系';

CREATE TABLE teaching_syllabus (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id           BIGINT NOT NULL,
    teacher_id          BIGINT COMMENT '编写教师',
    semester            VARCHAR(20),
    teaching_objective  TEXT COMMENT '教学目标',
    teaching_content    TEXT COMMENT '教学内容',
    textbook            VARCHAR(200),
    textbook_isbn       VARCHAR(30),
    reference_books     TEXT,
    assessment_method   TEXT COMMENT '考核方式',
    weekly_schedule     TEXT COMMENT '教学周历JSON',
    status              VARCHAR(20) DEFAULT '草稿' COMMENT '草稿/已提交/已审核',
    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by           BIGINT,
    update_by           BIGINT,
    deleted             TINYINT DEFAULT 0,
    remark              VARCHAR(500),
    INDEX idx_course (course_id),
    INDEX idx_teacher (teacher_id)
) ENGINE=InnoDB COMMENT='教学大纲';

-- ============================================================================
-- 4. 培养计划（2表，无审批流程）
-- ============================================================================

CREATE TABLE training_plan (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    plan_code            VARCHAR(30)  NOT NULL,
    plan_name            VARCHAR(200) NOT NULL,
    major_id             BIGINT NOT NULL,
    enrollment_year_start INT NOT NULL,
    enrollment_year_end  INT NOT NULL,
    duration             INT DEFAULT 4,
    total_credits        INT NOT NULL,
    required_credits     INT DEFAULT 0,
    elective_credits     INT DEFAULT 0,
    general_credits      INT DEFAULT 0 COMMENT '通识学分要求',
    core_credits         INT DEFAULT 0 COMMENT '核心学分要求',
    status               VARCHAR(20) DEFAULT 'PUBLISHED' COMMENT 'DRAFT/PUBLISHED/ARCHIVED',
    description          TEXT,
    create_time          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by            BIGINT,
    update_by            BIGINT,
    deleted              TINYINT DEFAULT 0,
    remark               VARCHAR(500),
    UNIQUE KEY uk_plan_code (plan_code),
    INDEX idx_major (major_id),
    INDEX idx_status (status)
) ENGINE=InnoDB COMMENT='培养计划';

CREATE TABLE plan_course (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    plan_id              BIGINT NOT NULL COMMENT '所属计划ID',
    course_id            BIGINT NOT NULL,
    semester             INT NOT NULL COMMENT '开课学期 1-8',
    course_type_in_plan  VARCHAR(30),
    course_category_in_plan VARCHAR(30),
    is_required          TINYINT DEFAULT 1 COMMENT '0-选修 1-必修',
    sort_order           INT DEFAULT 0,
    create_time          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by            BIGINT,
    update_by            BIGINT,
    deleted              TINYINT DEFAULT 0,
    remark               VARCHAR(500),
    INDEX idx_plan_semester (plan_id, semester),
    INDEX idx_course (course_id)
) ENGINE=InnoDB COMMENT='计划课程关联';

CREATE TABLE plan_snapshot (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    plan_id      BIGINT NOT NULL,
    version_name VARCHAR(50) NOT NULL COMMENT '版本名称 v1/v2',
    snapshot_data JSON COMMENT '课程快照JSON',
    change_log   VARCHAR(500) COMMENT '变更说明',
    create_time  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_plan (plan_id)
) ENGINE=InnoDB COMMENT='培养计划版本快照';

-- ============================================================================
-- 5. 学生学业（2表）
-- ============================================================================

CREATE TABLE student_course_record (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id  BIGINT NOT NULL,
    course_id   BIGINT NOT NULL,
    semester    VARCHAR(10) NOT NULL COMMENT '如2023-1',
    score       DECIMAL(5,1) COMMENT '成绩',
    grade_point DECIMAL(3,1) COMMENT '绩点',
    is_pass     TINYINT COMMENT '0-未通过 1-通过',
    is_retake   TINYINT DEFAULT 0,
    exam_type   VARCHAR(20) DEFAULT '正考',
    status      VARCHAR(20) DEFAULT '已修',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by   BIGINT,
    update_by   BIGINT,
    deleted     TINYINT DEFAULT 0,
    remark      VARCHAR(500),
    UNIQUE KEY uk_student_course_semester (student_id, course_id, semester),
    INDEX idx_student (student_id),
    INDEX idx_course (course_id)
) ENGINE=InnoDB COMMENT='学生选课成绩';

CREATE TABLE academic_warning (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id      BIGINT NOT NULL,
    warning_type    VARCHAR(30) NOT NULL COMMENT 'FAIL_COURSE/CREDIT_LOW/GRADUATION_RISK',
    warning_level   VARCHAR(10) DEFAULT '黄色' COMMENT '黄色/橙色/红色',
    warning_content TEXT,
    is_resolved     TINYINT DEFAULT 0,
    resolve_time    DATETIME,
    resolve_remark  VARCHAR(500),
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by       BIGINT,
    update_by       BIGINT,
    deleted         TINYINT DEFAULT 0,
    remark          VARCHAR(500),
    INDEX idx_student (student_id),
    INDEX idx_type (warning_type),
    INDEX idx_resolved (is_resolved)
) ENGINE=InnoDB COMMENT='学业预警';

-- ============================================================================
-- 6. 操作日志
-- ============================================================================

CREATE TABLE sys_log (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT,
    username    VARCHAR(50),
    module      VARCHAR(50),
    operation   VARCHAR(50),
    method      VARCHAR(200),
    params      TEXT,
    result      TEXT,
    ip          VARCHAR(50),
    duration    BIGINT COMMENT '耗时ms',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user (user_id),
    INDEX idx_time (create_time)
) ENGINE=InnoDB COMMENT='操作日志';
