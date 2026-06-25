# training-plan-server

> 大学学生专业培养计划管理系统 — 后端服务

## 🛠 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| **Spring Boot** | 3.2.6 | 应用框架 |
| **MyBatis-Plus** | 3.5.7 | ORM + 分页 + 逻辑删除 + 自动填充 |
| **Spring Security** | — | 认证授权框架（方法级 `@PreAuthorize`） |
| **jjwt** | 0.12.5 | JWT 签发 / 解析 / 校验 |
| **MySQL** | 8.0 | 关系型数据库（HikariCP 连接池） |
| **Redis** | — | 缓存（Lettuce + Spring Cache） |
| **Knife4j** | 4.5.0 | API 文档（OpenAPI 3.0 + Swagger UI） |
| **OpenPDF** | 2.0.2 | PDF 报表生成 |
| **Apache POI** | 5.2.5 | Excel 文件读写 |
| **Hutool** | 5.8.28 | Java 工具库（JSON / Excel / 异常 / 集合） |
| **Lombok** | latest | 注解消除样板代码 |
| **Spring AOP** | — | 操作日志切面 |
| **Spring Validation** | — | 请求参数校验 |

## 📁 项目结构

```
training-plan-server/
├── pom.xml
└── src/main/
    ├── java/com/hfk/training/
    │   ├── TrainingApplication.java              # Spring Boot 启动类
    │   │
    │   ├── config/                               # 配置类
    │   │   ├── WebMvcConfig.java                 #   CORS 跨域配置
    │   │   ├── MyBatisPlusConfig.java            #   分页插件
    │   │   └── RedisConfig.java                  #   Redis 序列化配置
    │   │
    │   ├── common/                               # 通用组件
    │   │   ├── BaseEntity.java                   #   实体基类（id/createTime/updateTime/deleted 等）
    │   │   ├── Result.java                       #   统一响应体（含快捷工厂方法如 ok/badRequest/forbidden）
    │   │   ├── PageResult.java                   #   分页结果封装
    │   │   ├── BusinessException.java            #   业务异常
    │   │   ├── GlobalExceptionHandler.java       #   全局异常处理（@RestControllerAdvice）
    │   │   └── MyMetaObjectHandler.java          #   自动填充处理器
    │   │
    │   ├── security/                             # 认证与授权
    │   │   ├── SecurityConfig.java               #   Spring Security 配置（无状态 JWT）
    │   │   ├── JwtUtils.java                     #   JWT 工具（生成/解析/校验）
    │   │   ├── JwtAuthenticationFilter.java      #   JWT 认证过滤器
    │   │   ├── UserDetailsServiceImpl.java       #   用户详情加载
    │   │   └── AuthController.java               #   登录/登出接口
    │   │
    │   └── modules/                              # 业务模块（Controller / Service / Mapper / Entity）
    │       ├── system/                           #   系统管理 & 基础数据
    │       │   ├── controller/
    │       │   │   ├── SysUserController.java    #       用户管理（含密码重置、角色分配）
    │       │   │   ├── SysRoleController.java    #       角色管理 + 权限分配
    │       │   │   ├── SysPermissionController.java #    权限树
    │       │   │   ├── CollegeController.java    #       学院管理
    │       │   │   ├── MajorController.java      #       专业管理
    │       │   │   ├── ClassInfoController.java  #       班级管理
    │       │   │   ├── StudentManageController.java #   学生管理
    │       │   │   ├── TeacherController.java    #       教师管理
    │       │   │   └── DashboardController.java  #       待办事项（按角色推送）
    │       │   ├── entity/                       #       数据实体
    │       │   ├── service/                      #       业务逻辑
    │       │   └── mapper/                       #       数据访问
    │       │
    │       ├── course/                           #   课程管理
    │       │   ├── controller/
    │       │   │   ├── CourseController.java     #       课程 CRUD
    │       │   │   ├── CourseImportController.java #     课程批量导入
    │       │   │   ├── CoursePrerequisiteController.java # 先修关系
    │       │   │   └── SyllabusController.java   #       教学大纲
    │       │   ├── entity/
    │       │   ├── service/
    │       │   └── mapper/
    │       │
    │       ├── plan/                             #   培养计划（核心）
    │       │   ├── controller/
    │       │   │   └── PlanController.java       #       计划 CRUD + 课程安排 + 版本快照 + 按角色过滤
    │       │   ├── entity/
    │       │   │   ├── TrainingPlan.java         #       培养计划
    │       │   │   ├── PlanCourse.java           #       计划-课程关联
    │       │   │   └── PlanSnapshot.java         #       版本快照（JSON）
    │       │   ├── service/
    │       │   │   └── PlanService.java          #       发布/复制/学分统计
    │       │   └── mapper/
    │       │
    │       ├── student/                          #   学生学业
    │       │   ├── controller/
    │       │   │   ├── StudentController.java    #       选课记录 + 修读进度 + 个人计划 + Excel 导入
    │       │   │   └── WarningController.java    #       学业预警（按角色过滤 + 自动生成）
    │       │   ├── entity/
    │       │   │   ├── StudentCourseRecord.java  #       成绩记录
    │       │   │   └── AcademicWarning.java      #       预警记录
    │       │   ├── service/
    │       │   │   ├── StudentService.java       #       进度计算
    │       │   │   ├── WarningService.java       #       预警查询/处理
    │       │   │   └── WarningGenerator.java     #       自动生成预警
    │       │   └── mapper/
    │       │
    │       └── statistics/                       #   统计报表
    │           ├── controller/
    │           │   └── StatisticsController.java #       数据统计 + Excel 导出 + PDF 导出
    │           └── service/
    │               └── PdfExportService.java     #       PDF 报表生成
    │
    └── resources/
        ├── application.yml                       # 主配置（端口/JWT/Knife4j/MyBatis-Plus）
        ├── application-dev.yml                   # 开发环境（数据库/Redis/日志）
        └── db/migration/
            ├── V1__init_schema.sql               # 建表脚本（19 张表）
            ├── V2__init_test_data.sql             # 测试数据（6 角色版）
            └── V2__test_data_v2.sql               # 测试数据（3 角色简化版）
```

