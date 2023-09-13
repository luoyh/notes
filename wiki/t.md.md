

## jenkins

```
## pipeline
pipeline {
   agent any
   
   stages {
      stage('common') {
          steps {
              dir('common') {
                git branch: '${components}', credentialsId: 'ffcdb8c0-86d5-44fe-85f1-d8d65b60fcb3', url: 'http://77.103.0.118:7777/TYAF/anti-fraud-components.git'
                sh "mvn clean install -Dmaven.test.skip=true -U"
              }
          }
      }
      stage('build') {
         steps {
            dir('antifraud') {
                git branch: '${appcq}', credentialsId: 'ffcdb8c0-86d5-44fe-85f1-d8d65b60fcb3', url: 'http://77.103.0.118:7777/TYAF/anti-fraud-appcq.git'
                script {
                    if (params.uwa) {
                        sh "mvn clean package -Dmaven.test.skip=true -U -P cq"
                    } else {
                        sh "mvn clean package -Dmaven.test.skip=true -U"
                    }
                    POM_ARTIFACTID=sh(script: 'mvn -q -N org.codehaus.mojo:exec-maven-plugin:3.0.0:exec -Dexec.executable=echo -Dexec.args=\'\${project.artifactId}\'',returnStdout:true).trim()
                    POM_VERSION=sh(script: 'mvn -q -N org.codehaus.mojo:exec-maven-plugin:3.0.0:exec -Dexec.executable=echo -Dexec.args=\'\${project.version}\'',returnStdout:true).trim()
                }
                echo "${POM_ARTIFACTID}-${POM_VERSION}.jar"
                
                sshPublisher(publishers: [
                    sshPublisherDesc(
                        configName: 'fz.138.data1', 
                        transfers: [
                            sshTransfer(
                                cleanRemote: false, 
                                excludes: '', 
                                execCommand: """
cd /data/apps/antifraud/
mv /data/apps/antifraud/upload/${POM_ARTIFACTID}-${POM_VERSION}.jar /data/apps/antifraud/upload/antifraud-appcq-${POM_VERSION}.jar
./start.sh antifraud-appcq ${POM_VERSION} restart


                                """, 
                                execTimeout: 120000, 
                                flatten: false, 
                                makeEmptyDirs: false, 
                                noDefaultExcludes: false, 
                                patternSeparator: '[, ]+', 
                                remoteDirectory: 'apps/antifraud/upload', 
                                remoteDirectorySDF: false, 
                                removePrefix: 'target/', 
                                sourceFiles: 'target/*.jar')], 
                                usePromotionTimestamp: false, 
                                useWorkspaceInPromotion: false, 
                                verbose: false)])
                                
            }
         }
      }
   }
}


--------

pipeline {
   agent any
   
   //parameters {
  //   gitParameter branch: '', branchFilter: '.*', defaultValue: 'dev', description: '', name: 'branchComponents', quickFilterEnabled: false, selectedValue: 'NONE', sortMode: 'NONE', tagFilter: '*', type: 'PT_BRANCH', useRepository: 'http://10.154.106.49:8180/TYAF/anti-fraud-components.git'
  // }

   stages {
      stage('common') {
          steps {
              dir('common') {
                git branch: '${componentsBranch}', credentialsId: 'ffcdb8c0-86d5-44fe-85f1-d8d65b60fcb3', url: 'http://77.103.0.118:7777/TYAF/anti-fraud-components.git'
                sh "mvn clean install -Dmaven.test.skip=true -U"
              }
          }
      }
      stage('build') {
         steps {
            dir('antifraud') {
                git branch: '${antifraudBranch}', credentialsId: 'ffcdb8c0-86d5-44fe-85f1-d8d65b60fcb3', url: 'http://77.103.0.118:7777/TYAF/anti-fraud.git'
                script {
                    sh "mvn clean package -Dmaven.test.skip=true -U -P cq"
                    POM_ARTIFACTID=sh(script: 'mvn -q -N org.codehaus.mojo:exec-maven-plugin:3.0.0:exec -Dexec.executable=echo -Dexec.args=\'\${project.artifactId}\'',returnStdout:true).trim()
                    POM_VERSION=sh(script: 'mvn -q -N org.codehaus.mojo:exec-maven-plugin:3.0.0:exec -Dexec.executable=echo -Dexec.args=\'\${project.version}\'',returnStdout:true).trim()
                }
                echo "${POM_ARTIFACTID}-${POM_VERSION}.jar"
                sshPublisher(publishers: [
                    sshPublisherDesc(
                        configName: 'fz.138.data1', 
                        transfers: [
                            sshTransfer(
                                cleanRemote: false, 
                                excludes: '', 
                                execCommand: """
cd /data/apps/antifraud/
mv /data/apps/antifraud/upload/${POM_ARTIFACTID}-startup-${POM_VERSION}.jar /data/apps/antifraud/upload/antifraud-startup-${POM_VERSION}.jar
./start.sh antifraud ${POM_VERSION} restart startup
                                """, 
                                execTimeout: 120000, 
                                flatten: false, 
                                makeEmptyDirs: false, 
                                noDefaultExcludes: false, 
                                patternSeparator: '[, ]+', 
                                remoteDirectory: 'apps/antifraud/upload', 
                                remoteDirectorySDF: false, 
                                removePrefix: 'startup/target/', 
                                sourceFiles: 'startup/target/*.jar')], 
                                usePromotionTimestamp: false, 
                                useWorkspaceInPromotion: false, 
                                verbose: false)])
            }
         }
      }
   }
}


## npm
find /data/nginx/antifraud -maxdepth 1 ! -name download ! -name antifraud -exec rm -rf {} \;
```



