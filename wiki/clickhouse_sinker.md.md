`https://github.com/housepower/clickhouse_sinker`
## 把kafka数据输出到ck

# ./clickhouse_sinker --local-cfg-file httplog.json

```
# httplog.json

{
  "clickhouse": {
    "hosts": [
      [
        "127.0.0.1"
      ]
    ],
    "port": 9000,
    "db": "default",
    "username": "",
    "password": "",
    "retryTimes": 0
  },
  "kafka": {
    "brokers": "127.0.0.1:9093",
    "version": "2.5.0"
  },
  "task": {
    "name": "test_fixed_schema",
    "topic": "topic1",
    "consumerGroup": "test_fixed_schema",
    "earliest": true,
    "parser": "json",
    "tableName": "test_fixed_schema",
    "dims": [
      {
        "name": "time",
        "type": "DateTime",
        "sourceName": "msg.time"
      },
      {
        "name": "host",
        "type": "String",
        "sourceName": "fields.host"
      },
      {
        "name": "value",
        "type": "Float32"
      }
    ],
    "bufferSize": 50000
  },
  "logLevel": "debug"
}
```