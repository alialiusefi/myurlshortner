# running flyway migrations


## commands for docker environment

```bash
flyway -user=postgres -password=safetypassword -url=jdbc:postgresql://localhost:5432/postgres info
```

```bash
flyway -user=postgres -password=safetypassword -url=jdbc:postgresql://localhost:5432/postgres migrate
```
