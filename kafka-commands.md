# Kafka & Registry Commands

> brew install kafka

- Cluster Metadata
```bash
kafka-metadata-quorum --bootstrap-server localhost:9092 describe --status
```

- Get consumer groups
```bash
kafka-consumer-groups --bootstrap-server localhost:9092 --list 
```

- Get consumer group details
```bash
kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group <group-name> 
```

- Create a topic
```bash
kafka-topics --bootstrap-server localhost:9092 --create --topic shortened-url-events --partitions 1 --replication-factor 1
```

- Get topics
```bash
kafka-topics --bootstrap-server localhost:9092 --describe --topic shortened-url-events
```

- Read from topic
```bash
kafka-console-consumer --topic shortened-url-events --bootstrap-server localhost:9092
```

- Create schema
- https://www.apicur.io/registry/docs/apicurio-registry/3.0.x/getting-started/assembly-managing-registry-artifacts-api.html#managing-artifact-versions-using-rest-api_registry
```bash
curl -X POST http://localhost:8888/apis/registry/v3/groups/com.acme.events/artifacts \
  -H "Content-Type: application/json" \
  --data-raw '{
    "artifactId": "ShortenedUrlUserEvents",
    "artifactType": "AVRO",
    "name": "ShortenedUrlUserEvents",
    "firstVersion": {
        "content": {
            "content": "[{\"type\":\"record\",\"name\":\"ShortenedUrlUserEvents\",\"namespace\":\"com.acme.events\",\"fields\":[{\"name\":\"userAccessedShortenedUrlEvent\",\"type\":[\"null\",\"UserAccessedShortenedUrl\"],\"default\":null},{\"name\":\"userCreatedShortenedUrlEvent\",\"type\":[\"null\",\"UserCreatedShortenedUrl\"],\"default\":null},{\"name\":\"userUpdatedOriginalUrlEvent\",\"type\":[\"null\",\"UserUpdatedOriginalUrl\"],\"default\":null}]},{\"type\":\"record\",\"name\":\"UserAccessedShortenedUrl\",\"namespace\":\"com.acme.events\",\"fields\":[{\"name\":\"unique_identifier\",\"type\":[\"null\",\"string\"],\"default\":\"null\"},{\"name\":\"shortened_url\",\"type\":\"string\"},{\"name\":\"original_url\",\"type\":\"string\"},{\"name\":\"user_agent\",\"type\":\"string\"},{\"name\":\"accessed_at\",\"type\":[\"null\",\"string\"],\"doc\":\"ISO 8601 Timestamp\",\"default\":\"null\"}]},{\"type\":\"record\",\"name\":\"UserCreatedShortenedUrl\",\"namespace\":\"com.acme.events\",\"fields\":[{\"name\":\"unique_identifier\",\"type\":\"string\"},{\"name\":\"original_url\",\"type\":\"string\"},{\"name\":\"created_at\",\"type\":\"string\",\"doc\":\"ISO 8601 Timestamp\"}]},{\"type\":\"record\",\"name\":\"UserUpdatedOriginalUrl\",\"namespace\":\"com.acme.events\",\"fields\":[{\"name\":\"unique_identifier\",\"type\":\"string\"},{\"name\":\"new_original_url\",\"type\":\"string\"},{\"name\":\"updated_at\",\"type\":\"string\",\"doc\":\"ISO 8601 Timestamp\"}]}]",
            "contentType": "application/json",
            "references": [ ]
        }
    }
}'
```