## 🚀 快速启动

### 环境要求

| 依赖 | 版本 |
|------|------|
| JDK | 17+ |
| Maven | 3.8+ |
| MySQL | 8.0 |
| Redis | 可选（开发可暂不启动） |

### 1. 初始化数据库

```bash
# 创建数据库并建表
mysql -u root -p < src/main/resources/db/migration/V1__init_schema.sql

# 导入测试数据（推荐 v2）
mysql -u root -p < src/main/resources/db/migration/V2__test_data_v2.sql
```

### 2. 修改配置

编辑 `src/main/resources/application-dev.yml`，填入你的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/training_plan?... # 无需修改
    username: root      # 改为你的 MySQL 用户名
    password: your_pwd  # 改为你的 MySQL 密码
```

### 3. 启动

```bash
# 方式一：Maven 插件
mvn spring-boot:run

# 方式二：构建 JAR 包运行
mvn clean package -DskipTests
java -jar target/training-plan-server-1.0.0-SNAPSHOT.jar
```

### 4. 验证

- **Knife4j 接口文档**：[http://localhost:8080/doc.html](http://localhost:8080/doc.html)
- **Swagger UI**：[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **登录测试**：`curl -X POST http://localhost:8080/auth/login -H 'Content-Type: application/json' -d '{"username":"admin","password":"123456"}'`

## 🔑 默认测试账号

> 所有密码均为 `123456`（BCrypt 加密存储）

| 用户名 | 角色 | 说明 |
|--------|------|------|
| `admin` | 系统管理员 | 全部权限 |
| `teacher01` | 教师 | 课程查看、大纲提交 |
| `teacher02` | 教师 | 课程查看、大纲提交 |
| `20230101001` | 学生 | 计算机科学与技术（正常修读） |
| `20230101002` | 学生 | 计算机科学与技术（含挂科/预警数据） |
| `20230201001` | 学生 | 软件工程 |

