## cheatsheet for nginx

```conf

# 根据ip段设置变量
geo $ipaddr {
   ranges;
   default 0;
   10.1.2.31-10.1.2.33 1;
   10.2.0.0-10.2.255.255 2;
}

# 根据请求地址设置变量
map $request_uri $uritype {
  default 'normar';
  ~*/-/api/admin/* 'admin';
  ~*/-/api/config/app 'app';
}

# limit_rate 限制响应速度, 默认bytes
# limit_rate 100; 
# limit_rate 10k;

```

# install

```
  ./configure --prefix=/home/data/web/nginx
  yum -y install pcre-devel
  ./configure --prefix=/home/data/web/nginx
  yum -y install zlib-devel
  ./configure --prefix=/home/data/web/nginx
  make 
  make install
```

# install and add module

```
# eg. echo-nginx-module
mkdir /data/nginx/modules
cd /data/nginx/modules
git clone https://github.com/openresty/echo-nginx-module.git

# 1. use --add-module
./configure --prefix=/data/nginx/nginx --add-module=/data/nginx/modules/echo-nginx-module 
make && make install

# 2. use --add-dynamic-module
./configure --prefix=/data/nginx/nginx --add-dynamic-module=/data/nginx/modules/echo-nginx-module 
make
# don't make install only make 
# copy objs/ngx_http_echo_module.so to you path like /data/nginx/modules/ngx_http_echo_module.so
cp objs/ngx_http_echo_module.so /data/nginx/modules/ngx_http_echo_module.so
# nginx.conf 
load_module /data/nginx/modules/ngx_http_echo_module.so;

# 3. use docker
# docker can use #2 compile *.so 
docker run -d --name nginx \
    -p 80:80 \
    -v /root/tmp/nginx/nginx.conf:/etc/nginx/nginx.conf \
    -v /root/tmp/nginx/conf.d:/etc/nginx/conf.d \
    -v /root/tmp/nginx/html:/html \
    -v /root/tmp/nginx/modules:/modules \
    --rm nginx

# if error contain compat
# recompile add --with-compat
./configure --prefix=/data/nginx/nginx --with-compat --add-dynamic-module=/data/nginx/modules/echo-nginx-module 

# note: docker version same as compile nginx version
```