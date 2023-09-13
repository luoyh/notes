## 访问日志统计





### 统计用户访问页面次数

> 统计原理是在打开每一个页面, 查看请求的接口; 
>
> 找到一个必须请求的接口, 然后统计此接口的调用次数
>

```shell

# 统计PV, 即用户访问页面次数
# 如: 全市访问网络预警页面次数
> cat access.log | grep "/page/net" | wc -l

# 所有的页面次数相加即得到总页面访问次数

```



### 统计每天用户数

> 统计原理按照`ip`分组去重即可

```shell
# 统计每天用户数
> cat access.log | awk '{print $1}' | sort | uniq | wc -l
```



### 统计访问最多页面数

> 按照请求接口分组取最多的, 然后根据其所在页面得到请求页面.
>
> 注意详情接口的请求是通过`id`访问的, 所以详情页面不要找详情接口作为参考

```shell
# top 10, 第一列是序号 第二列是次数, 第三列是占比, 第四列是具体的值
# 以下的都是一样的, 修改最后的 head -n 10获取具体的top N
# 去掉nl则不显示序号
> cat access.log | awk '{CMD[$7]++;count++;}END { for (a in CMD)print CMD[a] " " CMD[a]/count*100 "% " a;}'  | column -c3 -s " " -t | sort -nr | nl | head -n 10
```



### 统计访问次数最多区县或用户

> 根据`ip`分组, 得到请求数

```shell
# 注意: 如果区县的ip是前2个字节一样则修改下面的'{print $1"."$2"."$3}' 
# 改为 '{print $1"."$2}'
# 区县访问次数最多的
> cat access.log | awk '{print $1}' | awk -F '.' '{print $1"."$2"."$3}' | awk '{CMD[$0]++;count++;}END { for (a in CMD)print CMD[a] " " CMD[a]/count*100 "% " a;}'  | column -c3 -s " " -t | sort -nr | nl | head -n 10

# 访问最多的用户
> cat access.log | awk '{CMD[$1]++;count++;}END { for (a in CMD)print CMD[a] " " CMD[a]/count*100 "% " a;}'  | column -c3 -s " " -t | sort -nr | nl | head -n 10
```



### 时间段访问量

> 按时间分组



```shell
# 每天最简单, 直接wc -l即可
> cat access.log | wc -l

# 每小时访问量
> cat access.log | awk '{print $4}' | awk -F ':' '{CMD[$2]++;count++;}END { for (a in CMD)print CMD[a] " " CMD[a]/count*100 "% " a;}'  | column -c3 -s " " -t | sort -nr | nl | head -n 24

# 每分钟访问量
> cat access.log | awk '{print $4}' | awk -F ':' '{print $2":"$3}' | awk  '{CMD[$0]++;count++;}END { for (a in CMD)print CMD[a] " " CMD[a]/count*100 "% " a;}'  | column -c3 -s " " -t | sort -nr | nl | head -n 10

# 每秒钟访问量
> cat access.log | awk '{print $4}' | awk -F ':' '{print $2":"$3":"$4}' | awk  '{CMD[$0]++;count++;}END { for (a in CMD)print CMD[a] " " CMD[a]/count*100 "% " a;}'  | column -c3 -s " " -t | sort -nr | nl | head -n 10

```

