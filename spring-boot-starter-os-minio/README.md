# 本地调试
1. 打包该依赖到本地maven库
```
mvn clean install
```

2. 使用
```xml
<dependency>
    <groupId>com.ak47007</groupId>
    <artifactId>spring-boot-starter-os-minio</artifactId>
    <version>${version}</version>
</dependency>
```
# 注意事项

> Minio默认服务端口为9090,endpoint属性的值应该为服务端口的域名