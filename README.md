# 学生手册管理系统

这是一个基于 Spring Boot + Vue 3 的学生手册管理系统，包含完整的前后端分离架构。

## 项目概述

学生手册管理系统旨在为学校或教育机构提供一个管理学生手册信息的平台。系统支持学生手册的增删改查、搜索、分页等功能，并提供响应式的用户界面。

## 技术栈

### 后端技术
- Spring Boot 2.x
- MyBatis
- MySQL
- Maven
- Thymeleaf
- Swagger3 (API文档)

### 前端技术
- Vue 3
- Element Plus (UI组件库)
- Axios (HTTP客户端)
- Vite (构建工具)

## 项目结构

```
StudentHandbook/
├── sp-api/                    # 后端API服务
│   ├── src/main/java/com/sp/  # Java源代码
│   └── src/main/resources/    # 配置文件
├── sp-common/                 # 通用工具模块
├── sp-framework/              # 框架模块
├── sp-system/                 # 系统模块
├── student-handbook-vue/      # Vue前端项目
│   ├── src/components/        # Vue组件
│   ├── src/config/            # 配置文件
│   └── src/utils/             # 工具函数
├── sql/                       # 数据库脚本
└── pom.xml                    # Maven项目配置
```

## 功能特点

### 后端功能
- 基于Spring Boot的RESTful API
- 用户权限管理
- 数据库操作和事务管理
- 文件上传功能
- XSS攻击防护
- API文档自动生成

### 前端功能
- 学生手册列表展示（科目及老师、类别、标题、时间、内容）
- 增删改查操作
- 搜索和分页功能
- 响应式设计，支持多端访问

## 环境要求

- JDK 8+
- Node.js 16+
- MySQL 5.7+
- Maven 3.6+

## 安装和运行

### 1. 数据库配置

1. 创建数据库并执行 [sql/student_handbook.sql](./sql/student_handbook.sql) 脚本
2. 修改 [sp-api/src/main/resources/application.yml](./sp-api/src/main/resources/application.yml) 中的数据库连接信息

### 2. 后端运行

1. 进入后端项目目录：
   ```bash
   cd sp-api
   ```

2. 使用Maven构建项目：
   ```bash
   mvn clean install
   ```

3. 启动后端服务：
   ```bash
   mvn spring-boot:run
   ```

   或者打包后运行：
   ```bash
   java -jar target/sp-api.jar
   ```

### 3. 前端运行

1. 进入前端项目目录：
   ```bash
   cd student-handbook-vue
   ```

2. 安装依赖：
   ```bash
   npm install
   ```

3. 启动开发服务器：
   ```bash
   npm run dev
   ```

## API 接口

后端提供以下主要API接口：
- GET    `/api/system/handbook/list`      获取学生手册列表
- GET    `/api/system/handbook/{id}`      获取学生手册详情
- POST   `/api/system/handbook`           新增学生手册
- PUT    `/api/system/handbook/{id}`      修改学生手册
- DELETE `/api/system/handbook/{id}`      删除学生手册
- DELETE `/api/system/handbook/batch`     批量删除学生手册

## 配置说明

项目默认配置：
- 后端服务端口：8003
- 前端开发端口：3000 (默认)
- 数据库连接：请根据实际环境修改配置文件

## 项目特色

1. **前后端分离架构** - 采用现代化的开发模式
2. **响应式设计** - 适配不同尺寸的设备
3. **模块化开发** - 便于维护和扩展
4. **安全性** - 包含XSS防护等安全措施
5. **自动化构建** - 通过Maven插件自动构建前端资源

## 常见问题

1. 如果遇到Element Plus版本问题，可更新依赖版本
2. 确保前后端端口配置正确，以支持API调用
3. 数据库连接信息需要根据实际环境进行配置

## 许可证

项目版权年份：2025