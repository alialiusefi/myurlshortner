# Generate a shortened url

### Description

Returns a shortened url that should be accessible.

### Requests

```http
POST https://{hostname}/shorten
Content-Type: application/json
User-Id: 1
Authorization: 
{
  "url": "https://www.google.com",
  "unique_identifier": "a"
}
{
  "url": "www.google.com"
}
{
  "url": "google.com"
}
{
  "url": "http://google.com"
}
```

### Responses

201 Created
```json
{
  "shortened_url": "https://{hostname}/goto/a"
}

{
  "shortened_url": "https://{hostname}/goto/wLf16-ft"
}

```

400 Bad Request
```json
{
  "errors": [
    {
      "code": "UNIQUE_IDENTIFIER_ALREADY_EXISTS",
      "details": "The provided unique identifier already exists."
    },
    {
      "code": "UNIQUE_IDENTIFIER_CANNOT_BE_EMPTY",
      "details": "The unique identifier cannot be empty."
    },
    {
      "code": "UNIQUE_IDENTIFIER_IS_TOO_LONG",
      "details": "Unique identifier is too long."
    }
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