## 📊 数据库设计

共 **19 张表**，6 大模块：

### RBAC 权限体系（5 表）

| 表名 | 说明 |
|------|------|
| `sys_user` | 系统用户（BCrypt 密码、user_type 区分角色类型） |
| `sys_role` | 角色定义（role_code + role_name） |
| `sys_permission` | 权限定义（menu/button，树形结构，perm_code 如 `system:user:list`） |
| `sys_user_role` | 用户-角色关联 |
| `sys_role_permission` | 角色-权限关联 |

### 基础数据（5 表）

| 表名 | 说明 |
|------|------|
| `college` | 学院（学院编码唯一） |
| `major` | 专业（关联学院、学科门类、学位、总学分要求） |
| `class_info` | 班级（关联专业、年级） |
| `student` | 学生（学号唯一、关联 user/class/major/college） |
| `teacher` | 教师（工号唯一、关联 user/college） |

### 课程模块（3 表）

| 表名 | 说明 |
|------|------|
| `course` | 课程库（course_code 唯一、5 种分类、必修/选修） |
| `course_prerequisite` | 课程先修关系（强制/建议） |
| `teaching_syllabus` | 教学大纲（目标/内容/教材/考核/周历 JSON） |

### 培养计划（3 表）

| 表名 | 说明 |
|------|------|
| `training_plan` | 培养计划（plan_code 唯一、关联 major、学分门槛） |
| `plan_course` | 计划-课程关联（plan_id + course_id + semester） |
| `plan_snapshot` | 版本快照（plan_id + 课程 JSON 快照 + 变更说明） |

### 学生学业（2 表）

| 表名 | 说明 |
|------|------|
| `student_course_record` | 成绩记录（student_id + course_id + semester 唯一） |
| `academic_warning` | 学业预警（挂科/学分不足/毕业风险，黄/橙/红三级） |

### 系统日志（1 表）

| 表名 | 说明 |
|------|------|
| `sys_log` | 操作日志（用户/模块/操作/参数/耗时/IP） |

### 通用字段约定

所有业务表包含以下字段，由 `BaseEntity` 抽象类统一管理：

```
id          BIGINT       自增主键
create_time DATETIME     创建时间（自动填充）
update_time DATETIME     更新时间（自动填充）
create_by   BIGINT       创建人（自动填充）
update_by   BIGINT       更新人（自动填充）
deleted     TINYINT      逻辑删除（0=正常 1=删除）
remark      VARCHAR      备注
```

## 📝 API 接口

