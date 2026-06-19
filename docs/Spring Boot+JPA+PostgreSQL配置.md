# Spring Boot + JPA + PostgreSQL 配置指南

## 1. PostgreSQL 数据库创建

```sql
CREATE DATABASE kubemall;
\c kubemall;

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50),
    password VARCHAR(100),
    email VARCHAR(100)
);

INSERT INTO users (username, password, email)
VALUES ('admin', '123456', 'admin@test.com');
```
> 替换 `your_password` 为你的实际密码。

## 2. Spring Boot 项目配置

### pom.xml 依赖

```xml
<dependencies>
    <!-- Spring Boot Starter Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- PostgreSQL Driver -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>

    <!-- Spring Boot Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

### application.yml 配置

```yaml
spring:
  application:
    name: mall-user

  datasource:
    url: jdbc:postgresql://localhost:5432/kubemall
    username: postgres
    password: 123456

  jpa:
    hibernate:
      ddl-auto: none
    show-sql: true
server:
  port: 8080
```

> 替换 `your_password` 为你的 PostgreSQL 用户密码。

## 3. 创建 User 实体

```java
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    private String email;
}
```

## 4. 创建 Repository

```java
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
```

## 5. 创建 Controller

```java
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
```

## 6. 启动项目

```bash
mvn clean install -U
mvn spring-boot:run
```

访问接口：[http://localhost:8080/users](http://localhost:8080/users)

返回示例：

```json
[
  {
    "id": 1,
    "username": "admin",
    "password": "123456",
    "email": "admin@test.com"
  }
]
```

## 7. 下一步优化建议

1. **密码加密**：使用 BCrypt 加密存储密码
2. **DTO 返回**：屏蔽密码信息
3. **统一返回格式**：使用 `Result<T>` 结构返回数据
4. **异常处理**：使用 `@ControllerAdvice` 统一处理异常
5. **安全**：使用 Spring Security + JWT 控制权限
