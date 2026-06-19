@echo off
chcp 65001 > nul
echo 启动 kubeMall 微服务 (DEV 模式)...

echo ========================================
echo 第二步：启动微服务应用（DEV）
echo ========================================

cd /d E:\kubeMall\mall-user
start "mall-user" cmd /k "chcp 65001 > nul && mvn spring-boot:run -Dspring-boot.run.jvmArguments=-Dfile.encoding=UTF-8 -Dspring-boot.run.profiles=dev"

cd /d E:\kubeMall\mall-product
start "mall-product" cmd /k "chcp 65001 > nul && mvn spring-boot:run -Dspring-boot.run.jvmArguments=-Dfile.encoding=UTF-8 -Dspring-boot.run.profiles=dev"

cd /d E:\kubeMall\mall-gateway
start "mall-gateway" cmd /k "chcp 65001 > nul && mvn spring-boot:run -Dspring-boot.run.jvmArguments=-Dfile.encoding=UTF-8 -Dspring-boot.run.profiles=dev"

echo.
echo ========================================
echo 所有服务已启动（DEV）！
echo ========================================
echo Gateway: http://localhost:8088
echo User:    http://localhost:8081
echo Product: http://localhost:8082
echo.
echo 注意：当前使用 DEV 配置（本地数据库 / localhost）