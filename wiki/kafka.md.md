
```bash
./kafka-console-consumer.sh --bootstrap-server 172.26.2.174:9092 --consumer.config ../config/client.properties --group test001 --topic test-topic


./kafka-acls.sh --authorizer-properties zookeeper.connect=localhost:2181 --list

# --operation Read/Write
./kafka-acls.sh --authorizer-properties zookeeper.connect=localhost:2181 --add --allow-principal User:test-producer --producer --topic test-topic


./kafka-configs.sh --zookeeper localhost:2181 --alter --add-config 'SCRAM-SHA-512=[password=test123]' --entity-type users --entity-name test-producer --command-config ../config/client.properties


mvn compile exec:java -Dexec.mainClass="com.vvtf.sample.KafkaProducerTest"

```



# kafka 3.2.0 授权

## 下载`kafka`

- 官方下载地址 [kafka_2.13-3.2.0.tgz](https://www.apache.org/dyn/closer.cgi?path=/kafka/3.2.0/kafka_2.13-3.2.0.tgz)
- 国内腾讯镜像 [kafka_3.2.0](https://mirrors.cloud.tencent.com/apache/kafka/3.2.0/)
- 官方文档 [security](https://kafka.apache.org/documentation/#security_sasl)


------------


```bash

# 解压
tar zxvf kafka_2.13-3.2.0.tgz 

# 创建软连接
ln -s kafka_2.13-3.2.0 kafka

# 创建数据目录
cd kafka
mkdir -p data data/kafka-logs data/zoo-logs

# 修改zookeeper配置
# config/zookeeper.properties
dataDir=/data/kafka/data/zoo-log

# 启动zookeeper
cd bin
./zookeeper-server-start.sh -daemon ../config/zookeeper.properties

# 创建admin用户, 密码是admin123, 可使用SCRAM-SHA-256和SCRAM-SHA-512
# 多个使用逗号分割: 'SCRAM-SHA-512=[password=admin123],SCRAM-SHA-256=[password=admin123]'
./kafka-configs.sh --zookeeper localhost:2181 --alter --add-config 'SCRAM-SHA-512=[password=admin123]' --entity-type users --entity-name admin


```


## 设置`kafka`配置文件

```bash

# 修改kakfa配置
# config/server.properties
# 可使用自己的ip地址
# advertised.listeners也可以设置, 不设置就用listeners
listeners=SASL_PLAINTEXT://localhost:9092

# broker间的协议
security.inter.broker.protocol=SASL_PLAINTEXT

# 这里可选SCRAM-SHA-256/SCRAM-SHA-512/PLAIN
# PLAIN方式是通过配置文件固定的用户, 不能用过上面的命令动态创建用户
sasl.mechanism.inter.broker.protocol=SCRAM-SHA-512
sasl.enabled.mechanisms=SCRAM-SHA-512

# 授权方式, 必须, 不然就是没有设置acls的也能进行消费或者生产
authorizer.class.name=kafka.security.authorizer.AclAuthorizer

# 超级管理员用户可以多个使用逗号分割
super.users=User:admin

# 日志目录
log.dirs=/data/kafka/data/kafka-logs
```

## 设置`kafka`jaas文件

```bash

# 创建kafka_server_jaas.conf
# config/kafka_server_jaas.conf

KafkaServer {
    org.apache.kafka.common.security.scram.ScramLoginModule required
    username="admin"
    password="admin123"
    user_admin="admin123"
    ;
};


# 修改kafka启动命令, 增加jaas文件
# 在后面exec命令前一行加入
KAFKA_HEAP_OPTS="$KAFKA_HEAP_OPTS -Djava.security.auth.login.config=/data/kafka/config/kafka_server_jaas.conf"


# 启动kafka
cd bin
./kafka-server-start.sh -daemon ../config/server.properties

```



## 用户权限管理


```bash

# 创建用户, 用户名: test-producer, 密码: test123
./kafka-configs.sh --zookeeper localhost:2181 --alter --add-config 'SCRAM-SHA-512=[password=test123]' --entity-type users --entity-name test-producer


# 赋予权限, 给用户test-producer赋予test-topic的生产者权限
./kafka-acls.sh --authorizer-properties zookeeper.connect=localhost:2181 --add --allow-principal User:test-producer --producer --topic test-topic


# 给用户test-consumer赋予test-topic的消费者权限, 且分组必须是group-consumer-test-topic
./kafka-acls.sh --authorizer-properties zookeeper.connect=localhost:2181 --add --allow-principal User:test-consumer --consumer --topic test-topic --group group-consumer-test-topic

# 查看权限
./kafka-acls.sh --authorizer-properties zookeeper.connect=localhost:2181 --list



```


## 生产消费

```bash

# 命令行使用, kafka自带命令消费者

# 先创建client.properties文件
# 可以使用admin跳过权限验证
security.protocol=SASL_PLAINTEXT
sasl.mechanism=SCRAM-SHA-512
sasl.jaas.config=org.apache.kafka.common.security.scram.ScramLoginModule required \
    username="testconsumer" \
    password="testconsumer123";

# 消费数据
./kafka-console-consumer.sh --bootstrap-server 172.26.2.174:9092 --consumer.config ../config/client.properties --group test001 --topic test-topic



# java代码只需额外增加下面几个属性即可
properties.put("security.protocol", "SASL_PLAINTEXT");                                           
properties.put("sasl.mechanism", "SCRAM-SHA-512");
properties.put("sasl.jaas.config", "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"testconsumer\" password=\"testconsumer123\";");


```