## mysql





```sql
#> rpm -ivh *.rpm
#> # 如果失败, 先删除mariadb
#> rpm -qa | grep mariadb
#> rpm -e --nodeps mariadb-libs-5.5.56-2.el7.x86_64
#> vi /etc/my.cnf

sql_mode=STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION
max_connections=500
lower_case_table_names=1
validate_password_policy=0

datadir=/xxx

#> chown -R mysql:mysql /xxx
#> vi /var/log/mysqld.log # get password


## in mysql
mysql> grant all on *.* to 'root'@'%';
mysql> flush privileges;
mysql> source apollo.sql
mysql> grant all on anti_fraud.* to 'skycomm'@'%';
mysql> grant all on apolloconfigdb.* to 'skycomm'@'%';
mysql> grant all on apolloportaldb.* to 'skycomm'@'%';
mysql> grant all on cpip2_user_log_db.* to 'skycomm'@'%';
mysql> grant all on skycomm_base_service.* to 'skycomm'@'%';
mysql> grant all on skycomm_inbox.* to 'skycomm'@'%';
mysql> grant all on skycomm_message.* to 'skycomm'@'%';

mysql> flush privileges;
```



```
SELECT
	a.id
FROM
	fraud a
JOIN (
	SELECT
		b.fraud_id
	FROM
		fraud_feedback b
	WHERE
		b.organ_code IN ('40','416','415','414','413','412','411','410','409','408','407','406','405','404','403','402','401','400','399','398','397','396','395','394','393','392','391','390','389','388','387','386','385','384','383','382','381','380','379','378','377','101','79','78')
) c ON a.id = c.fraud_id
WHERE
	a.`status` IN (3, 4)
AND a.data_type =1
and a.created>='2021-06-14 00:00:00'
and a.created<'2021-07-15 00:00:00'
GROUP BY
	a.id
ORDER BY
	a.id DESC
LIMIT 0,20;



SELECT
		b.fraud_id
	FROM
		fraud_feedback b
	WHERE
		b.organ_code IN ('40','416','415','414','413','412','411','410','409','408','407','406','405','404','403','402','401','400','399','398','397','396','395','394','393','392','391','390','389','388','387','386','385','384','383','382','381','380','379','378','377','101','79','78')
and b.fraud_id in (
SELECT
	a.id
FROM
	fraud a
WHERE
	a.`status` IN (3, 4)
AND a.data_type =1
and a.created>='2021-06-14 00:00:00'
and a.created<'2021-07-15 00:00:00'
)
GROUP BY
	b.fraud_id
ORDER BY
	b.fraud_id DESC
LIMIT 0,20
```



## redis

```shell
> cd redis-4.0.2
> make
> make MALLOC=libc
> make install
> nohup src/redis-server redis.conf > /dev/null 2>&1 &
```



## nginx

```shell
  131  ./configure --prefix=/home/data/web/nginx
  132  yum -y install pcre-devel
  133  ./configure --prefix=/home/data/web/nginx
  134  yum -y install zlib-devel
  135  ./configure --prefix=/home/data/web/nginx
  136  make 
  137  make install

```



