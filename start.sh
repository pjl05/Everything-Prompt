#!/bin/bash

# ======================
# Everything-Prompt 启动脚本
# ======================

echo "=========================================="
echo "   Everything-Prompt 启动向导"
echo "=========================================="
echo ""

# 检查 Docker
if ! command -v docker &> /dev/null; then
    echo "❌ Docker 未安装，请先安装 Docker Desktop"
    exit 1
fi

if ! docker info &> /dev/null; then
    echo "❌ Docker 未运行，请启动 Docker Desktop"
    exit 1
fi

echo "✅ Docker 已就绪"

# 启动数据库服务
echo ""
echo "📦 启动 MySQL 和 Redis..."
docker-compose up -d

# 等待 MySQL 启动
echo "⏳ 等待 MySQL 启动完成..."
sleep 5

# 初始化数据库
echo "📊 初始化数据库..."
docker exec everything-prompt-mysql mysql -uroot -ppassword -e "CREATE DATABASE IF NOT EXISTS everything_prompt;" 2>/dev/null

echo ""
echo "=========================================="
echo "   服务启动完成！"
echo "=========================================="
echo ""
echo "📌 数据库服务:"
echo "   - MySQL: localhost:3306"
echo "   - Redis: localhost:6379"
echo ""
echo "📌 后端启动:"
echo "   cd backend && mvn spring-boot:run"
echo ""
echo "📌 前端启动:"
echo "   cd frontend && npm install && npm run dev"
echo ""
