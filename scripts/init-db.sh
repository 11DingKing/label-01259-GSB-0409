#!/bin/bash

# 数据库服务器地址（Docker 网络中使用服务名）
DB_HOST=${DB_HOST:-sqlserver}

echo "等待 SQL Server ($DB_HOST) 启动..."
sleep 10

# 循环检测 SQL Server 是否就绪
for i in {1..60}; do
    /opt/mssql-tools18/bin/sqlcmd -S "$DB_HOST" -U sa -P "$SA_PASSWORD" -C -Q "SELECT 1" > /dev/null 2>&1
    if [ $? -eq 0 ]; then
        echo "SQL Server 已就绪"
        break
    fi
    echo "等待 SQL Server 就绪... ($i/60)"
    sleep 3
done

# 再次确认连接
/opt/mssql-tools18/bin/sqlcmd -S "$DB_HOST" -U sa -P "$SA_PASSWORD" -C -Q "SELECT 1" > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "无法连接到 SQL Server，退出"
    exit 1
fi

# 执行初始化脚本
echo "开始执行数据库初始化脚本..."
/opt/mssql-tools18/bin/sqlcmd -S "$DB_HOST" -U sa -P "$SA_PASSWORD" -C -i /scripts/init-db.sql

if [ $? -eq 0 ]; then
    echo "数据库初始化成功！"
else
    echo "数据库初始化失败！"
    exit 1
fi