## mysql repl

> `master: db1, *.*.*.11`
>
> `slave: db2, *.*.*.13`

```shell
# on master

# create replication user
>> create user 'repl'@'db2' identified by 'repl@123';
>> grant replication slave on *.* to 'repl'@'db2';

log-bin=mysql-bin
server-id=11


# gtid_mode=ON
# enforce-gtid-consistency=ON

binlog-do-db=anti_fraud_test
binlog-ignore-db=information_schema
binlog-ignore-db=mysql
binlog-ignore-db=sys
binlog-ignore-db=skycomm_message
binlog-ignore-db=skycomm_inbox
binlog-ignore-db=cpip2_user_log_db
binlog-ignore-db=label_test
binlog-ignore-db=mid_fz_da
binlog-ignore-db=performance_schema

replicate-do-db=anti_fraud_test
replicate-ignore-db=information_schema
replicate-ignore-db=mysql
replicate-ignore-db=sys
replicate-ignore-db=skycomm_message
replicate-ignore-db=skycomm_inbox
replicate-ignore-db=cpip2_user_log_db
replicate-ignore-db=label_test
replicate-ignore-db=mid_fz_da
replicate-ignore-db=performance_schema

```

```shell
# on slave
skip-slave-start
read-only
server-id=13

# gtid_mode=ON
# enforce-gtid-consistency=ON

binlog-do-db=anti_fraud_test
binlog-ignore-db=information_schema
binlog-ignore-db=mysql
binlog-ignore-db=sys
binlog-ignore-db=skycomm_message
binlog-ignore-db=skycomm_inbox
binlog-ignore-db=cpip2_user_log_db
binlog-ignore-db=label_test
binlog-ignore-db=mid_fz_da
binlog-ignore-db=performance_schema

replicate-do-db=anti_fraud_test
replicate-ignore-db=information_schema
replicate-ignore-db=mysql
replicate-ignore-db=sys
replicate-ignore-db=skycomm_message
replicate-ignore-db=skycomm_inbox
replicate-ignore-db=cpip2_user_log_db
replicate-ignore-db=label_test
replicate-ignore-db=mid_fz_da
replicate-ignore-db=performance_schema

```



```shell
# restart master,slave
# on master
# master-data=2, create change master sql, but it's comment.
> mysqldump --databases anti_fraud_test --master-data=2 --single-transaction -uroot -p > slave1.db

# on slave
> mysql -p < slave1.db

>> change master to 
->> master_host='db1',
->> master_user='repl',
->> master_password='repl@123',
->> master_log_file='`log_file_name`',
->> master_log_pos=`1234`;

>> start slave;

# Slave_IO_Running: Yes; Slave_SQL_Running: Yes
>> show slave status\G


```



### use gtid

```sql
# on db1,db2
# open cnf

[mysqld]
gtid_mode=ON
enforce-gtid-consistency=ON

# 下面操作每个执行后需观察mysql的日子是否有异常

>> set @@global.enforce_gtid_consistency=WARN;
>> set @@global.enforce_gtid_consistency=ON;
>> set @@global.gtid_mode=OFF_PERMISSIVE;
>> set @@global.gtid_mode=ON_PERMISSIVE;
# 查询结果为0, 多次验证
>> show status like 'ongoing_anonymous_transaction_count';
>> set @@global.gtid_mode=ON;
>> show variables like '%gtid%';

# on slave db2
>> stop slave;
>> change master to master_auto_position=1;
>> start slave;

```



### gtid skip 

```sql
stop slave;set @@session.gtid_next='0b7cdf27-a3db-11ea-ab76-d00d91974427:2318945-4191618';begin;commit;set session gtid_next=automatic;start slave;show slave status\G
```



## redis scan

```shell
> scan 0 match 1* count 100
2752
1d0
1B6
1DC
1vp
17A
143
11d
> scan 2752 match 1* count 100
2912
1B7
14E
1E8
1E5
> scan 2912 match 1* count 100
...
```





## arthas

