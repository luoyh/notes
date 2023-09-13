
## mysqldump

> mysqldump --database test_db0 test_db1 \
> --single-transaction \
> --skip-lock-tables \
> --set-gtid-purged=OFF \
> -hlocalhose \
> -ureadonly \
> -p | gzip > /data/mysqlbackup/_db`date +%F`.db.gz


```
# https://wiki.shileizcc.com/confluence/display/MYS/MySQL+8.0.13+binary+install

wget https://dev.mysql.com/get/Downloads/MySQL-8.0/mysql-8.0.13-linux-glibc2.12-x86_64.tar.xz -P /opt

$ apt install -y libaio

$ groupadd mysql
$ useradd -r -g mysql -s /bin/false mysql


$ xz -d /opt/mysql-8.0.13-linux-glibc2.12-x86_64.tar.xz
$ cd /usr/local
$ tar xf /opt/mysql-8.0.13-linux-glibc2.12-x86_64.tar

$ ln -sf mysql-8.0.13-linux-glibc2.12-x86_64 mysql


$ cd mysql
$ mkdir mysql-files
$ chmod 750 mysql-files
$ chown -R mysql .
$ chgrp -R mysql .


$ bin/mysqld --initialize --user=mysql

$ bin/mysql_ssl_rsa_setup


$ cp support-files/mysql.server /etc/init.d/mysql

$ export PATH=$PATH:/usr/local/mysql/bin

$ mkdir /etc/mysql

vim /etc/mysql/my.cnf

!includedir /etc/mysql/conf.d/
!includedir /etc/mysql/mysql.conf.d/

vim /etc/mysql/mysql.conf.d/mysql.cnf

[mysqld]
user            = mysql
pid-file        = /usr/local/mysql/data/mysql.pid
socket          = /var/run/mysqld/mysqld.sock
datadir         = /usr/local/mysql/data
basedir         = /usr/local/mysql
log-error       = logerror.log
plugin-dir      = /usr/local/mysql/lib/plugin
bind-address    = 0.0.0.0


$ mkdir /var/run/mysqld
$ chown mysql.mysql /var/run/mysqld



$ systemctl daemon-reload
$ systemctl start mysql

ALTER USER 'root'@'localhost' IDENTIFIED BY 'shileizcc.com';

UPDATE mysql.user SET  Host = '%' WHERE User = 'root' AND Host = 'localhost';

```

## mysql master slave

> move slave to new host or add new slave from slave

```shell
# 1, in slave, stop slave
> stop slave
# 2, in slave, copy all datadir to new host
> tar cvf mysqldata.tar mysqldata
> scp mysqldata.tar new_slave_host:~
> scp /etc/my.cnf new_slave_host:~
# 3, in new slave, install mysql
# and replace my.cnf dont forget update server-id
# then delete auto.cnf (the server_uuid)
> # install mysql ignore
> cp ~/my.cnf /etc/my.cnf
# edit my.cnf set datadir to mysqldata
> rm -rf /data/mysqldata/auto.cnf
# on master grant 
> grant replicateion slave on *.* to `repl`@'new_slave_host';
# start mysql
> reset slave;
> start slave;

```


