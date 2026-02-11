# Get Shortened Url

Return details of a shortened url

```http
GET http://{hostname}/shortened-urls/{uid}
Content-Type: application/json
User-Id: 1
Authorization:
```

Responses:

200 OK
```json
{
  "unique_identifier": "poiuytrewq",
  "shortened_url": "http://{hostname}/goto/poiuytrewq",
  "url": "https://www.google.com?q=rr",
  "is_enabled": true,
  "created_at": "2025-01-01T01:05:12.123+09:00",
  "updated_at": "2025-01-01T01:05:12.123+09:00",
  "title": "Google - The center of web" // nullable
}
```

400 Bad Request
```json
{
  "errors": [
    {
      "code": "UNIQUE_IDENTIFIER_IS_TOO_LONG",
      "message": "The unique identifier provided is too long."
    },
    {
      "code": "UNIQUE_IDENTIFIER_CANNOT_BE_EMPTY",
      "message": "The unique identifier cannot be empty."
    }
  ]
}
```

404 Not Found
No Response Body