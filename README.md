# 第三方对象存储集成

# 支持的存储方式

1. 本地存储（默认）
2. Minio
3. 阿里云OSS

# 开始使用

**1.引入依赖**

```xml

<dependency>
    <groupId>com.ak47007</groupId>
    <artifactId>spring-boot-starter-os-core</artifactId>
    <version>${version}</version>
</dependency>
```

如果只引入core，默认只有本地存储，需要使用其他存储方式，请添加其他依赖包（core是必需的）:

`spring-boot-starter-os-${type}`

**2.配置文件**
默认配置文件为本地存储，如需使用其他方式，请查看**配置文件**
```yaml
object-storage:
  type: local
  local:
    storage-path: ~/Downloads/ObjectStorage
```

**3. 启用对象存储方案**
```java
@SpringBootApplication
// 加上该注解启用
@EnableObjectStorage
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
```

**4.实现数据库操作**

`com.ak47007.core.dao`包下有一个数据库操作接口，该依赖包下只有定义，没有实现。
demo里面使用的是Mybatis Plus实现数据库操作

# 配置文件
## 本地存储
```yaml
object-storage:
  type: local
  local:
    storage-path: ~/Downloads/ObjectStorage
```
## 阿里云OSS
```yaml
object-storage:
  type: oss
  oss:
    endpoint: oss.file.ak47007.com
    bucket: test
    accessKey: test
    secretKey: test
```
## Minio
```yaml
object-storage:
  type: minio
  minio:
    endpoint: minio.file.ak47007.com
    bucket: test
    accessKey: test
    secretKey: test
```