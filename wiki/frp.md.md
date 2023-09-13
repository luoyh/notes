
实例: 现有2个网段, `10.8.x.x` 和 `10.9.x.x`, 此时用户在`10.9`网段,
目前用户可通过`10.9.0.144`访问到`10.8.1.144`机器.
而`10.8`不能访问`10.9`的任何机器.
现需实现`10.8` 能够访问`10.9`.

现只演示`http`的访问, 其他的tcp类似.

1. 在`10.8.1.144`机器上配置`frps`
```
[common]
bind_port = 8080
vhost_http_port = 8081
```

启动服务: `./frps -c ./frps.ini`

2.在`10.9`的任意机器上如`10.9.0.31`上配置`frpc`
```
[common]
server_addr = 10.9.0.144
server_port = 8080

[web1]
type = http
local_ip = 10.9.0.31
local_port = 8030
custom_domains = web1.ofllibnnb.top

[web2]
type = http
local_ip = 10.9.0.31
local_port = 8031
custom_domains = web2.ofllibnnb.top
```

启动`frpc`: `./frpc -c frpc.ini`

现在就可以在`10.8.`网段通过`web1.ofllibnnb.top:8081` 访问到`10.9.0.31:8030`

`web2.ofllibnnb.top:8081 -> 10.9.0.31:8031`


3. 需在`10.8.x.x`配置`hosts`
```
10.8.1.144 web1.ofllibnnb.top web2.ofllibnnb.top
```

> 如果是通过`nginx`代理的话, 需设置`proxy_set_header Host web.ofllibnnb.top:8081`