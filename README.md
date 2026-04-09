# 原创内容创作者支持平台

一个完整的原创内容创作者支持平台，支持创作者入驻、作品发布、粉丝关注与打赏、付费内容、评论互动和数据看板等功能。

## 功能特性

### 用户端

- 🏠 **首页** - 热门创作者推荐、最新内容展示
- **发现创作者** - 分类筛选、搜索创作者
- 📝 **内容浏览** - 支持文章、视频、音频、图集多种类型
- 💰 **付费内容** - 购买解锁付费内容
- ❤️ **互动功能** - 关注、点赞、评论、打赏
- 👨‍💼 **创作者入驻** - 申请成为创作者
- ✍️ **创作中心** - 发布/编辑/管理作品
- 📊 **数据看板** - 收入、浏览、粉丝数据统计
- 💳 **账户充值** - 余额充值功能

### 管理后台

- 📈 **数据概览** - 平台整体数据统计
- 👥 **用户管理** - 用户列表与状态管理
- ✅ **创作者审核** - 审核创作者入驻申请
- 📋 **内容管理** - 内容审核与状态管理
- 📝 **操作日志** - 系统操作日志查询

## 快速开始

### 环境要求

- Docker & Docker Compose

### 一键启动所有服务

```bash
# 构建并启动所有服务
docker-compose up -d --build

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 停止所有服务
docker-compose down
```

> ⚠️ 首次启动需等待 SQL Server 初始化完成（约 1-2 分钟）
>
> 数据库会自动创建并初始化测试数据，无需手动操作

### 访问地址

| 服务       | 地址                  | 说明         |
| ---------- | --------------------- | ------------ |
| 用户端     | http://localhost:8081 | 用户前端界面 |
| 管理后台   | http://localhost:8082 | 管理员后台   |
| 后端 API   | http://localhost:8080 | REST API     |
| SQL Server | localhost:1433        | 数据库       |

## 测试账号

| 角色       | 用户名         | 密码   | 说明           |
| ---------- | -------------- | ------ | -------------- |
| 管理员     | admin          | 123456 | 可登录管理后台 |
| 普通用户   | testuser       | 123456 | 可申请创作者   |
| 科技创作者 | creator_tech   | 123456 | 已认证创作者   |
| 设计创作者 | creator_design | 123456 | 已认证创作者   |
| 美食创作者 | creator_food   | 123456 | 已认证创作者   |
| 旅行创作者 | creator_travel | 123456 | 已认证创作者   |
| 摄影创作者 | creator_photo  | 123456 | 已认证创作者   |
| 音乐创作者 | creator_music  | 123456 | 已认证创作者   |
| 粉丝用户1  | user_fan1      | 123456 | 普通粉丝用户   |
| 粉丝用户2  | user_fan2      | 123456 | 普通粉丝用户   |

> 💡 登录页面提供快捷填充测试账号功能

## 项目结构

```
├── backend/                 # Spring Boot 后端
│   ├── src/main/java/com/creator/
│   │   ├── annotation/      # 自定义注解 (@OperLog)
│   │   ├── aspect/          # AOP 切面 (操作日志)
│   │   ├── common/          # 通用类 (Result, PageResult)
│   │   ├── config/          # 配置类 (CORS, MyBatis)
│   │   ├── controller/      # 控制器
│   │   ├── dto/             # 数据传输对象
│   │   ├── entity/          # 实体类
│   │   ├── interceptor/     # 拦截器 (JWT认证)
│   │   ├── mapper/          # MyBatis Mapper
│   │   ├── service/         # 服务层
│   │   ├── util/            # 工具类 (JWT, UserContext)
│   │   └── vo/              # 视图对象
│   └── src/main/resources/
│       ├── application.yml  # 配置文件
│       └── schema.sql       # 数据库初始化脚本
│
├── frontend-user/           # Vue 3 用户端
│   └── src/
│       ├── api/             # API 接口封装
│       ├── components/      # 公共组件
│       ├── router/          # 路由配置
│       ├── stores/          # Pinia 状态管理
│       ├── styles/          # 全局样式 (SCSS)
│       └── views/           # 页面组件
│
├── frontend-admin/          # Vue 3 管理后台
│   └── src/
│       ├── api/
│       ├── layouts/
│       ├── router/
│       ├── stores/
│       ├── styles/
│       └── views/
│
├── docs/                    # 文档
│   └── project_design.md    # 系统设计文档
│
├── docker-compose.yml       # Docker 编排配置
└── README.md
```

## 技术栈

### 后端

- Java 17
- Spring Boot 3.2
- MyBatis-Plus 3.5
- SQL Server 2022
- JWT 认证
- AOP 操作日志

### 前端

- Vue 3.4 + Composition API
- Vite 5
- Element Plus 2.6
- Pinia 状态管理
- Vue Router 4
- Axios
- ECharts 数据可视化
- SCSS

### 部署

- Docker
- Docker Compose
- Nginx

## Docker 服务说明

| 容器名称               | 镜像/构建        | 端口映射  | 说明              |
| ---------------------- | ---------------- | --------- | ----------------- |
| creator-sqlserver      | mssql/server     | 1433:1433 | SQL Server 数据库 |
| creator-db-init        | mssql/server     | -         | 数据库初始化服务  |
| creator-backend        | ./backend        | 8080:8080 | Spring Boot 后端  |
| creator-frontend-user  | ./frontend-user  | 8081:80   | 用户端 Nginx      |
| creator-frontend-admin | ./frontend-admin | 8082:80   | 管理后台 Nginx    |

启动顺序：sqlserver → db-init（自动创建数据库和表） → backend → frontend

## 常用命令

```bash
# 启动所有服务
docker-compose up -d --build

# 仅重启后端
docker-compose up -d --build backend

# 仅重启前端
docker-compose up -d --build frontend-user frontend-admin

# 查看后端日志
docker-compose logs -f backend

# 进入后端容器
docker exec -it creator-backend sh

# 清理所有容器和数据
docker-compose down -v
```

## 本地开发模式

如需本地开发调试，可以只启动数据库和后端：

```bash
# 仅启动数据库和后端
docker-compose up -d sqlserver backend
```

然后本地启动前端：

```bash
# 用户端 (localhost:5173)
cd frontend-user
npm install
npm run dev

# 管理后台 (localhost:5174)
cd frontend-admin
npm install
npm run dev
```

## API 文档

详见 [系统设计文档](docs/project_design.md)

## 注意事项

1. SQL Server 容器首次启动需要较长时间初始化
2. 密码默认为 `123456`（BCrypt 加密存储）
3. Docker 模式下前端通过 Nginx 反向代理访问后端
4. 生产环境请修改 JWT 密钥和数据库密码

## 题目要求

> 用 Vue 前端框架，Spring 后端框架写一个原创内容创作者支持平台，主要模块：
>
> - 创作者入驻与作品发布
> - 粉丝关注与打赏
> - 付费内容设置
> - 评论互动
> - 数据看板
>
> 要求数据库本地获取，不需要部署上云。数据库来源 SQL Server。

## License

MIT
