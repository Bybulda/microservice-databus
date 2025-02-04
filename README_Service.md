### Сборка образов

---

Фильтрация сообщений: чтобы собрать образы нужно будет переходить по папкам в jar-files, команды ниже учитывают перемещение по папкам из корневой папки проекта(если запускать последовательно в указанном ниже порядке)
```shell
cd jar-files/Filtering
docker build -t filtering -f ./FilteringDockerFile .
cd ../
```

Дедупликация сообщений
```shell
cd ./Deduplication
docker build -t deduplication -f ./DeduplicationDockerFile .
cd ../
```

Обогащение сообщений
```shell
cd ./Enrichment
docker build -t enrichment -f ./EnrichmentDockerFile .
cd ../
```

Менеджмент сообщений
```shell
cd ./Managment
docker build -t managment -f ./ManagmentDockerFile .
cd ../../
```

---

### Запустить контейнеры
```shell
docker compose up
```

Проверка
```shell
kafka-console-producer.sh --bootstrap-server localhost:9092 --topic input
```

```shell
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic output
```

```
{"name":"nikita", "age":3, "sex":"M"}
```