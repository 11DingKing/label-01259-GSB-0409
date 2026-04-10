-- 原创内容创作者支持平台 数据库初始化脚本
-- 数据库: SQL Server

-- 用户表
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'sys_user')
CREATE TABLE sys_user (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(50) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    nickname NVARCHAR(50),
    avatar NVARCHAR(500),
    email NVARCHAR(100),
    balance DECIMAL(12,2) DEFAULT 0.00,
    role INT DEFAULT 0,
    status INT DEFAULT 1,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    deleted INT DEFAULT 0,
    version INT DEFAULT 0
);

-- 创作者表
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'creator')
CREATE TABLE creator (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    pen_name NVARCHAR(50) NOT NULL,
    bio NVARCHAR(MAX),
    category NVARCHAR(50),
    cover_image NVARCHAR(500),
    status INT DEFAULT 0,
    follower_count INT DEFAULT 0,
    content_count INT DEFAULT 0,
    total_income DECIMAL(12,2) DEFAULT 0.00,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    deleted INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES sys_user(id)
);

-- 内容表
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'content')
CREATE TABLE content (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    creator_id BIGINT NOT NULL,
    title NVARCHAR(200) NOT NULL,
    summary NVARCHAR(500),
    content NVARCHAR(MAX) NOT NULL,
    cover_image NVARCHAR(500),
    media_url NVARCHAR(MAX),
    content_type INT DEFAULT 1,
    is_paid INT DEFAULT 0,
    price DECIMAL(10,2) DEFAULT 0.00,
    view_count INT DEFAULT 0,
    like_count INT DEFAULT 0,
    comment_count INT DEFAULT 0,
    status INT DEFAULT 1,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    deleted INT DEFAULT 0,
    FOREIGN KEY (creator_id) REFERENCES creator(id)
);

-- 关注表
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'follow')
CREATE TABLE follow (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    creator_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES sys_user(id),
    FOREIGN KEY (creator_id) REFERENCES creator(id),
    CONSTRAINT uk_follow UNIQUE (user_id, creator_id)
);

-- 评论表
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'comment')
CREATE TABLE comment (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    content_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    parent_id BIGINT DEFAULT 0,
    comment_text NVARCHAR(MAX) NOT NULL,
    like_count INT DEFAULT 0,
    status INT DEFAULT 1,
    created_at DATETIME DEFAULT GETDATE(),
    deleted INT DEFAULT 0,
    FOREIGN KEY (content_id) REFERENCES content(id),
    FOREIGN KEY (user_id) REFERENCES sys_user(id)
);

-- 打赏表
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'reward')
CREATE TABLE reward (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    creator_id BIGINT NOT NULL,
    content_id BIGINT,
    amount DECIMAL(10,2) NOT NULL,
    message NVARCHAR(200),
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES sys_user(id),
    FOREIGN KEY (creator_id) REFERENCES creator(id)
);

-- 购买记录表
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'purchase')
CREATE TABLE purchase (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    content_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES sys_user(id),
    FOREIGN KEY (content_id) REFERENCES content(id),
    CONSTRAINT uk_purchase UNIQUE (user_id, content_id)
);

-- 操作日志表
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'operation_log')
CREATE TABLE operation_log (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT,
    username NVARCHAR(50),
    operation NVARCHAR(100),
    method NVARCHAR(200),
    params NVARCHAR(MAX),
    ip NVARCHAR(50),
    duration BIGINT,
    created_at DATETIME DEFAULT GETDATE()
);

-- 点赞记录表
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'content_like')
CREATE TABLE content_like (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    content_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES sys_user(id),
    FOREIGN KEY (content_id) REFERENCES content(id),
    CONSTRAINT uk_content_like UNIQUE (user_id, content_id)
);

-- =====================================================
-- 插入测试数据
-- 密码统一为: 123456 (BCrypt加密)
-- =====================================================

-- 清空现有数据（按依赖顺序）
DELETE FROM content_like;
DELETE FROM purchase;
DELETE FROM reward;
DELETE FROM comment;
DELETE FROM follow;
DELETE FROM content;
DELETE FROM creator;
DELETE FROM sys_user;
DELETE FROM operation_log;

