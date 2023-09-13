
> https://kubernetes.io/zh-cn/docs/setup/production-environment/tools/kubeadm/install-kubeadm/

```bash
kubeadm init \
  --apiserver-advertise-address=10.0.3.3 \
  --image-repository registry.aliyuncs.com/google_containers \
  --service-cidr=10.96.0.0/12 \
  --pod-network-cidr=10.244.0.0/16 \
  --cri-socket=unix:///var/run/cri-dockerd.sock \
  --ignore-preflight-errors=all


kubeadm join 10.0.3.3:6443 \
--token krfbeq.zawqhno3px6jcffn \
--discovery-token-ca-cert-hash \
sha256:1a6294a2582393a8550017dcbd6e6c3657e6810946b9bb0259b8c321c5cf204a \
--apiserver-advertise-address=10.0.3.4 \
--cri-socket=unix:///var/run/cri-dockerd.sock

 kubeadm token create --print-join-command

########### K8S ######################
1. cat /etc/docker/daemon.json
2. install -o root -g root -m 0755 cri-dockerd /usr/local/bin/cri-dockerd
3. install cri-docker.service /etc/systemd/system
4. install cri-docker.socket /etc/systemd/system
5. cp -rf cni /opt/
6. install crictl /usr/local/bin/
7. install kubelet.service /etc/systemd/system
8. mkdir -p /etc/systemd/system/kubelet.service.d
9. cp 10-kubeadm.conf /etc/systemd/system/kubelet.service.d/
10. install kubeadm /usr/local/bin/
11. install kubectl /usr/local/bin/
12. install kubelet /usr/local/bin/
13. systemctl daemon-reload
14. systemctl restart docker
15. systemctl start cri-docker
# load images
16. docker load -i xxx

## kubeadm init

## yum -y install socat conntrack-tools
```


```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: nginx-test

---

apiVersion: v1
data:
  nginx.conf: |
    user  nginx;
    worker_processes  auto;

    error_log  /var/log/nginx/error.log notice;
    pid        /var/run/nginx.pid;

    events {
        worker_connections  1024;
    }

    http {
        include       /etc/nginx/mime.types;
        default_type  application/octet-stream;

        log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                          '$status $body_bytes_sent "$http_referer" '
                          '"$http_user_agent" "$http_x_forwarded_for"';

        access_log  /var/log/nginx/access.log  main;

        sendfile        on;
        #tcp_nopush     on;

        keepalive_timeout  65;

        #gzip  on;

        include /etc/nginx/conf.d/*.conf;
    }
kind: ConfigMap
metadata:
  name: nginx-conf
  namespace: nginx-test

--- 

apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-test-deployment
  namespace: nginx-test
spec:
  selector:
    matchLabels:
      app: nginx-test-pod
  replicas: 1
  template:
    metadata:
      labels:
        app: nginx-test-pod
    spec:
      containers:
        - name: nginx-test-pod
          image: nginx:latest
          imagePullPolicy: Never
          ports:
            - containerPort: 80
          volumeMounts:
            - mountPath: /etc/nginx/nginx.conf
              readOnly: true
              name: nginx-v-conf
              subPath: nginx.conf
            - mountPath: /etc/nginx/conf.d
              readOnly: true
              name: nginx-v-conf-d
            - mountPath: /html
              readOnly: true
              name: nginx-v-html
            - mountPath: /modules
              readOnly: true
              name: nginx-v-module
      volumes:
        - name: nginx-v-conf-d
          hostPath:
            path: /data/k8s/nginx/conf.d
            #readOnly: true
        - name: nginx-v-html
          hostPath:
            path: /data/k8s/nginx/html
            #readOnly: true
        - name: nginx-v-module
          hostPath:
            path: /data/k8s/nginx/modules
            #readOnly: true
        - name: nginx-v-conf
          configMap:
            name: nginx-conf
            items:
              - key: nginx.conf
                path: nginx.conf
      
---

apiVersion: v1
kind: Service
metadata:
  name: nginx-test-service
  namespace: nginx-test
spec:
  #externalTrafficPolicy: Cluster
  selector:
    app: nginx-test-pod
  ports:
  - protocol: TCP
    port: 8081 # match for service access port
    targetPort: 80 # match for pod access port
    #nodePort: 30088 # match for external access port
  type: ClusterIP

---

apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: nginx-test-ingress
  namespace: nginx-test
spec:
  ingressClassName: nginx
  rules:
   - host: 'ng.s3.local'
     http:
      paths:
        - path: /
          pathType: Prefix
          backend:
            service:
              name: nginx-test-service
              port:
                number: 8081


```


#### some command