### 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1719000000000
}
```

### 状态码约定

| 状态码 | 含义 |
|--------|------|
| `200` | 成功 |
| `400` | 参数校验失败 / 业务校验不通过 |
| `401` | 未登录 / Token 过期 |
| `403` | 权限不足 |
| `404` | 资源不存在 |
| `500` | 服务器内部错误 |

### 接口一览

| 模块 | 路径前缀 | 主要端点 | 认证 |
|------|----------|----------|------|
| 认证 | `/auth` | `POST /login`, `GET /captcha`, `POST /register` | 无需 |
| 待办事项 | `/auth` | `GET /pending-items`（按角色查询待处理任务） | JWT |
| 用户管理 | `/system/user/**` | `GET /page`, `GET /all`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}`, `PUT /{id}/reset-pwd` | JWT |
| 角色管理 | `/system/role/**` | `GET /page`, `GET /all`, `POST`, `PUT /{id}`, `DELETE /{id}`, `PUT /assign-permissions` | JWT |
| 权限管理 | `/system/permission/**` | `GET /tree` | JWT |
| 学院管理 | `/system/college/**` | `GET /page`, `GET /all`, `POST`, `PUT /{id}`, `DELETE /{id}` | JWT |
| 专业管理 | `/system/major/**` | `GET /page`, `GET /all`, `POST`, `PUT /{id}`, `DELETE /{id}` | JWT |
| 班级管理 | `/system/class/**` | `GET /page`, `POST`, `PUT /{id}`, `DELETE /{id}` | JWT |
| 学生管理 | `/system/student/**` | `GET /page`, `POST`, `PUT /{id}`, `DELETE /{id}` | JWT |
| 教师管理 | `/system/teacher/**` | `GET /page`, `POST`, `PUT /{id}`, `DELETE /{id}` | JWT |
| 课程库 | `/course/**` | `GET /page`, `GET /all`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}`, `POST /import`（Excel 批量导入） | JWT |
| 先修关系 | `/course/{id}/prerequisite/**` | `GET`, `POST`, `PUT`, `DELETE` | JWT |
| 教学大纲 | `/syllabus/**` | `GET /page`, `GET /course/{id}`, `POST`, `PUT /{id}` | JWT |
| 培养计划 | `/plan/**` | `GET /page`（按角色过滤）, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}`, `POST /{id}/copy`, `PUT /{id}/publish` | JWT |
| 计划课程 | `/plan/{id}/courses` | `GET` 列表 + `PUT` 保存 + `GET /courses-detail`（按学期分组 + 学分统计） | JWT |
| 版本快照 | `/plan/{id}/snapshots` | `POST` 创建 + `GET` 列表；`GET /plan/snapshot/{id}` 详情 | JWT |
| 学业记录 | `/student/**` | `GET /page`, `GET /{id}/courses`, `POST /course-record`, `PUT /course-record/{id}`, `DELETE /course-record/{id}`, `POST /courses/import`（Excel 导入）, `GET /{id}/progress`, `GET /my-progress`, `GET /my-plan` | JWT |
| 学业预警 | `/warning/**` | `GET /page`（学生只看自己）, `GET /student/{id}`, `PUT /{id}/resolve`, `POST /generate` | JWT |
| 统计报表 | `/statistics/**` | `GET /overview`, `GET /plan-execution`, `GET /course-stats`, `GET /graduation-analysis/{majorId}`, `GET /semester-credits`, `GET /export/{type}`（Excel）, `GET /export-pdf/{type}`（PDF） | JWT |

## 🔐 安全设计

### 认证流程

```
客户端                     服务端
  │                          │
  │  POST /auth/login        │
  │  {username, password}    │
  │─────────────────────────▶│
  │                          │ 1. BCrypt 校验密码
  │                          │ 2. 生成 JWT（含 userId/username/角色/权限 claims）
  │  {code:200, data:        │
  │   {token: "Bearer ..."}} │
  │◀─────────────────────────│
  │                          │
  │  后续请求 Header:         │
  │  Authorization: Bearer.. │
  │─────────────────────────▶│
  │                          │ 3. JwtAuthenticationFilter 解析 Token
  │                          │ 4. 设置 SecurityContext
  │                          │ 5. @PreAuthorize 检查方法权限
  │  {code:200, data: ...}   │
  │◀─────────────────────────│
```

### JWT 配置

```yaml
# application.yml
jwt:
  secret: <Base64 编码的密钥，至少 256 位>
  expiration: 86400000    # 24 小时
  header: Authorization
  token-prefix: "Bearer "
```

### 白名单路径

以下路径无需认证即可访问：

- `/auth/login` — 登录接口
- `/auth/captcha` — 验证码
- `/auth/register` — 注册
- `/doc.html`, `/swagger-ui/**`, `/v3/api-docs/**` — API 文档
- `/static/**`, `/uploads/**` — 静态资源
- `OPTIONS` 请求全部放行（CORS 预检）

### 权限控制

- **路由级**：`SecurityConfig` 中对 `/auth/**` 放行，其余全部认证
- **方法级**：`@PreAuthorize("hasAnyAuthority('plan:add','ROLE_ADMIN')")` 精确控制

## 🏗 架构分层

```
┌─────────────────────────────────────────────────────────┐
│                    Controller 层                         │
│  接收 HTTP 请求，参数校验，调用 Service，返回 Result       │
│  @RestController + @RequestMapping + @PreAuthorize       │
├─────────────────────────────────────────────────────────┤
│                    Service 层                            │
│  业务逻辑编排、事务管理、复杂查询组装                       │
│  @Service + @Transactional                               │
├─────────────────────────────────────────────────────────┤
│                    Mapper 层                             │
│  MyBatis-Plus BaseMapper + 自定义 SQL                    │
│  @Mapper + extends BaseMapper<Entity>                    │
├─────────────────────────────────────────────────────────┤
│                    Entity 层                             │
│  extends BaseEntity（通用字段自动继承）                    │
│  @TableName + @TableField + @TableLogic                  │
└─────────────────────────────────────────────────────────┘
```

## 💡 关键设计

### 逻辑删除

所有业务表通过 `deleted` 字段实现逻辑删除，配置在 `BaseEntity` 中：

```java
@TableLogic
private Integer deleted;  // 0=正常, 1=删除
```

MyBatis-Plus 自动在查询 / 删除时追加 `WHERE deleted=0` / `SET deleted=1`。

> 注意：`student_course_record` 表在新增时因唯一约束 `(student_id, course_id, semester)` 需要检查历史记录，此处使用 `jdbcTemplate` 查询含已删除数据的记录数，删除时使用物理删除以避免冲突。

### 自动填充

`MyMetaObjectHandler` 自动填充 `createTime`、`updateTime`、`createBy`、`updateBy`，无需手动设置时间戳和操作人。

### 培养计划按角色过滤

`PlanController.page()` 根据不同用户类型自动过滤数据范围：

| user_type | 可见范围 |
|-----------|----------|
| `admin` | 所有计划（含 DRAFT） |
| `teacher` | 本学院已发布计划 |
| `student` | 本专业已发布计划 |

### 待办事项按角色推送

`DashboardController` 挂载在 `/auth` 路径下，提供 `GET /auth/pending-items` 端点，根据当前登录用户的角色返回不同的待处理事项：
- **管理员**：待审核大纲数 + 待发布计划数
- **教师**：被驳回大纲数 + 草稿大纲数
- **学生**：未处理预警数 + 未通过课程数

### 版本快照

培养计划的课程安排保存为 JSON 快照（`plan_snapshot` 表），支持版本回溯。快照使用 Hutool `JSONUtil` 序列化/反序列化。

### Excel 导入/导出

- **课程批量导入**：`CourseImportController` 接收 Excel 文件，逐行解析并写入
- **选课记录导入**：`StudentController.importRecords()` 使用 Hutool `ExcelUtil` 读取 Excel，逐行写入
- **报表 Excel 导出**：`StatisticsController.export()` 使用 Hutool `ExcelWriter` 动态生成并流式输出
- **报表 PDF 导出**：`PdfExportService` 使用 OpenPDF 按类型生成 PDF 文件并流式输出

### 唯一性校验

Controller 层在创建/更新时先查重，若违反唯一约束则直接返回 `400` 错误信息，避免数据库异常逃逸到客户端。

### 预警按角色过滤

`WarningController.page()` 自动识别当前用户角色——学生只能看到自己的预警记录，管理员可查看全部。

## 🧪 测试数据

`V2__test_data_v2.sql` 包含：

| 数据 | 数量 |
|------|------|
| 角色 | 3（管理员 / 教师 / 学生） |
| 用户 | 10（1 管理员 + 2 教师 + 7 学生） |
| 权限 | 60+ （菜单 + 按钮权限，含完整权限树） |
| 学院 | 2（计算机与信息工程学院 / 电子信息工程学院） |
| 专业 | 4（计科 / 软工 / 通信 / 大数据） |
| 班级 | 4 |
| 课程 | 20（通识 6 + 基础 5 + 核心 4 + 选修 3 + 实践 2） |
| 课程先修关系 | 6 |
| 培养计划 | 1（计算机 2023 级，含 8 学期课程安排） |
| 学生成绩 | 15 条（赵同学正常 / 钱同学含挂科） |
| 学业预警 | 1 条（钱同学橙色预警） |

## 📄 License

MIT
