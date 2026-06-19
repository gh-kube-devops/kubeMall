## 🐳 Docker 命令

### 基础操作

```bash
# 构建并启动所有服务
docker compose up -d --build

# 启动所有服务（不重新构建）
docker compose up -d

# 停止所有服务
docker compose down

# 停止并删除所有容器（含孤儿容器）
docker compose down --remove-orphans

# 重启指定服务
docker compose restart mall-user
docker compose restart mall-product
docker compose restart mall-gateway

# 查看所有容器状态
docker compose ps

# 查看所有容器日志（实时）
docker compose logs -f

# 查看指定服务日志（最近50行）
docker compose logs mall-user --tail=50
docker compose logs mall-product --tail=50
docker compose logs mall-gateway --tail=50

# 查看指定服务日志（实时跟踪）
docker compose logs -f mall-user

# 进入容器内部
docker exec -it kubemall-user sh
docker exec -it kubemall-product sh
docker exec -it kubemall-gateway sh
docker exec -it kubemall-postgres sh


# 重新构建指定服务镜像（不启动）
docker compose build mall-user

# 构建并启动指定服务
docker compose up -d --build mall-user

# 查看本地镜像
docker images | grep kubemall

# 删除指定镜像
docker rmi kubemall-mall-user


# 进入 PostgreSQL 命令行
docker exec -it kubemall-postgres psql -U postgres -d kubemall

# 直接执行 SQL（不进入交互模式）
docker exec -it kubemall-postgres psql -U postgres -d kubemall -c "SELECT * FROM users;"

-- 查看所有 Schema
\dn

-- 查看所有表
\dt

-- 查看指定 Schema 下的所有表
\dt user_schema.*
\dt product_schema.*

-- 查看表结构
\d user_schema.users

-- 查看数据库列表
\l

-- 切换数据库
\c kubemall

-- 退出
\q

# 创建 Schema
docker exec -it kubemall-postgres psql -U postgres -d kubemall -c "CREATE SCHEMA IF NOT EXISTS user_schema;"
docker exec -it kubemall-postgres psql -U postgres -d kubemall -c "CREATE SCHEMA IF NOT EXISTS product_schema;"

# 删除 Schema（慎用！会删除所有数据）
docker exec -it kubemall-postgres psql -U postgres -d kubemall -c "DROP SCHEMA IF EXISTS user_schema CASCADE;"

# 查看表是否存在
docker exec -it kubemall-postgres psql -U postgres -d kubemall -c "SELECT table_name FROM information_schema.tables WHERE table_schema = 'user_schema';"

# 清理并打包所有模块（跳过测试）
mvn clean package -DskipTests

# 强制更新依赖（-U）
mvn clean package -DskipTests -U

# 只打包指定模块
mvn clean package -DskipTests -pl mall-user -am

# 查看依赖树
mvn dependency:tree

# 查看生效的配置
mvn help:effective-pom > effective-pom.txt

# 完整重建流程（推荐）
mvn clean package -DskipTests && docker compose up -d --build

# 只重建 user 服务
mvn clean package -DskipTests -pl mall-user -am && docker compose up -d --build mall-user

# 停止并删除所有容器
docker compose down

# 删除所有未使用的镜像、容器、网络
docker system prune -a

# 清理 dangling 镜像
docker image prune

# 清理所有停止的容器
docker container prune

# 清理 target 目录
mvn clean

# 强制更新快照并清理
mvn clean -U

# 删除本地 Maven 缓存中的失败下载
rm -rf ~/.m2/repository/org/springframework/cloud/spring-cloud-dependencies/2025.0.0

# 查看完整日志
docker compose logs

# 查看特定服务日志
docker compose logs mall-user

# 查看最近 100 行
docker compose logs mall-user --tail=100

# 搜索错误
docker compose logs mall-user | findstr "ERROR"

# 搜索 Hibernate 建表语句
docker compose logs mall-user | findstr "Hibernate.*create"

# 进入容器查看环境变量
docker exec -it kubemall-user env | grep -i spring

# 进入容器查看配置文件
docker exec -it kubemall-user cat /app/application.yml

# 进入容器查看日志文件
docker exec -it kubemall-user cat /logs/kubemall/mall-user/mall-user.log

# 测试容器间网络连通性
docker exec -it kubemall-user ping postgres
docker exec -it kubemall-product ping postgres

# 测试端口
docker exec -it kubemall-user curl http://postgres:5432