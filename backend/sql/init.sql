CREATE DATABASE IF NOT EXISTS everything_prompt DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE everything_prompt;

CREATE TABLE IF NOT EXISTS sys_user (
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

CREATE TABLE IF NOT EXISTS template_category (
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

CREATE TABLE IF NOT EXISTS prompt_template (
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
    example_result TEXT COMMENT '效果示例',
    scenario VARCHAR(500) COMMENT '适用场景',
    usage_guide VARCHAR(500) COMMENT '使用说明',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 清空现有数据（重新初始化）
TRUNCATE TABLE prompt_template;
TRUNCATE TABLE template_category;

-- 插入分类（10个分类）
INSERT INTO template_category (name, code, icon, description, sort_order, is_free) VALUES
('编程开发', 'code', '💻', 'Java, Python, 前端等开发模板', 1, 1),
('职场办公', 'office', '💼', 'PPT, 邮件, 报告等办公模板', 2, 1),
('论文写作', 'paper', '📝', '论文, 报告, 文献等写作模板', 3, 1),
('AI绘画', 'image', '🎨', 'Midjourney, Stable Diffusion 提示词', 4, 1),
('短视频文案', 'video', '🎬', '抖音, 快手, 视频号文案模板', 5, 1),
('学习备考', 'study', '📚', '考试, 面试, 学习计划模板', 6, 1),
('Java开发', 'java', '☕', 'Java高级工程师专属模板', 7, 0),
('数据分析', 'data', '📊', '数据分析, BI, 可视化模板', 8, 0),
('自媒体营销', 'social', '📱', '小红书、抖音、公众号文案模板', 9, 1),
('生活服务', 'life', '🏠', '旅游、健身、食谱等生活模板', 10, 1);

-- 插入核心模板（20个）
INSERT INTO prompt_template (title, description, content, category_id, tags, is_premium, is_official, usage_count, rating, status, example_result, scenario, usage_guide) VALUES
-- 自媒体营销类（8个）
('小红书爆款标题生成', '输入产品或主题，生成吸引眼球的小红书标题', '你是一个专业的小红书文案专家。请根据我提供的主题，生成10个吸引眼球的小红书标题。\n\n主题：{topic}\n\n要求：\n1. 标题要吸引人，引起好奇心\n2. 使用emoji增加视觉效果\n3. 标题控制在20字以内\n4. 具有爆款潜质\n\n请直接输出标题，每行一个。', 9, '小红书,标题,爆款', 0, 1, 0, 5.00, 1, '1. 💄这也太绝了！化妆师都在用的秘诀\n2. 姐妹们！终于找到这款宝藏护肤品\n3. 🙌答应我！一定要试试这个方法\n4. 救命！这也太好看了吧\n5. ✨普通人逆袭！只需掌握这个技巧', '美妆护肤、产品推荐、生活技巧分享', '输入你的产品或主题，系统会生成10个吸引眼球的标题供你选择'),

('小红书种草文案', '生成具有感染力的小红书种草笔记', '你是一个专业的小红书文案专家。请帮我写一篇小红书种草笔记。\n\n产品：{product}\n产品特点：{features}\n\n要求：\n1. 语言亲切自然，像朋友推荐\n2. 包含真实使用感受\n3. 适当使用emoji\n4. 结尾引导互动', 9, '小红书,种草,文案', 0, 1, 0, 5.00, 1, '今天要跟大家分享一款我最近超爱的好物！🎉\n\n就是这款XXX，真的绝了！\n\n首先说说包装，超级有质感，打开的一瞬间就被惊艳到了。\n\n使用感受：\n✅质地清爽不油腻\n✅吸收很快\n✅用完皮肤滑滑的\n\n真心推荐给姐妹们！你们的评论区告诉我呀～', '美妆护肤、电子产品、生活好物', '输入产品名称和特点，系统生成种草文案'),

('朋友圈产品推广', '生成朋友圈产品推广文案', '你是一个微商文案专家。请帮我写一条朋友圈产品推广文案。\n\n产品：{product}\n价格：{price}\n卖点：{selling_points}\n\n要求：\n1. 简短有力，控制在100字以内\n2. 突出产品卖点\n3. 引导购买', 9, '朋友圈,推广,文案', 0, 1, 0, 5.00, 1, '🔥爆款推荐 | XXX\n\n用了XXX才知道什么叫真香！✨\n\n💰限时价：XXX元\n\n✅功效：XXX\n✅适合：XXX\n\n库存有限，点击链接抢购👇', '电商、微商、产品推广', '输入产品信息，系统生成朋友圈推广文案'),

('抖音视频脚本', '生成抖音视频拍摄脚本', '你是一个抖音内容策划专家。请帮我写一个抖音视频脚本。\n\n视频主题：{topic}\n视频时长：{duration}\n目标：{goal}\n\n要求：\n1. 开场前3秒要有爆点\n2. 结构清晰，有节奏感\n3. 结尾引导互动', 9, '抖音,视频,脚本', 0, 1, 0, 5.00, 1, '【开场】0-3秒\n哇！这个XXX也太好用了吧！\n\n【正文】3-50秒\n今天给大家分享XXX...\n首先，我们来看看...\n然后...\n最后...\n\n【结尾】50-60秒\n你们觉得怎么样？评论区告诉我！\n喜欢的话点个赞！\n\n#标签1 #标签2 #标签3', '抖音短视频内容创作', '输入视频主题和时长，系统生成完整脚本'),

('公众号文章开头', '生成吸引读者的公众号文章开头', '你是一个公众号运营专家。请帮我写一个公众号文章开头。\n\n文章主题：{topic}\n文章类型：{type}\n目标读者：{audience}\n\n要求：\n1. 前3行要有吸引力\n2. 引发读者好奇心\n3. 引出主题', 9, '公众号,开头,文章', 0, 1, 0, 5.00, 1, '你有没有遇到过这种情况？\n\n明明很努力了，结果却总是不如人意...\n\n今天我要分享的这件事，可能改变你对XXX的认知。\n\n先别急着划走，看完这个故事，你会感谢我的。', '公众号文章创作', '输入文章主题和类型，系统生成吸引人的开头'),

('产品描述文案', '生成电商产品描述', '你是一个电商文案专家。请帮我写产品描述。\n\n产品名称：{product_name}\n产品特点：{features}\n目标用户：{target_users}\n\n要求：\n1. 突出产品核心卖点\n2. 引发购买欲望\n3. 语言专业且有感染力', 9, '产品描述,电商', 0, 1, 0, 5.00, 1, '【产品名称】\n\n✨产品亮点：\n• XXX\n• XXX\n• XXX\n\n👥适合人群：\nXXX\n\n💡使用体验：\nXXX\n\n📦规格参数：\nXXX', '电商平台产品详情页', '输入产品信息，系统生成专业的产品描述'),

('品牌故事文案', '生成品牌故事', '你是一个品牌策划专家。请帮我写一个品牌故事。\n\n品牌名称：{brand_name}\n品牌理念：{philosophy}\n创始人故事：{founder_story}\n\n要求：\n1. 故事要有感染力\n2. 传递品牌价值观\n3. 引发情感共鸣', 9, '品牌,故事,文案', 0, 1, 0, 5.00, 1, '【品牌名称】的故事\n\n一切源于一个简单的念头——\n\n[创始人故事]\n\n我们相信，\n[品牌理念]\n\n这就是【品牌名称】。', '品牌宣传、企业介绍', '输入品牌信息，系统生成品牌故事'),

('活动促销文案', '生成促销活动文案', '你是一个活动策划专家。请帮我写促销文案。\n\n活动类型：{activity_type}\n产品：{product}\n优惠力度：{discount}\n\n要求：\n1. 突出优惠力度\n2. 制造紧迫感\n3. 引导行动', 9, '促销,活动,文案', 0, 1, 0, 5.00, 1, '🎉限时优惠 | [活动名称]\n\n❗注意！错过等一年！\n\n【产品名称】\n原价：XXX元\n活动价：XXX元\n\n✅福利：\n• XXX\n• XXX\n• XXX\n\n⏰活动时间：XXX\n\n立即抢购👉 [链接]', '电商促销、节日活动', '输入活动信息，系统生成促销文案'),

-- 职场办公类（8个）
('专业工作邮件', '生成专业的工作邮件', '你是一个资深职场顾问。请帮我写一封专业的工作邮件。\n\n邮件目的：{purpose}\n收件人：{recipient}\n发件人：{sender}\n邮件背景：{background}\n\n要求：\n1. 语言专业、得体\n2. 结构清晰\n3. 语气恰当\n4. 包含适当的礼貌用语', 2, '邮件,工作,办公', 0, 1, 0, 5.00, 1, '主题：关于XX项目进度汇报\n\n尊敬的XX：\n\n您好！\n\n感谢您对XX项目的关注。现将项目最新进度向您汇报如下：\n\n一、已完成工作\n1. 需求调研已完成\n2. 方案设计已通过评审\n\n二、下一步计划\n1. 预计下周完成开发\n2. 计划月底前完成测试\n\n如有任何问题，请随时联系我。\n\n此致\n敬礼\n\nXX\n2026年4月27日', '商务沟通、工作汇报、项目协调', '输入邮件目的、收件人等信息，系统生成专业邮件'),

('周报/月报生成', '生成工作总结周报月报', '你是一个职场汇报专家。请帮我写周报/月报。\n\n时间范围：{period}\n岗位：{position}\n主要工作：{work}\n\n要求：\n1. 结构清晰\n2. 成果用数据说话\n3. 问题客观分析', 2, '周报,月报,总结', 0, 1, 0, 5.00, 1, '【XX年第X周/月 工作报告】\n\n一、本周/本月工作完成情况\n1. XXX（完成度：100%）\n2. XXX（完成度：80%）\n3. XXX（进行中）\n\n二、成果数据\n• 完成XXX个项目\n• 提升效率XX%\n• 节省成本XX元\n\n三、下周/下月工作计划\n1. XXX\n2. XXX\n\n四、问题与建议\n• 遇到的问题：XXX\n• 建议解决方案：XXX', '职场汇报、绩效总结', '输入时间段和工作内容，系统生成结构化周报'),

('会议纪要整理', '整理会议内容生成纪要', '你是一个会议记录专家。请帮我整理会议纪要。\n\n会议主题：{meeting_topic}\n参会人员：{participants}\n会议内容：{content}\n\n要求：\n1. 结构清晰\n2. 突出决议事项\n3. 明确责任人和时间节点', 2, '会议,纪要,办公', 0, 1, 0, 5.00, 1, '【会议纪要】\n\n会议主题：XXX\n时间：XXX\n参会人员：XXX\n\n一、会议内容\n1. XXX\n2. XXX\n\n二、决议事项\n1. XXX（负责人：XXX 完成时间：XXX）\n2. XXX（负责人：XXX 完成时间：XXX）\n\n三、下次会议安排\n时间：XXX\n地点：XXX', '会议记录、工作备忘', '输入会议信息，系统生成规范的会议纪要'),

('PPT大纲生成', '根据主题生成PPT大纲', '你是一个PPT策划专家。请帮我生成PPT大纲。\n\n主题：{topic}\n页数：{slides}\n目标受众：{audience}\n\n要求：\n1. 结构清晰\n2. 每页有明确要点\n3. 逻辑递进', 2, 'PPT,大纲,演示', 0, 1, 0, 5.00, 1, '【PPT大纲】\n\n第1页：封面\n- 标题：XXX\n- 副标题：XXX\n\n第2页：目录\n- 01 XXX\n- 02 XXX\n- 03 XXX\n\n第3页：背景介绍\n- XXX\n- XXX\n\n第4页：核心内容\n- 要点1：XXX\n- 要点2：XXX\n- 要点3：XXX\n\n第5页：总结\n- 核心观点\n- 行动建议\n\n第6页：Q&A', '工作汇报、演讲展示', '输入主题和页数，系统生成PPT大纲'),

('商业计划书摘要', '生成商业计划书执行摘要', '你是一个商业策划专家。请帮我写商业计划书摘要。\n\n项目名称：{project_name}\n核心业务：{business}\n目标市场：{market}\n\n要求：\n1. 简洁有力\n2. 突出商业模式\n3. 展现市场机会', 2, '商业,计划书,创业', 0, 1, 0, 5.00, 1, '【执行摘要】\n\n一、项目概述\n[项目名称]致力于[核心业务]，通过[独特价值]解决[市场需求]。\n\n二、市场机会\n[目标市场]规模达[数值]，年增长[百分比]。\n\n三、商业模式\n• 收入来源：XXX\n• 目标客户：XXX\n• 渠道策略：XXX\n\n四、核心优势\n• XXX\n• XXX\n• XXX\n\n五、融资需求\n计划融资[金额]万元，用于[用途]。', '创业融资、商业计划', '输入项目信息，系统生成商业计划书摘要'),

('项目方案文档', '生成项目实施方案', '你是一个项目管理专家。请帮我写项目实施方案。\n\n项目名称：{project_name}\n项目目标：{goals}\n时间要求：{timeline}\n\n要求：\n1. 目标明确\n2. 时间节点清晰\n3. 风险可控', 2, '项目,方案,文档', 0, 1, 0, 5.00, 1, '【项目实施方案】\n\n一、项目目标\n1. XXX\n2. XXX\n\n二、项目周期\n• 启动阶段：XXX\n• 执行阶段：XXX\n• 收尾阶段：XXX\n\n三、资源配置\n• 人员：XXX\n• 预算：XXX\n• 设备：XXX\n\n四、风险管控\n• 风险1：XXX 应对措施：XXX\n• 风险2：XXX 应对措施：XXX\n\n五、验收标准\n1. XXX\n2. XXX', '项目管理、方案策划', '输入项目信息，系统生成完整实施方案'),

('工作总结', '生成工作总结文档', '你是一个职场写作专家。请帮我写工作总结。\n\n时间段：{period}\n岗位：{position}\n主要成就：{achievements}\n\n要求：\n1. 成果量化\n2. 突出贡献\n3. 客观真实', 2, '总结,工作,年度', 0, 1, 0, 5.00, 1, '【年度工作总结】\n\n一、工作概述\n本人在XXX岗位负责XXX工作，主要涉及XXX领域。\n\n二、主要工作成果\n1. XXX（成果：XXX）\n2. XXX（成果：XXX）\n3. XXX（成果：XXX）\n\n三、能力提升\n• 专业技能：XXX\n• 软技能：XXX\n\n四、不足与改进\n• 不足：XXX\n• 改进措施：XXX\n\n五、来年计划\n• 目标1：XXX\n• 目标2：XXX', '年度总结、晋升汇报', '输入时间段和工作内容，系统生成工作总结'),

('商务谈判话术', '生成商务谈判话术', '你是一个商务谈判专家。请帮我准备谈判话术。\n\n谈判场景：{scenario}\n对方立场：{their_position}\n我方底线：{our_bottom_line}\n\n要求：\n1. 专业得体\n2. 进退有度\n3. 达成目标', 2, '商务,谈判,话术', 0, 1, 0, 5.00, 1, '【商务谈判话术】\n\n开场：\n"感谢您抽出时间...我们非常重视这次合作机会"\n\n探需：\n"能否先了解一下贵司目前在这个项目上的想法？"\n\n阐述：\n"基于我们的了解，我们可以在XXX方面提供支持..."\n\n处理异议：\n"关于您提到的XXX，我们的考虑是..."\n\n成交：\n"综合来看，我们建议..."', '商务谈判、销售沟通', '输入谈判场景，系统生成谈判话术'),

-- 教育学习类（4个）
('家长会发言稿', '生成家长会发言稿', '你是一个教育专家。请帮我写家长会发言稿。\n\n年级：{grade}\n班级情况：{class_info}\n重点内容：{key_points}\n\n要求：\n1. 温和有礼\n2. 重点突出\n3. 家长能听懂', 6, '家长会,发言稿', 0, 1, 0, 5.00, 1, '各位家长：\n\n大家好！\n\n首先感谢各位家长在百忙之中抽出时间参加今天的家长会。\n\n一、班级整体情况\n本学期我们班级在XXX方面取得了XXX进步...\n\n二、学习情况分析\n大部分同学在XXX方面表现良好，同时也需要关注XXX...\n\n三、需要家长配合的事项\n1. 关注孩子的XXX\n2. 培养孩子的XXX习惯\n3. 定期与老师沟通\n\n四、个别交流\n会后有兴趣的家长可以单独交流。\n\n谢谢大家！', '学校家长会、家校沟通', '输入年级和重点内容，系统生成家长会发言稿'),

('论文开题报告', '生成论文开题报告', '你是一个学术写作专家。请帮我写论文开题报告。\n\n论文题目：{topic}\n研究领域：{field}\n研究意义：{significance}\n\n要求：\n1. 学术规范\n2. 逻辑清晰\n3. 论证充分', 3, '论文,开题,报告', 0, 1, 0, 5.00, 1, '【论文开题报告】\n\n一、选题背景\n随着XXX的发展，XXX问题日益突出，本研究具有重要的理论和实践意义。\n\n二、研究目的\n本研究旨在XXX，为XXX提供理论指导和实践参考。\n\n三、研究内容\n1. XXX\n2. XXX\n3. XXX\n\n四、研究方法\n• 文献研究法\n• 案例分析法\n• 实证研究法\n\n五、预期成果\n通过研究，预期可以XXX。\n\n六、参考文献\n[列出主要参考文献]', '学术论文开题', '输入论文题目和研究领域，系统生成开题报告'),

('读书笔记模板', '生成读书笔记', '你是一个阅读专家。请帮我写读书笔记。\n\n书名：{book_name}\n作者：{author}\n阅读目的：{purpose}\n\n要求：\n1. 抓重点\n2. 有自己的思考\n3. 实践导向', 6, '读书笔记,学习', 0, 1, 0, 5.00, 1, '【读书笔记】《XXX》\n\n📖基本信息\n作者：XXX\n阅读时间：XXX\n\n📝核心内容\n1. 主要观点：XXX\n2. 重要概念：XXX\n3. 关键论据：XXX\n\n💡我的思考\n• 对我冲击最大的观点：XXX\n• 我的理解：XXX\n• 与我已有知识的联系：XXX\n\n🎯实践应用\n• 我将如何应用：XXX\n• 具体行动：XXX\n\n⭐评分：★★★★☆\n推荐指数：XXX', '读书学习、知识管理', '输入书名和阅读目的，系统生成结构化读书笔记'),

('面试自我介绍', '生成面试自我介绍', '你是一个求职顾问。请帮我写面试自我介绍。\n\n应聘岗位：{position}\n工作经历：{experience}\n个人优势：{strengths}\n\n要求：\n1. 简洁有力\n2. 突出匹配度\n3. 展现个人特色', 6, '面试,自我介绍', 0, 1, 0, 5.00, 1, '面试官好，我是XXX。\n\n我有X年XXX相关经验，之前在XXX公司担任XXX岗位。\n\n在工作中，我主要负责XXX，取得了XXX成果：\n• XXX（用数据说话）\n• XXX\n\n我应聘贵司XXX岗位，是因为：\n1. XXX（行业/公司吸引力）\n2. XXX（岗位匹配度）\n3. XXX（个人发展）\n\n如果有机会加入贵司，我会XXX（承诺）。\n\n谢谢！', '求职面试、自我展示', '输入应聘岗位和经历，系统生成面试自我介绍');

-- 插入管理员用户
INSERT INTO sys_user (username, password, email, `role`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'admin@example.com', 'ADMIN');

-- 对话会话表
CREATE TABLE IF NOT EXISTS chat_session (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(100) DEFAULT '新对话',
    template_id BIGINT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 聊天消息表
CREATE TABLE IF NOT EXISTS chat_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL COMMENT 'user/assistant',
    content TEXT NOT NULL,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_session_id (session_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 提示词分享表
CREATE TABLE IF NOT EXISTS prompt_share (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '分享用户ID',
    template_id BIGINT COMMENT '关联模板ID',
    prompt_title VARCHAR(100) COMMENT '提示词标题',
    prompt_content TEXT COMMENT '分享的提示词内容',
    ai_result TEXT COMMENT 'AI生成结果示例',
    description TEXT COMMENT '分享描述',
    usage_count INT DEFAULT 0 COMMENT '使用次数',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    status TINYINT DEFAULT 1 COMMENT '状态：0隐藏 1展示',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_template_id (template_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 分享点赞表
CREATE TABLE IF NOT EXISTS share_like (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    share_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_share_user (share_id, user_id),
    INDEX idx_share_id (share_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