```shell
   61  mv ~/mysql-8.0.24-linux-glibc2.12-x86_64.tar.xz .
   62  groupadd mysql
   63  useradd -r -g mysql -s /bin/false mysql
   64  ll /bin/false
   65  cat /bin/false
   66  
   67  ll
   68  xz -d mysql-8.0.24-linux-glibc2.12-x86_64.tar.xz 
   69  ll
   70  tar xvf mysql-8.0.24-linux-glibc2.12-x86_64.tar 
   71  ll
   72  cd /usr/local/
   73  ll
   74  mv /opt/mysql-8.0.24-linux-glibc2.12-x86_64 .
   75  ll
   76  ln -sf mysql-8.0.24-linux-glibc2.12-x86_64 mysql
   77  ll
   78  cd mysql
   79  mkdir mysql-files
   80  ll
   81  chmod 750 mysql-files
   82  ll
   83  cd ..
   84  chown -R mysql mysql
   85  ll
   86  chgrp -R mysql mysql
   87  ll
   88  cd mysql
   89  ll
   90  cd ..
   91  ll
   92  chown -R mysql:mysql mysql/*
   93  cd mysql
   94  ll
   95  cd bin/
   96  ll
   97  cd ..
   98  ll
   99  df -h
  100  free -h
  101  ll
  102  bin/mysqld --initialize --user=mysql
  103  bin/mysql_ssl_rsa_setup 
  104  cp support-files/mysql.server /etc/init.d/mysql
  105  export PATH=$PATH:/usr/local/mysql/bin
  106  vi /etc/profile
  107  source /etc/profile
  108  mkdir /etc/mysql
  109  vim /etc/mysql/my.cnf
  110  vim /etc/mysql/mysql.conf.d/mysql.cnf
  111  mkdir /etc/mysql/mysql.conf.d
  112  vim /etc/mysql/mysql.conf.d/mysql.cnf
  113  mkdir /var/run/mysqld
  114  chown mysql.mysql /var/run/mysqld\
  115  ll /var/run/mysqld
  116  chown mysql.mysql /var/run/mysqld
  117  ll /var/run/
  118  systemctl cat mysql
  119  systemctl daemon-reload
  120  systemctl cat mysql
  121  systemctl start mysql
  122  ll
  123  cd mysql-files/
  124  ll
  125  cd ..
  126  ll
  127  cd data/
  128  ll
  129  systemctl status mysql.service
  130  cat /usr/local/mysql/data/iZ2vc367f0pwzpdlsky83aZ.err
  131  journalctl -xe
  132  ll /etc/mysql/mysql.conf.d/
  133  vim /etc/mysql/my.cnf 
  134  systemctl start mysql
  135  journalctl -xe
  136  ll /usr/local/mysql/data/
  137  systemctl cat mysql
  138  ll /etc/rc.d/init.d/mysql 
  139  free -h
  140  ll
  141  cd ..
  142  ll
  143  cd data
  144  ll
  145  cd ..
  146  ll
  147  cd ..
  148  ll
  149  cd mysql
  150  ll
  151   systemctl start mysql
  152  ps aux | grep mysql
  153  ll /var/run/mysqld/
  154  ll
  155  ll mysql-files/
  156  ll
  157  ll /etc/init.d/mysql 
  158  ll /etc/profile
  159  vi /etc/profile
  160  ll
  161  vim x
  162   systemctl start mysql
  163  journalctl -xe
  164  vim /etc/mysql/my.cnf 
  165   systemctl start mysql
  166  ll
  167  ll data/
  168  vim data/logerror.log 
  169  ll /var/run/mysqld/
  170  mysql -p
  171  cat x
  172  mysql -p
  173  mysql -uroot -p
  174  vi /etc/mysql/mysql.conf.d/mysql.cnf 
  175  ll data/
  176  cat x
  177  ps aux | grep mysql
  178  mysql -uroot -p
  179  systemctl stop mysql
  180  ps aux | grep mysql
  181  vim /etc/mysql/mysql.conf.d/mysql.cnf 
  182  systemctl start mysql
  183  mysql -p
  184  free -h
  185  ps aux | grep mysql
  186  systemctl stop mysql
  187  ps aux | grep mysql


```



**/etc/mysql/my.cnf**

```
!includedir /etc/mysql/mysql.conf.d/

```


**/etc/mysql/mysql.conf.d/mysql.cnf**
```
[mysqld]
user            = mysql
pid-file        = /usr/local/mysql/data/mysql.pid
socket          = /var/run/mysqld/mysqld.sock
datadir         = /usr/local/mysql/data
basedir         = /usr/local/mysql
log-error       = logerror.log
plugin-dir      = /usr/local/mysql/lib/plugin
bind-address    = 0.0.0.0

[client]
port=3306
socket          = /var/run/mysqld/mysqld.sock


```




```
[root@iZ2vc367f0pwzpdlsky83aZ mysql]# systemctl cat mysql.service 
# /run/systemd/generator.late/mysql.service
# Automatically generated by systemd-sysv-generator

[Unit]
Documentation=man:systemd-sysv-generator(8)
SourcePath=/etc/rc.d/init.d/mysql
Description=LSB: start and stop MySQL
After=network-online.target
After=remote-fs.target
After=ypbind.service
After=nscd.service
After=ldap.service
After=ntpd.service
After=xntpd.service
Wants=network-online.target

[Service]
Type=forking
Restart=no
TimeoutSec=5min
IgnoreSIGPIPE=no
KillMode=process
GuessMainPID=no
RemainAfterExit=yes
SuccessExitStatus=5 6
ExecStart=/etc/rc.d/init.d/mysql start
ExecStop=/etc/rc.d/init.d/mysql stop
ExecReload=/etc/rc.d/init.d/mysql reload
```