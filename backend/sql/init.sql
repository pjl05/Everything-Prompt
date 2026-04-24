CREATE DATABASE IF NOT EXISTS everything_prompt DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE everything_prompt;

CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    nickname VARCHAR(50),
    avatar VARCHAR(255) DEFAULT '/static/default-avatar.png',
    `role` ENUM('GUEST', 'USER', 'VIP', 'ADMIN') DEFAULT 'USER',
    status TINYINT DEFAULT 1,
    last_login_time DATETIME,
    last_login_ip VARCHAR(50),
    total_ai_calls INT DEFAULT 0,
    vip_expire_time DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_username (username),
    INDEX idx_role (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE template_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    icon VARCHAR(100),
    description VARCHAR(255),
    sort_order INT DEFAULT 0,
    is_free TINYINT DEFAULT 1,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE prompt_template (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    content TEXT NOT NULL,
    category_id BIGINT NOT NULL,
    tags VARCHAR(255),
    is_premium TINYINT DEFAULT 0,
    is_official TINYINT DEFAULT 0,
    usage_count INT DEFAULT 0,
    rating DECIMAL(3,2) DEFAULT 5.00,
    status TINYINT DEFAULT 1,
    create_user_id BIGINT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO template_category (name, code, icon, description, sort_order, is_free) VALUES
('编程开发', 'code', '💻', 'Java, Python, 前端等开发模板', 1, 1),
('职场办公', 'office', '💼', 'PPT, 邮件, 报告等办公模板', 2, 1),
('论文写作', 'paper', '📝', '论文, 报告, 文献等写作模板', 3, 1),
('AI绘画', 'image', '🎨', 'Midjourney, Stable Diffusion 提示词', 4, 1),
('短视频文案', 'video', '🎬', '抖音, 快手, 视频号文案模板', 5, 1),
('学习备考', 'study', '📚', '考试, 面试, 学习计划模板', 6, 1),
('Java开发', 'java', '☕', 'Java高级工程师专属模板', 7, 0),
('数据分析', 'data', '📊', '数据分析, BI, 可视化模板', 8, 0);

INSERT INTO sys_user (username, password, email, `role`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'admin@example.com', 'ADMIN');
