@echo off
chcp 65001 > nul
echo 启动 kubeMall 微服务...

cd /d E:\kubeMall\mall-user
start "mall-user" cmd /k "chcp 65001 > nul && mvn spring-boot:run"

cd /d E:\kubeMall\mall-product
start "mall-product" cmd /k "chcp 65001 > nul && mvn spring-boot:run"

cd /d E:\kubeMall\mall-gateway
start "mall-gateway" cmd /k "chcp 65001 > nul && mvn spring-boot:run"

echo 所有服务已启动！
echo Gateway: http://localhost:8088
echo User:    http://localhost:8081
echo Product: http://localhost:8082