```shell

# 获取类加载器
classloader -l

# 获取spring 代理的对象
ognl -c 31cefde0 '#ctx=@com.skycomm.antifraud.components.common.helper.Springs@ctx,#ctx.getBean("AutoCallControlBus")'

# 获取静态变量, -x 2展开2次
getstatic com.skycomm.antifraud.application.timer.AutoCallControlBus consumers -x 2


# 获取枚举对象的变量
getstatic com.skycomm.antifraud.application.timer.CallDataQueue E queueMap -x 2

#  执行方法, 调用静态方法
ognl '@java.lang.System@currentTimeMillis()'

# 观察方法调用, -b调用前
watch com.skycomm.antifraud.application.timer.CallDataConsumer tudo '{params,returnObj}' -b

# 执行spring方法
ognl -c 31cefde0 '#ctx=@com.skycomm.antifraud.components.common.helper.Springs@ctx,#i=#ctx.getBean("personResidentService"),#i.resident("510223197204258140", true)'

# 执行静态方法
ognl '#c=@com.skycomm.antifraud.datax.application.timer.FilledTimer@class,#f=#c.getDeclaredField("latest"),#f.setAccessible(true),#f.set(#c,123)'

# 方法调用监控
tt -t com.skycomm.antifraud.track.manager.JgAnalysisManager taskStatus

# 没有spring context时
tt -t org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter invokeHandlerMethod
tt -i 1000 -w 'target.getApplicationContext().getBean("jgAnalysisManager")'
tt -i 1000 -w 'target.getApplicationContext().getBean("jgAnalysisManager").esPool'
tt -i 1000 -w 'target.getApplicationContext().getBean("appCaseWashTask").fraudBlackListAllToRedis()'

```

 





## tcpdump

```shell
tcpdump -i eno1 port 9900 -n -A
```





```shell
netstat -antp | grep pid | wc -l
pstree -ap pid
top -Hp pid
```

## docker

```shell
# docker终端显示不全
docker exec -ti -e LINES=$(tput lines) -e COLUMNS=$(tput cols) aba3d3135509 bash

docker exec -ti mysql mysql -p --default-character-set=utf8mb4

docker top container

# export image
docker save -o datax-2.0.0-23.tar anti-fraud/datax:2.0.0.23

# import image
docker load -i datax-2.0.0-23.tar

# set scale
docker service scale untifraud_datax=0


```

## kafka

```
./kafka-console-consumer.sh --bootstrap-server 10.154.106.59:9092 --topic test

./kafka-server-start.sh -daemon ../config/server.properties
./zkServer.sh start

./kafka-server-stop.sh
./zkServer.sh stop
```

## processor on console

```java

    static void process() throws Exception {
        new Thread(() -> {
            char[] ps = {'|', '/', '一', '\\'};
            int i = 0;
            int len = (max + "").length();
            for (;;) {
                StringBuilder sb = new StringBuilder();
                sb.append(StringUtils.leftPad((int)(idx*1.0/max*100)+"", 3));
                sb.append("%:");
                sb.append(StringUtils.leftPad(idx + "", len));
                sb.append("/");
                sb.append(max);
                sb.append(ps[i]);
                System.out.print(sb);
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                }
                i ++;
                if (i >= ps.length) {
                    i = 0;
                }
                for (int j = 0; j < sb.length(); j ++) {
                    System.out.print('\b');
                }
                
                if (idx >= max) {
                    System.out.print("100%:");
                    System.out.print(max);
                    System.out.print("/");
                    System.out.print(max);
                    break;
                }
            }
        }).start();
    }
```

## clickhouse

```sql
clickhouse-client --password skycomm@123 --port 9001
CREATE DATABASE if not exists fz ENGINE = Ordinary;

-- 本地表
create table if not exists fz.test_shard001 on cluster cluster_skycomm (
    id UInt64,
    name String
) engine = MergeTree() 
partition by id 
order by id 
SETTINGS index_granularity = 8192;


-- 分布式表
create table if not exists fz.test_shard001_all on cluster cluster_skycomm (
    id UInt64,
    name String
) engine = Distributed(cluster_skycomm,fz,'test_shard001', rand());

select * from system.clusters;



select * from system.processes;

kill query where query_id='';


```

## npm nexus

```shell

$> find . -name package.json > x.sh

$> vim x.sh
:g/package.json/s///g
:%s/^/npm publish --registry=http:\/\/10.154.106.50:8081\/repository\/npm-test-hosted\/ /
:wq

$> split -l 300 x.sh xx


$> find . -name package.json | xargs grep '"registry": "http"'


$> for i in $(ls xx*); do echo "nohup sh $i >> xx.log 2>&1 &"; done


npm publish --registry=http://10.154.106.50:8081/repository/npm-test-hosted/ xx_node_component

```