-- 重置自增ID
DBCC CHECKIDENT ('sys_user', RESEED, 0);
DBCC CHECKIDENT ('creator', RESEED, 0);
DBCC CHECKIDENT ('content', RESEED, 0);
DBCC CHECKIDENT ('follow', RESEED, 0);
DBCC CHECKIDENT ('comment', RESEED, 0);
DBCC CHECKIDENT ('reward', RESEED, 0);

-- 插入用户数据
INSERT INTO sys_user (username, password, nickname, avatar, email, balance, role, status) VALUES
('admin', '$2a$10$owef6gNlm.McXbfgDHOf5uOKC5uBg3mo/VgY46PEfT8Y58I9qaVk2', N'系统管理员', 'https://api.dicebear.com/7.x/avataaars/svg?seed=admin', 'admin@creator.com', 0, 2, 1),
('testuser', '$2a$10$owef6gNlm.McXbfgDHOf5uOKC5uBg3mo/VgY46PEfT8Y58I9qaVk2', N'测试用户', 'https://api.dicebear.com/7.x/avataaars/svg?seed=test', 'test@creator.com', 5000.00, 0, 1),
('creator_tech', '$2a$10$owef6gNlm.McXbfgDHOf5uOKC5uBg3mo/VgY46PEfT8Y58I9qaVk2', N'科技小明', 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150&h=150&fit=crop', 'tech@creator.com', 12580.00, 1, 1),
('creator_design', '$2a$10$owef6gNlm.McXbfgDHOf5uOKC5uBg3mo/VgY46PEfT8Y58I9qaVk2', N'设计师Luna', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150&h=150&fit=crop', 'design@creator.com', 25800.00, 1, 1),
('creator_food', '$2a$10$owef6gNlm.McXbfgDHOf5uOKC5uBg3mo/VgY46PEfT8Y58I9qaVk2', N'美食家老王', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop', 'food@creator.com', 38200.00, 1, 1),
('creator_travel', '$2a$10$owef6gNlm.McXbfgDHOf5uOKC5uBg3mo/VgY46PEfT8Y58I9qaVk2', N'旅行者阿杰', 'https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=150&h=150&fit=crop', 'travel@creator.com', 45600.00, 1, 1),
('creator_photo', '$2a$10$owef6gNlm.McXbfgDHOf5uOKC5uBg3mo/VgY46PEfT8Y58I9qaVk2', N'摄影师小雨', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150&h=150&fit=crop', 'photo@creator.com', 32100.00, 1, 1),
('creator_music', '$2a$10$owef6gNlm.McXbfgDHOf5uOKC5uBg3mo/VgY46PEfT8Y58I9qaVk2', N'音乐人小李', 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop', 'music@creator.com', 18900.00, 1, 1),
('user_fan1', '$2a$10$owef6gNlm.McXbfgDHOf5uOKC5uBg3mo/VgY46PEfT8Y58I9qaVk2', N'忠实粉丝小红', 'https://api.dicebear.com/7.x/avataaars/svg?seed=fan1', 'fan1@user.com', 800.00, 0, 1),
('user_fan2', '$2a$10$owef6gNlm.McXbfgDHOf5uOKC5uBg3mo/VgY46PEfT8Y58I9qaVk2', N'热心读者大刘', 'https://api.dicebear.com/7.x/avataaars/svg?seed=fan2', 'fan2@user.com', 1200.00, 0, 1);

-- 插入创作者数据
INSERT INTO creator (user_id, pen_name, bio, category, cover_image, status, follower_count, content_count, total_income) VALUES
(3, N'小明说科技', N'资深科技博主，专注AI、区块链、智能硬件领域。每周深度解析科技趋势，帮你把握未来风口。前大厂技术专家，10年行业经验。', N'科技', 'https://images.unsplash.com/photo-1518770660439-4636190af475?w=1200&h=400&fit=crop', 1, 12580, 48, 125800.00),
(4, N'Luna的设计日记', N'UI/UX设计师，Dribbble Top 100。分享设计灵感、Figma技巧、作品集打造经验。帮助设计师提升审美与技能，打造个人品牌。', N'设计', 'https://images.unsplash.com/photo-1561070791-2526d30994b5?w=1200&h=400&fit=crop', 1, 8960, 36, 89600.00),
(5, N'老王的美食厨房', N'米其林三星主厨，30年烹饪经验。从家常小炒到法式大餐，用最简单的方式教你做出餐厅级美味。美食是爱的表达。', N'美食', 'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=1200&h=400&fit=crop', 1, 25600, 128, 256000.00),
(6, N'镜头下的世界', N'环球旅行摄影师，足迹遍布50+国家。用镜头记录世界的美好，分享旅行攻略与摄影技巧。生活不止眼前，还有诗和远方。', N'旅行', 'https://images.unsplash.com/photo-1488646953014-85cb44e25828?w=1200&h=400&fit=crop', 1, 18900, 86, 189000.00),
(7, N'光影魔术师', N'商业摄影师，擅长人像、产品、风光摄影。分享后期修图技巧、布光方案、器材评测。让每一张照片都讲述故事。', N'摄影', 'https://images.unsplash.com/photo-1542038784456-1ea8e935640e?w=1200&h=400&fit=crop', 1, 15200, 62, 152000.00),
(8, N'音乐制作人小李', N'独立音乐人，网易云音乐人气创作者。分享编曲教程、混音技巧、音乐制作心得。用音乐连接每一个热爱生活的灵魂。', N'音乐', 'https://images.unsplash.com/photo-1511379938547-c1f69419868d?w=1200&h=400&fit=crop', 1, 9800, 45, 98000.00);

-- 插入内容数据
INSERT INTO content (creator_id, title, summary, content, cover_image, content_type, is_paid, price, view_count, like_count, comment_count, status) VALUES
-- 科技类内容
(1, N'2024年AI发展趋势深度解析', N'从GPT-5到AGI，全面解读人工智能的未来走向', N'人工智能正在以前所未有的速度改变我们的世界。本文将从技术突破、商业应用、伦理挑战三个维度，深入分析2024年AI领域最值得关注的发展趋势。

## 一、大语言模型的进化

GPT-4的发布标志着大语言模型进入了新纪元。我们预计2024年将看到：
- 多模态能力的进一步增强
- 推理能力的显著提升
- 更长的上下文窗口

## 二、AI Agent的崛起

自主AI代理将成为下一个风口...', 'https://images.unsplash.com/photo-1677442136019-21780ecad995?w=800&h=450&fit=crop', 1, 0, 0.00, 15680, 892, 156, 1),

(1, N'区块链技术在供应链中的应用实践', N'如何用区块链解决供应链信任问题', N'区块链技术正在重塑全球供应链管理。本文结合实际案例，详细介绍区块链在溯源、防伪、金融等场景的应用...', 'https://images.unsplash.com/photo-1639762681485-074b7f938ba0?w=800&h=450&fit=crop', 1, 1, 19.90, 8920, 456, 89, 1),

(1, N'智能家居生态系统搭建指南', N'从零开始打造你的智能家居', N'想要拥有一个科技感满满的智能家居？本文将手把手教你如何选择设备、搭建系统、实现自动化...', 'https://images.unsplash.com/photo-1558002038-1055907df827?w=800&h=450&fit=crop', 1, 0, 0.00, 12350, 678, 234, 1),

-- 设计类内容
(2, N'2024年UI设计趋势完全指南', N'掌握这些趋势，让你的设计领先一步', N'设计趋势每年都在变化，但优秀的设计师总能把握住时代的脉搏。本文总结了2024年最重要的UI设计趋势：

## 1. 3D元素的广泛应用
## 2. 玻璃拟态设计
## 3. 大胆的色彩搭配
## 4. 微交互动效...', 'https://images.unsplash.com/photo-1561070791-2526d30994b5?w=800&h=450&fit=crop', 1, 0, 0.00, 9860, 567, 123, 1),

(2, N'Figma高级技巧：提升10倍工作效率', N'这些技巧让你的设计工作事半功倍', N'Figma已经成为设计师的必备工具。但你真的会用吗？本文分享20个高级技巧...', 'https://images.unsplash.com/photo-1581291518633-83b4ebd1d83e?w=800&h=450&fit=crop', 1, 1, 29.90, 6780, 389, 78, 1),

(2, N'设计师作品集打造完全手册', N'如何让你的作品集脱颖而出', N'一份优秀的作品集是设计师求职的敲门砖。本文从内容策划、视觉呈现、案例包装三个方面...', 'https://images.unsplash.com/photo-1558655146-9f40138edfeb?w=800&h=450&fit=crop', 1, 1, 39.90, 5420, 312, 56, 1),

-- 美食类内容
(3, N'米其林主厨的家常菜秘籍', N'用专业技巧做出不一样的家常味道', N'谁说家常菜不能有米其林的品质？作为一名有30年经验的主厨，我将分享如何用简单的食材和技巧，做出让家人惊艳的美味...', 'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=800&h=450&fit=crop', 1, 0, 0.00, 28900, 1560, 345, 1),

(3, N'完美牛排的烹饪艺术', N'从选肉到摆盘，全流程详解', N'一块完美的牛排需要精确的温度控制和时间把握。本文将详细介绍如何在家做出餐厅级别的牛排...', 'https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=800&h=450&fit=crop', 1, 1, 15.90, 18600, 923, 189, 1),

(3, N'中式糕点制作入门', N'传统手艺，现代演绎', N'中式糕点承载着深厚的文化底蕴。本文将教你制作桂花糕、绿豆糕、马蹄糕等经典点心...', 'https://images.unsplash.com/photo-1558961363-fa8fdf82db35?w=800&h=450&fit=crop', 1, 0, 0.00, 15200, 867, 156, 1),

-- 旅行类内容
(4, N'日本深度游：那些小众却惊艳的地方', N'避开人潮，发现不一样的日本', N'去过东京、大阪、京都？这次带你探索日本那些鲜为人知却美到窒息的小众目的地...', 'https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?w=800&h=450&fit=crop', 1, 0, 0.00, 32500, 1890, 423, 1),

(4, N'背包客的欧洲穷游攻略', N'用最少的钱，看最美的风景', N'欧洲旅行不一定要花大钱。本文分享如何用每天50欧元的预算，玩转欧洲各国...', 'https://images.unsplash.com/photo-1467269204594-9661b134dd2b?w=800&h=450&fit=crop', 1, 1, 25.90, 21800, 1234, 267, 1),

(4, N'冰岛极光摄影之旅', N'追逐北极光的完整指南', N'冰岛是观赏极光的最佳目的地之一。本文将分享最佳观测时间、地点选择、摄影技巧...', 'https://images.unsplash.com/photo-1531366936337-7c912a4589a7?w=800&h=450&fit=crop', 1, 1, 35.90, 16700, 978, 189, 1),

-- 摄影类内容
(5, N'人像摄影布光完全指南', N'从自然光到棚拍，全面掌握布光技巧', N'光线是摄影的灵魂。本文将系统介绍人像摄影中的各种布光方案，从简单的自然光到复杂的多灯布光...', 'https://images.unsplash.com/photo-1542038784456-1ea8e935640e?w=800&h=450&fit=crop', 1, 0, 0.00, 14500, 823, 167, 1),

(5, N'Lightroom调色预设分享', N'10套电影感调色预设免费下载', N'调色是后期处理的关键环节。本文分享我常用的10套调色预设，涵盖日系、电影、复古等风格...', 'https://images.unsplash.com/photo-1493863641943-9b68992a8d07?w=800&h=450&fit=crop', 1, 1, 19.90, 11200, 645, 134, 1),

-- 音乐类内容
(6, N'零基础学编曲：从入门到精通', N'用FL Studio创作你的第一首歌', N'想要创作自己的音乐？本教程将从零开始，教你使用FL Studio进行音乐制作...', 'https://images.unsplash.com/photo-1511379938547-c1f69419868d?w=800&h=450&fit=crop', 1, 0, 0.00, 9800, 534, 98, 1),

(6, N'混音技巧进阶：让你的作品更专业', N'专业混音师的秘密武器', N'混音是音乐制作中最关键的环节之一。本文将分享EQ、压缩、混响等效果器的高级用法...', 'https://images.unsplash.com/photo-1598488035139-bdbb2231ce04?w=800&h=450&fit=crop', 1, 1, 49.90, 6500, 378, 67, 1);

-- 插入关注数据
INSERT INTO follow (user_id, creator_id, created_at) VALUES
(2, 1, DATEADD(day, -30, GETDATE())),
(2, 2, DATEADD(day, -25, GETDATE())),
(2, 3, DATEADD(day, -20, GETDATE())),
(9, 1, DATEADD(day, -15, GETDATE())),
(9, 3, DATEADD(day, -10, GETDATE())),
(9, 4, DATEADD(day, -5, GETDATE())),
(10, 2, DATEADD(day, -12, GETDATE())),
(10, 5, DATEADD(day, -8, GETDATE())),
(10, 6, DATEADD(day, -3, GETDATE()));

-- 插入评论数据
INSERT INTO comment (content_id, user_id, parent_id, comment_text, like_count, created_at) VALUES
(1, 2, 0, N'写得太好了！对AI的分析非常到位，期待更多这样的深度内容。', 45, DATEADD(day, -5, GETDATE())),
(1, 9, 0, N'作为一个AI从业者，觉得这篇文章的观点很有见地。', 32, DATEADD(day, -4, GETDATE())),
(1, 10, 1, N'同意！特别是关于AI Agent的部分，确实是未来的趋势。', 18, DATEADD(day, -3, GETDATE())),
(4, 2, 0, N'Luna老师的设计课程真的很棒，学到了很多实用技巧！', 28, DATEADD(day, -6, GETDATE())),
(4, 9, 0, N'这些设计趋势总结得很全面，已收藏。', 15, DATEADD(day, -5, GETDATE())),
(7, 10, 0, N'老王的菜谱太实用了，昨天按照教程做了一道，家人都说好吃！', 56, DATEADD(day, -2, GETDATE())),
(7, 2, 0, N'终于找到一个讲得清楚的美食博主了，关注！', 23, DATEADD(day, -1, GETDATE())),
(10, 9, 0, N'日本的这些小众景点太美了，下次去一定要打卡！', 34, DATEADD(day, -4, GETDATE())),
(13, 2, 0, N'布光教程讲得很详细，对新手很友好。', 19, DATEADD(day, -3, GETDATE())),
(15, 10, 0, N'一直想学编曲，这个教程正好入门！', 12, DATEADD(day, -2, GETDATE()));

-- 插入打赏数据
INSERT INTO reward (user_id, creator_id, content_id, amount, message, created_at) VALUES
(2, 1, 1, 50.00, N'感谢分享，内容很有价值！', DATEADD(day, -5, GETDATE())),
(9, 1, 1, 20.00, N'支持一下！', DATEADD(day, -4, GETDATE())),
(10, 3, 7, 100.00, N'老王的菜谱太棒了，必须打赏！', DATEADD(day, -3, GETDATE())),
(2, 4, 10, 30.00, N'旅行攻略很实用，谢谢分享', DATEADD(day, -2, GETDATE())),
(9, 2, 4, 25.00, N'设计干货满满', DATEADD(day, -1, GETDATE())),
(10, 5, 13, 15.00, N'摄影教程很专业', DATEADD(day, -1, GETDATE())),
(2, 6, 15, 20.00, N'音乐制作入门好教程', GETDATE());

-- 插入购买记录
INSERT INTO purchase (user_id, content_id, amount, created_at) VALUES
(2, 2, 19.90, DATEADD(day, -10, GETDATE())),
(2, 5, 29.90, DATEADD(day, -8, GETDATE())),
(9, 8, 15.90, DATEADD(day, -6, GETDATE())),
(9, 11, 25.90, DATEADD(day, -4, GETDATE())),
(10, 6, 39.90, DATEADD(day, -5, GETDATE())),
(10, 12, 35.90, DATEADD(day, -3, GETDATE())),
(2, 16, 49.90, DATEADD(day, -1, GETDATE()));

PRINT N'数据库初始化完成！';
