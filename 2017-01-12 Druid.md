# druid 密码加解密
```
ConfigTools.decrypt();
ConfigTools.encrypt();
```
#### 可以设置自定义加密的公钥key
```
// if config.decrypt=false, the password not encrypt.
connectionProperties=config.decrypt=true;config.decrypt.key=MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBALDi8Jl8IRMNvL5WDfGuWbz4DLh6QxC6qV/JdYBDC03livx1GIpQJidDRcRjVsC8vtFuB/rLmIz7/6g4I/K+DRECAwEAAQ==
```

# druid 启动出现IdentityHashMap错误
```
Invocation of init method failed; nested exception is java.lang.ClassCastException: java.util.Collections$SynchronizedMap cannot be cast to java.util.IdentityHashMap

// 原因是项目里面有多个的druid版本. 删掉其中一个项目就可以了.
```

#druid 多个项目设置
```
$> cd /tomcat-path/bin/
$> vim setenv.sh
JAVA_OPTS="-Ddruid.registerToSysProperty=true"
```