```bash

kubectl get nods
kubectl get pods -A
kubectl get pods -n namespace

# namespace -> ns
# configmap -> cm
kubectl create namespace test-namespace
kubectl create ns test-namespace


kubectl create configmap test-config -n test-namespace --from-file=hellp.properties
kubectl get cm -n test-namespace test-config
kubectl get cm -n test-namespace test-config -o yaml

kubectl delete ns test-namespace

# deployment -> dm ?
kubectl create deployment test-deployment

# restart pod
kubectl rollout restart deployment test-deployment -n test-namespace

# exec
kubectl exec -ti test-deployment -n test-namespace bash

```




```x.yaml
apiVersion: v1
kind: Namespace
metadata:
  name: ssry

---

apiVersion: v1
data:
  gateway.properties: "spring.application.name = alphadata-gateway\r\nserver.port
    = 80\r\nspring.cloud.nacos.discovery.server-addr = 192.168.124.122:8848\r\nspring.cloud.nacos.discovery.namespace=ssry-k8s\r\n\r\n#spring.cloud.gateway.globalcors.cors-configurations.[/**].allowed-headers
    = *\r\n#spring.cloud.gateway.globalcors.cors-configurations.[/**].allowed-methods
    = *\r\n#spring.cloud.gateway.globalcors.cors-configurations.[/**].allowed-origin-patterns
    = *\r\n#spring.cloud.gateway.globalcors.add-to-simple-url-handler-mapping = true\r\n\r\n\r\nspring.cloud.gateway.routes[0].id
    = alphadata-community-service\r\nspring.cloud.gateway.routes[0].uri = lb://alphadata-community-service/\r\nspring.cloud.gateway.routes[0].predicates[0]
    = Path=/community/**\r\nspring.cloud.gateway.routes[0].filters[0] = StripPrefix=1\r\n\r\nspring.cloud.gateway.routes[1].id
    = alphadata-face-service\r\nspring.cloud.gateway.routes[1].uri = lb://alphadata-face-service/\r\nspring.cloud.gateway.routes[1].predicates[0]
    = Path=/face/**\r\nspring.cloud.gateway.routes[1].filters[0] = StripPrefix=1\r\n\r\nspring.cloud.gateway.routes[2].id
    = alphadata-dictionary-service\r\nspring.cloud.gateway.routes[2].uri = lb://alphadata-dictionary-service/\r\nspring.cloud.gateway.routes[2].predicates[0]
    = Path=/dictionary/**\r\nspring.cloud.gateway.routes[2].filters[0] = StripPrefix=1\r\n\r\nspring.cloud.gateway.routes[3].id
    = alphadata-trajectory-service\r\nspring.cloud.gateway.routes[3].uri = lb://alphadata-trajectory-service/\r\nspring.cloud.gateway.routes[3].predicates[0]
    = Path=/trajectory/**\r\nspring.cloud.gateway.routes[3].filters[0] = StripPrefix=1\r\n\r\nspring.cloud.gateway.routes[4].id
    = alphadata-case-service\r\nspring.cloud.gateway.routes[4].uri = lb://alphadata-case-service/\r\nspring.cloud.gateway.routes[4].predicates[0]
    = Path=/case/**\r\nspring.cloud.gateway.routes[4].filters[0] = StripPrefix=1\r\n\r\nspring.cloud.gateway.routes[5].id
    = alphadata-surveillance-service\r\nspring.cloud.gateway.routes[5].uri = lb://alphadata-surveillance-service/\r\nspring.cloud.gateway.routes[5].predicates[0]
    = Path=/surveillance/**\r\nspring.cloud.gateway.routes[5].filters[0] = StripPrefix=1\r\n\r\nspring.cloud.gateway.routes[6].id
    = alphadata-person-control-service\r\nspring.cloud.gateway.routes[6].uri = lb://alphadata-person-control-service/\r\nspring.cloud.gateway.routes[6].predicates[0]
    = Path=/control/**\r\nspring.cloud.gateway.routes[6].filters[0] = StripPrefix=1\r\n\r\nspring.cloud.gateway.routes[7].id
    = alphadata-user-service\r\nspring.cloud.gateway.routes[7].uri = lb://alphadata-user-service/\r\nspring.cloud.gateway.routes[7].predicates[0]
    = Path=/user/**\r\nspring.cloud.gateway.routes[7].filters[0] = StripPrefix=1\r\n\r\nspring.cloud.gateway.routes[8].id
    = alphadata-community-sync-data\r\nspring.cloud.gateway.routes[8].uri = lb://alphadata-community-sync-data/\r\nspring.cloud.gateway.routes[8].predicates[0]
    = Path=/communitySyncData/**\r\nspring.cloud.gateway.routes[8].filters[0] = StripPrefix=1\r\n\r\nspring.cloud.gateway.routes[9].id
    = alphadata-device-service\r\nspring.cloud.gateway.routes[9].uri = lb://alphadata-device-service/\r\nspring.cloud.gateway.routes[9].predicates[0]
    = Path=/device/**\r\nspring.cloud.gateway.routes[9].filters[0] = StripPrefix=1\r\n\r\nspring.cloud.gateway.routes[10].id
    = alphadata-approval-service\r\nspring.cloud.gateway.routes[10].uri = lb://alphadata-approval-service/\r\nspring.cloud.gateway.routes[10].predicates[0]
    = Path=/approval/**\r\nspring.cloud.gateway.routes[10].filters[0] = StripPrefix=1\r\n\r\nspring.cloud.gateway.routes[11].id
    = alphadata-app\r\nspring.cloud.gateway.routes[11].uri = lb://alphadata-app/\r\nspring.cloud.gateway.routes[11].predicates[0]
    = Path=/api/**\r\nspring.cloud.gateway.routes[11].filters[0] = StripPrefix=2\r\n\r\nspring.cloud.gateway.routes[12].id
    = alphadata-gun-control\r\nspring.cloud.gateway.routes[12].uri = lb://alphadata-gun-control/\r\nspring.cloud.gateway.routes[12].predicates[0]
    = Path=/qt/**\r\n\r\nspring.cloud.gateway.routes[13].id
    = alphadata-gun-app\r\nspring.cloud.gateway.routes[13].uri = lb://alphadata-gun-app/\r\nspring.cloud.gateway.routes[13].predicates[0]
    = Path=/gunapp/**\r\nspring.cloud.gateway.routes[13].filters[0] = StripPrefix=1\r\n\r\nspring.cloud.gateway.routes[14].id
    = alphadata-graphhopper\r\nspring.cloud.gateway.routes[14].uri = lb://alphadata-graphhopper/\r\nspring.cloud.gateway.routes[14].predicates[0]
    = Path=/graphhopper/**\r\nspring.cloud.gateway.routes[14].filters[0] = StripPrefix=1\r\n\r\nspring.cloud.gateway.routes[15].id
    = alphadata-database-view\r\nspring.cloud.gateway.routes[15].uri = lb://alphadata-database-view/\r\nspring.cloud.gateway.routes[15].predicates[0]
    = Path=/VIID/**\r\n\r\nspring.cloud.gateway.routes[16].id = alphadata-device-statistics\r\nspring.cloud.gateway.routes[16].uri
    = lb://alphadata-device-statistics/\r\nspring.cloud.gateway.routes[16].predicates[0]
    = Path=/deviceStatistics/**\r\n\r\n\r\nauth.skip.urls[0] = /user/jwt/login\r\nauth.skip.urls[1]
    = /user/jwt/getUserInfoByToken\r\nauth.skip.urls[2] = /user/unifiedLogin/index\r\nauth.skip.urls[3]
    = /api/app/auth/login\r\nauth.skip.urls[4] = /api/app/unifiedLogin/fenghuo/index\r\nauth.skip.urls[5]
    = /gunapp/user/jwt/login\r\n\r\n\r\n# end\r\n"
kind: ConfigMap
metadata:
  name: gateway-config
  namespace: ssry

--- 

apiVersion: apps/v1
kind: Deployment
metadata:
  name: gateway-deployment
  namespace: ssry
spec:
  selector:
    matchLabels:
      app: gateway-pod
  replicas: 1
  template:
    metadata:
      labels:
        app: gateway-pod
    spec:
      containers:
        - name: gateway-pod
          image: ssry/gateway:latest
          imagePullPolicy: Never
          env:
            - name: JAVA_OPTS
              value: "-Dspring.config.location=/gateway.properties -Xmx2g -Dserver.port=80"
          ports:
            - containerPort: 80
          volumeMounts:
            - mountPath: /gateway.properties
              readOnly: true
              name: gateway-config
              subPath: gateway.properties
      volumes:
        - name: gateway-config
          configMap:
            name: gateway-config
            items:
              - key: gateway.properties
                path: gateway.properties
      
---

apiVersion: v1
kind: Service
metadata:
  name: gateway-service
  namespace: ssry
spec:
  #externalTrafficPolicy: Cluster
  selector:
    app: gateway-pod
  ports:
  - protocol: TCP
    port: 80 # match for service access port
    targetPort: 80 # match for pod access port
    #nodePort: 30000 # match for external access port
  type: ClusterIP

---

apiVersion: apps/v1
kind: Deployment
metadata:
  name: ssry-deployment
  namespace: ssry
spec:
  selector:
    matchLabels:
      app: ssry-pod
  replicas: 1
  template:
    metadata:
      labels:
        app: ssry-pod
    spec:
      containers:
        - name: ssry-pod
          image: ssry/ssry:latest
          imagePullPolicy: Never
          env:
            - name: JAVA_OPTS
              value: "-Xmx2g -Dspring.redis.database=12 -Dserver.port=80 -Dspring.cloud.nacos.discovery.namespace=ssry-k8s"
          ports:
            - containerPort: 80
      
---

apiVersion: v1
kind: Service
metadata:
  name: ssry-service
  namespace: ssry
spec:
  #externalTrafficPolicy: Cluster
  selector:
    app: ssry-pod
  ports:
  - protocol: TCP
    port: 80 # match for service access port
    targetPort: 80 # match for pod access port
    #nodePort: 30001 # match for external access port
  type: ClusterIP

---

apiVersion: apps/v1
kind: Deployment
metadata:
  name: user-deployment
  namespace: ssry
spec:
  selector:
    matchLabels:
      app: user-pod
  replicas: 1
  template:
    metadata:
      labels:
        app: user-pod
    spec:
      containers:
        - name: user-pod
          image: ssry/user:latest
          imagePullPolicy: Never
          env:
            - name: JAVA_OPTS
              value: "-Xmx2g -Dserver.port=80 -Dspring.redis.database=12 -Dspring.cloud.nacos.discovery.server-addr=192.168.124.122:8848 -Dspring.cloud.nacos.discovery.namespace=ssry-k8s"
          ports:
            - containerPort: 80
      
---

apiVersion: v1
kind: Service
metadata:
  name: user-service
  namespace: ssry
spec:
  #externalTrafficPolicy: Cluster
  selector:
    app: user-pod
  ports:
  - protocol: TCP
    port: 80 # match for service access port
    targetPort: 80 # match for pod access port
    #nodePort: 30001 # match for external access port
  type: ClusterIP

---

apiVersion: v1
kind: ConfigMap
metadata:
  name: nginx-config
  namespace: ssry
data:
  nginx.conf: |
    user  nginx;
    worker_processes  auto;
    
    error_log  /var/log/nginx/error.log notice;
    pid        /var/run/nginx.pid;
    
    
    events {
        worker_connections  1024;
    }
    
    
    http {
        include       /etc/nginx/mime.types;
        default_type  application/octet-stream;
    
        log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                          '$status $body_bytes_sent "$http_referer" '
                          '"$http_user_agent" "$http_x_forwarded_for"';
    
        access_log  /var/log/nginx/access.log  main;
    
        sendfile        on;
        #tcp_nopush     on;
    
        keepalive_timeout  65;
    
        #gzip  on;
    
        server {
            listen       80;
            server_name  localhost;
        
            #access_log  /var/log/nginx/host.access.log  main;
        
        
            location / {
                root   /htmls/ssry;
        	try_files $uri $uri/ /index.html;
                index  index.html index.htm;
            }
        
            location /qt {
                proxy_set_header Host $http_host;
                proxy_set_header X-Real-IP $remote_addr;
                proxy_set_header REMOTE-HOST $remote_addr;
                proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
                proxy_pass http://gateway-service;
                proxy_http_version 1.1;
                proxy_set_header Upgrade $http_upgrade;
                proxy_set_header Connection "upgrade";
            }
        
            location /zhxqapi/user {
                proxy_redirect off;
                proxy_pass http://gateway-service/user;
                proxy_set_header X-Real-IP $remote_addr;
                proxy_set_header X-Forwarded-Proto $scheme;
                proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
                proxy_set_header Host $http_host;
            }
        }
    
    }

---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-deployment
  namespace: ssry
spec:
  selector:
    matchLabels:
      app: nginx-pod
  replicas: 1
  template:
    metadata:
      labels:
        app: nginx-pod
    spec:
      containers:
        - name: nginx-pod
          image: nginx:latest
          imagePullPolicy: Never
          ports:
            - containerPort: 80
          volumeMounts:
            - mountPath: /etc/nginx/nginx.conf
              readOnly: true
              name: nginx-v-conf
              subPath: nginx.conf
            - mountPath: /htmls/ssry
              readOnly: true
              name: nginx-v-ssry
      volumes:
        - name: nginx-v-ssry
          hostPath:
            path: /data/apps/ssry/front/SSRY
        - name: nginx-v-conf
          configMap: 
            name: nginx-config
            items:
              - key: nginx.conf
                path: nginx.conf
---

apiVersion: v1
kind: Service
metadata:
  name: nginx-service
  namespace: ssry
spec:
  #externalTrafficPolicy: Cluster
  selector:
    app: nginx-pod
  ports:
  - protocol: TCP
    port: 30000 # match for service access port
    targetPort: 80 # match for pod access port
    nodePort: 30000 # match for external access port
  type: NodePort

---


```