@echo off
chcp 65001 > nul
echo 启动 kubeMall 微服务...

echo.
echo ========================================
echo 第一步：安装公共模块到本地仓库
echo ========================================

echo 正在安装 mall-common-core...
cd /d E:\kubeMall\mall-common-core
call mvn clean install -DskipTests
if %errorlevel% neq 0 (
    echo mall-common-core 安装失败！
    pause
    exit /b %errorlevel%
)
echo mall-common-core 安装成功！

echo.
echo 正在安装 mall-common-web...
cd /d E:\kubeMall\mall-common-web
call mvn clean install -DskipTests
if %errorlevel% neq 0 (
    echo mall-common-web 安装失败！
    pause
    exit /b %errorlevel%
)
echo mall-common-web 安装成功！

echo.
echo ========================================
echo 第二步：启动微服务应用
echo ========================================

cd /d E:\kubeMall\mall-user
start "mall-user" cmd /k "chcp 65001 > nul && mvn spring-boot:run"

cd /d E:\kubeMall\mall-product
start "mall-product" cmd /k "chcp 65001 > nul && mvn spring-boot:run"

cd /d E:\kubeMall\mall-gateway
start "mall-gateway" cmd /k "chcp 65001 > nul && mvn spring-boot:run"

echo.
echo ========================================
echo 所有服务已启动！
echo ========================================
echo Gateway: http://localhost:8088
echo User:    http://localhost:8081
echo Product: http://localhost:8082
echo.
echo 注意：请确保公共模块安装成功，否则微服务可能启动失败！