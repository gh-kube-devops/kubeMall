# JVM（HotSpot）+ JIT + GC（DevOps / 面试实战版）

---

# 一、整体一句话（开场直接说）

👉 **JVM（HotSpot）通过 JIT 编译优化热点代码执行性能，通过 GC 管理内存，在 K8s 环境下需要结合容器资源限制进行调优。**

---

# 二、JVM（HotSpot）在干什么？

## 核心职责

1. 加载类（ClassLoader）
2. 执行代码（解释 + JIT）
3. 管理内存（GC）

---

## 运行流程（必须能讲）

👉 请求进来：

1. Java代码 → 字节码（.class）
2. JVM加载类
3. 先解释执行
4. **热点代码 → JIT 编译（变机器码）**
5. GC 回收不用的对象

---

# 三、JIT（重点：性能）

## 是什么？

👉 Just-In-Time 编译器

---

## 干什么？

👉 把“高频执行代码”编译成本地机器码

---

## 为什么重要？

* 解释执行：慢 ❌
* 编译执行：快 ✅

---

## 面试一句话

👉 **JIT 会识别热点代码并编译为机器码，提高运行效率。**

---

## K8s里的影响（重点🔥）

👉 问题：

* Pod 刚启动 → 没有热点代码 → 性能差
* 跑一段时间 → JIT优化 → 性能提升

👉 结论：

❗ **Java服务有“预热期”**

---

## 实战优化

👉 方案：

* 不要频繁重启 Pod
* 设置合理副本数（避免冷启动影响）

---

# 四、GC（重点：稳定性）

## 是什么？

👉 Garbage Collection（垃圾回收）

---

## 管理哪些内存？

* 堆（Heap）
* 新生代 / 老年代

---

## 常见 GC（你说这几个就够了）

* G1（默认，推荐）✅
* ZGC（低延迟，高级玩法）

---

## 面试一句话

👉 **GC负责自动回收无用对象，防止内存泄漏。**

---

# 五、GC 在 K8s 里的坑（非常重要🔥）

## ⚠️ 问题1：内存不够 → Pod被杀

K8s：

```yaml
resources:
  limits:
    memory: "512Mi"
```

👉 如果 JVM 不知道限制：

💥 可能直接 OOMKilled

---

## ⚠️ 问题2：GC 频繁

表现：

* CPU 飙高
* 服务卡顿

---

# 六、K8s + JVM 调优（核心实战）

## ✅ 必加 JVM 参数

```bash
-XX:+UseContainerSupport
-XX:MaxRAMPercentage=75.0
```

👉 作用：

* 感知容器内存
* 自动控制堆大小

---

## ✅ 推荐配置（实战用）

```bash
-XX:+UseG1GC
-XX:MaxRAMPercentage=75
-XX:InitialRAMPercentage=50
```

---

## 👉 举例（你面试可以说）

K8s 限制：

```yaml
memory: 512Mi
```

JVM：

👉 实际使用 ≈ 300~380Mi

---

# 七、典型问题（你必须会讲）

---

## ❓ 问题1：为什么 Java 容器容易 OOM？

👉 答：

* JVM默认按宿主机内存算
* K8s限制了容器内存
* 导致超限被杀

---

## ❓ 问题2：怎么解决？

👉 答：

* 开启容器感知参数
* 控制堆大小
* 设置资源 limits

---

## ❓ 问题3：为什么服务刚启动慢？

👉 答：

* JIT 未生效
* 热点代码还没编译

---

## ❓ 问题4：GC频繁怎么排查？

👉 答：

1. 看日志（GC log）
2. 看 CPU
3. 看内存使用
4. 调整堆大小 / GC策略

---

# 八、你项目里怎么用（KubeMall落地）

## Dockerfile（示例）

```dockerfile
ENTRYPOINT ["java",
"-XX:+UseG1GC",
"-XX:MaxRAMPercentage=75",
"-jar","app.jar"]
```

---

## Kubernetes（示例）

```yaml
resources:
  requests:
    memory: "256Mi"
  limits:
    memory: "512Mi"
```

---

# 九、总结（面试收尾）

👉 你可以这样收：

**在 K8s 环境中，我会结合 JVM 参数和资源限制来调优 Java 服务，确保既不会 OOM，也能保持较好的性能和稳定性。**

---

# 十、你当前阶段重点

你现在不用死磕 GC 算法细节 👇

👉 重点是：

✅ 知道 JIT 在干嘛
✅ 知道 GC 会影响性能
✅ 知道 K8s 下必须调 JVM

---

# （完）
