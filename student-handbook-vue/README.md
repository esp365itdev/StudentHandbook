# 学生手册管理系统 Vue 前端

这是一个基于 Vue 3 + Element Plus 的学生手册管理系统前端项目。

## 功能特点

- 显示学生手册列表，包含以下字段：
  - 科目及老师
  - 类别
  - 标题
  - 时间（格式为2/9/2025-2/9/2025）
  - 内容
- 支持增删改查操作
- 支持搜索和分页功能

## 技术栈

- Vue 3
- Element Plus
- Axios
- Vite

## 项目结构

```
student-handbook-vue/
├── src/
│   ├── components/
│   │   └── StudentHandbook.vue  # 学生手册组件
│   ├── App.vue                  # 根组件
│   └── main.js                  # 入口文件
├── index.html                   # HTML模板
├── package.json                 # 项目配置
└── vite.config.js              # Vite配置
```

## 安装和运行

1. 安装依赖：
   ```bash
   npm install
   ```

2. 开发环境运行：
   ```bash
   npm run dev
   ```

3. 构建生产环境：
   ```bash
   npm run build
   ```

## API 接口

项目通过代理方式访问后端API：

- 代理地址：`/api` -> `http://localhost:80`
- 学生手册相关接口：
  - GET    `/api/system/handbook/list`      获取学生手册列表
  - GET    `/api/system/handbook/{id}`      获取学生手册详情
  - POST   `/api/system/handbook`           新增学生手册
  - PUT    `/api/system/handbook/{id}`      修改学生手册
  - DELETE `/api/system/handbook/{id}`      删除学生手册
  - DELETE `/api/system/handbook/batch`     批量删除学生手册

## 组件说明

### StudentHandbook.vue

这是学生手册管理的主要组件，包含以下功能：

1. 数据表格展示学生手册信息
2. 搜索功能（按科目及老师、类别、标题搜索）
3. 分页功能
4. 新增、编辑、删除操作
5. 批量删除功能

### 表格列说明

| 列名 | 字段 | 说明 |
|------|------|------|
| 科目及老师 | subjectTeacher | 显示科目及老师信息 |
| 类别 | category | 显示手册类别 |
| 标题 | title | 显示手册标题 |
| 时间 | startTime-endTime | 显示开始时间到结束时间 |
| 内容 | content | 显示手册内容 |
| 操作 | - | 包含编辑和删除按钮 |

## 常见问题解决

### 1. Element Plus 版本问题

如果遇到如下错误：
```
npm error code ETARGET
npm error notarget No matching version found for element-plus@^1.8.0.
```

这是因为指定的Element Plus版本可能不存在。项目已更新为使用较新版本：
```json
"element-plus": "^2.2.28",
"@element-plus/icons-vue": "^2.0.10"
```

### 2. 清除npm缓存

如果仍然遇到安装问题，可以尝试清除npm缓存：
```bash
npm cache clean --force
```

然后重新安装依赖：
```bash
npm install
```

## 注意事项

1. 项目使用 Element Plus 组件库
2. 时间格式按照需求显示为 "2/9/2025-2/9/2025" 格式
3. 项目通过 Vite 构建，支持热重载
4. API 接口通过代理方式访问后端服务