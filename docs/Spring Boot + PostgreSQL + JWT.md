# Spring Boot + PostgreSQL + JWT

本文档整理了 User 模块从开发到测试的完整流程，包括密码加密、DTO 返回、统一返回格式、异常处理、安全配置，以及测试流程，方便快速搭建和验证。

## 1. 功能目标

1. **密码加密**：使用 BCrypt 加密存储密码  
2. **DTO 返回**：屏蔽密码信息  
3. **统一返回格式**：使用 `Result<T>` 结构返回数据  
4. **异常处理**：使用 `@ControllerAdvice` 统一处理异常  
5. **安全**：使用 Spring Security + JWT 控制权限

## 2. Maven 依赖

```xml
<!-- Spring Boot 核心依赖 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.34</version>
    <optional>true</optional>
</dependency>

<!-- Spring Security + JWT -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
```

## 3. DTO 与密码加密

```java
@Data
public class UserDto {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6)
    @JsonIgnore // 不返回密码
    private String password;

    @Email(message = "邮箱格式错误")
    private String email;

    // DTO -> Entity
    public User toUser() {
        User user = new User();
        user.setUsername(this.username);
        user.setPassword(PasswordUtil.encode(this.password)); // BCrypt
        user.setEmail(this.email);
        return user;
    }

    // Entity -> DTO
    public static UserDto from(User user) {
        UserDto dto = new UserDto();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        return dto;
    }
}
```

## 4. Controller 示例

```java
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<UserDto> register(@Valid @RequestBody UserDto dto) {
        User user = Objects.requireNonNull(userService.createUser(dto.toUser()), "创建用户返回 null");
        return Result.success(UserDto.from(user));
    }

    @PostMapping("/login")
    public Result<String> login(@Valid @RequestBody UserDto dto) {
        User user = userService.findByUsername(dto.getUsername());
        if (user == null || !PasswordUtil.matches(dto.getPassword(), user.getPassword())) {
            return Result.fail("用户名或密码错误");
        }
        String token = JwtUtil.generateToken(user.getUsername());
        return Result.success(token);
    }
}
```

## 5. 统一返回格式

```java
public class Result<T> {
    private int code; // 0 成功，非0失败
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.setCode(0);
        r.setMessage("success");
        r.setData(data);
        return r;
    }

    public static <T> Result<T> fail(String msg) {
        Result<T> r = new Result<>();
        r.setCode(-1);
        r.setMessage(msg);
        r.setData(null);
        return r;
    }
}
```

## 6. 全局异常处理

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<?>> handleValidationException(MethodArgumentNotValidException ex) {
        var fieldError = ex.getBindingResult().getFieldError();
        String msg = (fieldError != null) ? fieldError.getDefaultMessage() : "参数校验错误";
        return ResponseEntity.badRequest().body(Result.fail(msg));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<?>> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(Result.fail("服务器内部错误"));
    }
}
```

## 7.Spring Security + JWT 配置

JwtAuthenticationFilter
```java
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

JwtAuthenticationFilter
```java
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

## 8. 测试流程（Postman）

```http
POST http://localhost:8080/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "password": "123456",
  "email": "test@test.com"
}

{
    "code": 0,
    "message": "success",
    "data": {
        "username": "testuser",
        "email": "test@test.com"
    }
}
```

```http
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "123456"
}

{
    "code": 0,
    "message": "success",
    "data": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTc3NTU1OTM0MSwiZXhwIjoxNzc1NTYyOTQxfQ.EThFFP_ANVXQcEZQ4DFki0mmeXjea91upUd5KF3ylwk"
}
```

```http
GET http://localhost:8080/users
Authorization: Bearer <JWT_TOKEN>

[
    {
        "id": 1,
        "username": "admin",
        "password": "123456",
        "email": "admin@test.com"
    },
    {
        "id": 3,
        "username": "testuser",
        "password": "$2a$10$uhXkmcxj0ofZo76SECcG4.wApv8y2IF2S//Yi7OHU/yYBfsCI7o6W",
        "email": "test@test.com"
    }
]
```