## ulimit

```shell
ulimit -SHn 65535
vim /etc/security/limits.conf
* soft nofile 65535
* hard nofile 65535
* soft nproc 65535
* hard nproc 65535

vim /etc/security/limits.d/20-nproc.conf
```

## account password

`select sha1('123123r')`

## clean cache for linux

```
sync; echo 3 > /proc/sys/vm/drop_caches
```

## linux for in command

```shell
for i in $(ls); do echo $i: `find $i -type f | wc -l`; done

```



## linux get threads for pid

```shell
# all
> ps -eLf|wc -l
> top -Hp PID
> ls /proc/PID/tasks/ | wc -l
> cat /proc/PID/status | grep Thread
```







## find content

```
find . -name *.py -type f | xargs grep -i "y.\`code\`" 
```

## curl

```
curl http://77.1.24.156  -o /dev/null -s -w ' status: \t\t%{http_code}\n connect time: \t\t%{time_connect}s\n time_namelookup: \t%{time_namelookup}s\n time_pretransfer: \t%{time_pretransfer}s\n time_starttransfer: \t%{time_starttransfer}s\n time_redirect: \t%{time_redirect}s\n total time: \t\t%{time_total}s\n\n' -m 5
```

## mysql page_cleaner

```
# 考虑减少
innodb_lru_scan_depth=256
# 考虑减少, 这个是动态的, 最大不会超过innodb_io_capacity_max
innodb_io_capacity=200
# 考虑减少
innodb_io_capacity_max=2000
# 考虑减少
innodb_max_dirty_pages_pct=75
innodb_max_dirty_pages_pct_lwm=0
# 考虑增加, 最好与innodb_buffer_pool_instances一致
innodb_page_cleaners=1
# 考虑增加, 不超过CPU数.
innodb_buffer_pool_instances=1
```

## rpm

```
rpm -ivh xxx.rpm --nodeps
```



## iostat

```
iostat -d -m -x 1 3
```



## actuator

```
curl -X POST http://77.1.24.14:6020/actuator/loggers/org.apache.ibatis -H 'Content-Type: application/json' -d '{"configuredLevel":"info"}'
```



## jar

```
# 解压文件
> jar xvf xxx.jar

# 打包 
> jar cvfM0 xxx.jar .

# 替换文件
> jar uf xxx.jar BOOT-INF/lib/xx1.jar

```



## ab

```
ab -c 2 -n 10000 -p payload -T 'application/json' -H 'Content-Type: application/json;charset=UTF-8'  -H 'authentication: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhY2NvdW50SWQiOjQyNDIyODU1MTU4ODM4NjI3MSwicHJvZHVjdF9jb2RlIjoiYWYiLCJhY2NvdW50X3R5cGUiOiJwYyIsImlzcyI6IkNQSVAtU1NPIiwiYXVkIjoiMDk4ZjZiY2Q0NjIxZDM3M2NhZGU0ZTgzMjYyN2I0ZjYiLCJleHAiOjE2NDI2OTU2NDYsIm5iZiI6MTY0MjY1OTY0Nn0.PUsQPELgts5uZix1ByHpNQOUoMFSsU6CHJRzrmLCj3E' 'http://10.154.255.138:9999/antifraud/fraud/corner/marker/list'
```





## netstat

```
netstat -n | awk '/^tcp/ {++S[$NF]} END {for(a in S) print a, S[a]}'
```



## config-start.sh

