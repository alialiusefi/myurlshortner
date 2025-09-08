# 1-UserAccessedShortenedUrlEvent

Started At: 2025-08-30T10:00
BE Development Time: 1 week
BE Testing Time: 1/2 day

### Description

Consumes shortened url user events. 

### Tech Debt and Dependencies 

Since this is the first implementation, Codebase has to be prepared:
- The consumer will support multiple event types. 
- Postgres Database Docker
- Database schema sql init
- Database connection config
- Database connection pool 
- ~~Scala ZIO Framework~~ Spring Framework with Kotlin
- Kafka Consumer Configuration
- Kafka Consumer
- Apicurio Registry Deserializer Configuration
- Existing event producer only supports one event. // Techdebt

### Event
-　UserAccessedShortenedUrl

### Data to save
- User Device
- User Browser
- User OS
- ShortenedUrl
- OriginalUrl
- AccessedAt
