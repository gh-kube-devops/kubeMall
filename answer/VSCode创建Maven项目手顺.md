# 📘 VS Code 创建 Spring Boot + Maven 镜像配置实战

---

## 一、环境准备

### 1. Java 环境

```bash
java -version
```

👉 推荐：

* JDK 17（LTS）
* HotSpot / SapMachine 均可

---

### 2. Maven 环境

```bash
mvn -v
```

👉 确保：

* Maven 3.8+
* Java 版本为 17

---

## 二、使用 VS Code 创建 Spring Boot 项目

### 1. 打开命令面板

```
Ctrl + Shift + P
```

输入：

```
Spring Initializr: Create a Maven Project
```

---

### 2. 按顺序选择

| 配置项          | 值            |
| ------------ | ------------ |
| Project Type | Maven        |
| Language     | Java         |
| Spring Boot  | 3.x（建议稳定版）   |
| Group        | com.kubemall |
| Artifact     | mall-user    |
| Packaging    | Jar          |
| Java Version | 17           |

---

### 3. 依赖选择

勾选：

* Spring Web
* Spring Data JPA
* PostgreSQL Driver
* Lombok

---

### 4. 项目目录

```
E:\kubeMall\mall-user
```

---

## 三、项目结构（推荐）

```
mall-user
└── src/main/java/com/kubemall/user
    ├── controller
    ├── service
    ├── repository
    ├── entity
    ├── dto
    ├── config
    └── MallUserApplication.java
```

---

## 四、配置 Maven 镜像（解决依赖下载慢/失败）

### 1. 打开配置文件

```
E:\maruzen\apache-maven-3.8.8\conf\settings.xml
```

---

### 2. 配置 mirrors

```xml
<mirrors>
    <!-- 禁止 HTTP 仓库 -->
    <mirror>
      <id>maven-default-http-blocker</id>
      <mirrorOf>external:http:*</mirrorOf>
      <name>Block HTTP</name>
      <url>http://0.0.0.0/</url>
      <blocked>true</blocked>
    </mirror>

    <!-- 阿里云镜像 -->
    <mirror>
        <id>aliyun</id>
        <mirrorOf>*</mirrorOf>
        <name>Aliyun Maven</name>
        <url>https://maven.aliyun.com/repository/central</url>
    </mirror>
</mirrors>
```

---

### 3. （可选）代理配置

```xml
<proxies>
  <proxy>
    <id>my-proxy</id>
    <active>false</active>
    <protocol>https</protocol>
    <host>172.30.0.20</host>
    <port>8888</port>
    <nonProxyHosts>localhost|127.0.0.1</nonProxyHosts>
  </proxy>
</proxies>
```

---

## 五、构建项目

```bash
mvn clean install
```

---

### ✅ 成功标志

```
BUILD SUCCESS
```

---

## 六、启动项目

### 方法1（推荐）

```bash
mvn spring-boot:run
```

---

### 方法2

运行：

```
MallUserApplication.java
```

---

### ✅ 启动成功日志

```
Tomcat started on port 8080
Started MallUserApplication
```

---

## 七、访问验证

浏览器打开：

```
http://localhost:8080
```

---

### 👉 出现：

```
Whitelabel Error Page
```

说明：

✔ 服务启动成功
❗ 只是没有接口

---

## 八、添加测试接口

```java
package com.kubemall.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "ok";
    }
}
```

---

### 访问：

```
http://localhost:8080/test
```

---

### 返回：

```
ok
```

---

## 九、常见问题

### 1. Maven 依赖下载失败

原因：

* 网络问题
* 未配置镜像
* 代理异常

解决：

```bash
mvn -U clean install
```

---

### 2. Main method not found

原因：

* 运行了错误的类（非启动类）

解决：

* 运行 `MallUserApplication`

---

### 3. Whitelabel Error Page

原因：

* 没有接口

👉 正常现象

---

## 十、下一步

👉 连接 PostgreSQL：

* 配置数据源
* 创建表
* 实现 `/users` 接口

---

# 🚀 总结

你已经完成：

* ✔ Java + Maven 环境
* ✔ Spring Boot 项目创建
* ✔ Maven 镜像配置
* ✔ 项目成功运行

👉 已进入 **后端开发实战阶段**
