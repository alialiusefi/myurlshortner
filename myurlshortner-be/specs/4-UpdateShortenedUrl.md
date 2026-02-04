# Update the shortened url

### Description

Updates a shortened url.

### Requests

```http
PATCH https://{hostname}/shortened-urls/{uniqueIdentifier}
Content-Type: application/json
User-Id: 1
Authorization: 
{
  "url": "https://www.newurl.com", 
  "is_enabled": false,
  "title": "sale on paper"
}
{
  "url": "https://www.newurl.com"
}
{
  "title": "sale on paper"
}
```

### Responses

200 OK

```json
{
  "unique_identifier": "poiuytrewq",
  "shortened_url": "http://{hostname}/goto/poiuytrewq",
  "url": "https://www.google.com?q=rr",
  "is_enabled": true,
  "created_at": "2025-01-01T01:05:12.123+09:00",
  "updated_at": "2025-01-01T01:05:12.123+09:00",
  "user_id": 1,
  "title": "a" // nullable
}
```

400 Bad Request

```json
{
  "errors": [
    {
      "code": "URL_FORMAT_IS_NOT_CORRECT",
      "details": "The url '%s' provided is not correct."
    },
    {
      "code": "URL_IS_EMPTY",
      "details": "The url is empty."
    },
    {
      "code": "URL_IS_NOT_HTTP",
      "details": "HTTP protocol is supported only."
    },
    {
      "code": "URL_IS_MISSING_A_HOSTNAME",
      "details": "The url '%s' is missing a hostname."
    },
    {
      "code": "URL_IS_TOO_LONG",
      "details": "The url is too long."
    },
    {
      "code": "URL_CANNOT_BE_A_SHORTENED_URL",
      "details": "The provided url %s cannot be a shortened url."
    },
    {
      "code": "TITLE_IS_NOT_CORRECT",
      "details": "The title provided is not correct. It cannot exceed 100 characters or be null."
    }
  ]
}
```

404 NotFound
No Response Body
