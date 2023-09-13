
`https://www.elastic.co/guide/en/beats/filebeat/current/configuring-howto-filebeat.html`

## 记录日志到kafka



```
# httplog.yml
filebeat.inputs:
- type: filestream
  enabled: true  
  paths:
    - /data/apps/logs/s0/http.log
# 自定义字段数据
# 在json里面为: {fileds: {app: "s0", host: "10.0.0.1"}}
  fields:
    app: "s0"
    host: "10.0.0.1"
# 可以多个, 指定不同的app或者自定义字段
- type: filestream

# 输出到kafka
output.kafka:
  hosts: ["10.0.0.3:9092"]
  # 可以使用json里面的动态字段: %{[fields.topic]}
  topic: "httplog"

# 数据处理, 可以自定义格式化
processors:
  - dissect:
      # 这里基本是根据logback的pattern来分割的, 然后会把响应的内容放到msg下面
      # 结果大概是这样的: {msg: {time: "2022-01-01 11:11:11", tid: 1234}}
      tokenizer: '[%{msg.time} %{msg.pid} %{msg.tid|long}]'
      # 内容所属字段, 默认message
      fields: "message"
      target_prefix: ""
```