```shell
#!/bin/bash
APP=""
APP_NAME=""
VER=""
file='/data/apps/antifraud/'
backupFile="${file}backup/"
uploadFile="${file}upload/"
LOG_FILE='./logs/start.log'
cmd=""
port=""
ext=""

while [ $# -ge 1 ]; do
  case "$1" in
      --port=*) port=`echo "$1" | sed -e 's/[-_a-zA-Z0-9]*=//'`; shift 1 ;;
      --app=*) APP=`echo "$1" | sed -e 's/[-_a-zA-Z0-9]*=//'`; shift 1 ;;
      --ver=*) VER=`echo "$1" | sed -e 's/[-_a-zA-Z0-9]*=//'`; shift 1 ;;
      --cmd=*) cmd=`echo "$1" | sed -e 's/[-_a-zA-Z0-9]*=//'`; shift 1 ;;
      --ext=*) ext=`echo "$1" | sed -e 's/[-_a-zA-Z0-9]*=//'`; shift 1 ;;
	    *) shift 1 ;;
  esac
done
APP_NAME=$APP
if [ -n "$ext" ]; then
  APP_NAME=${APP}-${ext}
fi

if [ -z "$APP" ]; then
  echo "缺少必要参数: 请填写app参数, 如: --app=antifraud-datax"
  exit 1
fi

if [ -z "$VER" ]; then
  echo "缺少必要参数: 请填写ver参数, 如: --ver=0.13"
  exit 1
fi

if [ -z "$cmd" ]; then
  echo "缺少必要参数: 请填写cmd参数, 如: --cmd=restart, start:启动,restart:重启,stop:停止"
  exit 1
fi

args="-Xms512m -Xmx2048m -XX:PermSize=512M -XX:MaxPermSize=2048M -Denv=pro -Dapollo.meta=http://127.0.0.1:8761 "

if [ -n "$port" ]; then
  args="-Xms512m -Xmx2048m -XX:PermSize=512M -XX:MaxPermSize=2048M -Denv=pro -Dapollo.meta=http://127.0.0.1:8761 -Dserver.port=$port"
fi

echo "pid=ps -ef|grep java|grep ${APP_NAME}-|awk { print #2}"

pid=`ps -ef|grep java|grep ${APP_NAME}-|awk '{print $2}'`
new=$uploadFile$APP_NAME-$VER.jar
export JAVA_HOME=/data/libs/jdk
export PATH=.:$PATH:$JAVA_HOME/bin

count=0
max_count=80

check(){
  sleep 1
  echo "nnpid=ps -ef|grep java|grep ${APP_NAME}-|awk '{print #2}'"
  npid=`ps -ef|grep java|grep ${APP_NAME}-|awk '{print $2}'`
  if [ -z "$npid" ]; then
    echo -e "\033[31m PID not found for $APP_NAME \033[0m"
    exit 1
  fi
  
  echo -e "\033[32m get new PID=$npid \033[0m"

  # until [[ (( $count -ge $max_count )) || "$(curl -X GET --silent --connect-timeout 1 --max-time 2 --head http://localhost:$PORT/ | head -n 1)" != ""  ]];
  # until [[ (( $count -ge $max_count )) || "$(netstat -tlp | grep :$PORT | wc -l)" -gt 0 ]];
  until [[ (( $count -ge $max_count )) || $(netstat -tlp | grep " ${npid}/java" | wc -l) -gt 0 ]];
  do
    tail -n 10 ${file}logs/$APP/logback.log
    # printf "."
    count=$[$count+1]
    sleep 3
  done
}

startup(){
  backup
  sleep 5
  echo "starting........"
  echo "nohup java $args -jar $APP_NAME-$VER.jar >/dev/null 2>&1 &"
  nohup java $args -jar $APP_NAME-$VER.jar >/dev/null 2>&1 & 
  check
  if [[ $count -ge $max_count ]]; then
    echo -e "\033[31m start $APP_NAME failed! \033[0m"
  else
    echo -e "\033[32m start $APP_NAME success! \033[0m"
  fi
  tail -n 30 ${file}logs/$APP_NAME/logback.log
  echo "start end........"
}

backup(){
  echo '备份文件开始'
  if [ `ls $file | grep "${APP_NAME}" | wc -l` -gt 0 ]; then
   # cp $file${APP_NAME} $backupFile
   mv $file${APP_NAME}*.jar $backupFile
   echo "备份成功!"
  else
   echo "$file${APP_NAME}文件不存在!"
  fi
 
  if [ -f $new ]; then
   #cp $uploadFile${APP_NAME} $file
   mv $new $file
   echo "更新成功!"
  else
   echo -e "\033[31m Error: ${new}文件不存在! \033[0m"
   exit 1
  fi
   echo '备份文件结束'
}


shutdown(){
  if [ -n "$pid" ]; then
    kill $pid
    echo "$pid will be killed after 10 seconds!"
    sleep 10
    
    xpid=`ps -ef|grep java|grep ${APP_NAME}-|awk '{print $2}'` 
    if [ -n "$xpid" ]; then
      echo "$APP_NAME still lived, force kill"
      kill -9 $xpid
      sleep 1
    fi
  fi
  echo "$APP_NAME: $pid is stopped"
}

if [ ! $cmd ]; then
  echo -e "\033[31m Please specify args 'start|restart|stop' \033[0m"
  exit
fi

if [ $cmd == 'start' ]; then
  if [ ! $pid ]; then
    startup
  else
    echo "$app is running! pid=$pid"
  fi
fi

if [ $cmd == 'restart' ]; then
  shutdown
  startup
fi

if [ $cmd == 'stop' ]; then
  shutdown
fi

```



