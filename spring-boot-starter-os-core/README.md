# 本地调试
1. 打包该依赖到本地maven库
```
mvn clean install
```

2. 使用
```xml
<dependency>
    <groupId>com.ak47007</groupId>
    <artifactId>spring-boot-starter-os-core</artifactId>
    <version>${version}</version>
</dependency>
```