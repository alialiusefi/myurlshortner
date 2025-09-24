# Update the original url

### Description

Updates an original url in a shortened url.

### Requests

```http
POST https://{hostname}/shortened-urls/{uniqueIdentifier}
Content-Type: application/json
Authorization: 
{
  "url": "https://www.newurl.com"
}
```

### Responses

204 No Content

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
    }
  ]
}
``` 

404 NotFound
No Response Body