## clickhouse-sinker

```json
{
  "clickhouse": {
      "db": "fz",
      "hosts": [["10.154.106.104"]],
      "dnsLoop" : false,
      "maxLifeTime": 300,
      "password": "skycomm@123",
      "port": 9001,
      "username": "default"
  },
  "kafka": {
     "brokers": "10.154.106.59:9092",
     "version": "2.0.1"
  },
  "common": {
    "bufferSize": 5000,
    "flushInterval": 5,
    "logLevel": "debug"
  },
  "task": {
    "name": "af_http_log",
    "topic": "af-http-log",
    "earliest": true,
    "consumerGroup": "clickhouseSinker",
    "parser": "gjson",
    "tableName": "af_http_log",
    "dims": [
      {
        "name": "app",
        "type": "String",
        "sourceName": "fields.app"
      },
      {
        "name": "body",
        "type": "String",
        "sourceName": "msg.body"
      },
      {
        "name": "time",
        "type": "DateTime",
        "sourceName": "msg.time"
      },
      {
        "name": "rid",
        "type": "String",
        "sourceName": "msg.rid"
      },
      {
        "name": "elapsed",
        "type": "Int64",
        "sourceName": "msg.elapsed"
      },
      {
        "name": "dsc",
        "type": "String",
        "sourceName": "msg.dsc"
      },
      {
        "name": "tid",
        "type": "String",
        "sourceName": "msg.tid"
      },
      {
        "name": "level",
        "type": "String",
        "sourceName": "msg.level"
      },
      {
        "name": "et",
        "type": "Int64",
        "sourceName": "msg.et"
      },
      {
        "name": "st",
        "type": "Int64",
        "sourceName": "msg.st"
      },
      {
        "name": "code",
        "type": "String",
        "sourceName": "msg.code"
      },
      {
        "name": "pid",
        "type": "String",
        "sourceName": "msg.pid"
      },
      {
        "name": "thread",
        "type": "String",
        "sourceName": "msg.thread"
      },
      {
        "name": "url",
        "type": "String",
        "sourceName": "msg.url"
      },
      {
        "name": "method",
        "type": "String",
        "sourceName": "msg.method"
      },
      {
        "name": "res",
        "type": "String",
        "sourceName": "msg.res"
      },
      {
        "name": "status",
        "type": "String",
        "sourceName": "msg.status"
      },
      {
        "name": "host",
        "type": "String",
        "sourceName": "fields.host"
      }
    ]
  }
}

## nohup ./clickhouse-sinker --local-cfg-file af_http_log.json > sinker.out 2>&1 &
```



## filebeat

```yaml
filebeat.inputs:

# Each - is an input. Most options can be set at the input level, so
# you can use different inputs for various configurations.
# Below are the input specific configurations.

# filestream is an input for collecting log messages from files.
- type: filestream

  # Change to true to enable this input configuration.
  enabled: true

  # Paths that should be crawled and fetched. Glob based paths.
  paths:
    - /data/apps/antifraud/logs/antifraud-datax/http.log
    #- c:\programdata\elasticsearch\logs\*
  fields:
    app: "datax"
    host: "151.50.3.138"
- type: filestream
  enabled: true
  paths:
    - /data/apps/antifraud/logs/antifraud/http.log
  fields:
    app: "antifraud"
    host: "151.50.3.138"

output:
  #console:
  #  pretty: true
  kafka:
    hosts: ["10.154.106.59:9092"]
    topic: 'af-http-log'


processors:
  - dissect:
      tokenizer: '[%{msg.time}] %{msg.pid}:[%{msg.thread}] -%{msg.tid} -%{msg.rid} %{msg.level} [%{msg.method}] : dsc:%{msg.dsc},url:%{msg.url},method:%{msg.method},body:%{msg.body},res:%{msg.res},code:%{msg.code},st:%{msg.st|long},et:%{msg.et|long},status:%{msg.status},elapsed:%{msg.elapsed|long}'
      field: "message"
      target_prefix: ""


## nohup ./filebeat -e -c datax.yml > filebeat.out 2>&1 &
```





end