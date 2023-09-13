```shell

# in container the command or content show not full
> docker exec -ti -e LINES=$(tput lines) -e COLUMNS=$(tput cols) PID bash

> docker top container

# export image
> docker save -o image.1.0.tar image:1.0

# import image
> docker load -i image.1.0.tar

# set service scale
> docker service scale my-service=0

# update service
> docker service update --image image:1.0 --publish-add 8080:8080 --force my-service

```


### install docker manual offline

```bash

# see https://docs.docker.com/engine/install/binaries/#install-static-binaries

# 1. download tar.gz for https://download.docker.com/linux/static/stable/
tar xzvf /path/to/<FILE>.tar.gz
cp docker/* /usr/bin/
dockerd &

# 2. in China mirror
vim /etc/docker/daemon.json

{ 
  "registry-mirrors": [
    "http://hub-mirror.c.163.com",
    "https://docker.mirrors.ustc.edu.cn",
    "https://registry.docker-cn.com"
  ]
}

# 3. systemd: https://docs.docker.com/config/daemon/systemd/
# visit https://github.com/moby/moby/tree/master/contrib/init/systemd
# copy docker.service and docker.socket to /etc/systemd/system
chmod a+x /etc/systemd/system/docker.service
chmod a+x /etc/systemd/system/docker.socket
# copy https://github.com/containerd/containerd/containerd.service to /etc/systemd/system
# update ExecStart=/usr/local/bin/containerd to ExecStart=/usr/bin/containerd
chmod a+x /etc/systemd/system/containerd.service


# 4. reload daemon
systemctl daemon-reload

# 5. start docker
systemctl start docker

```