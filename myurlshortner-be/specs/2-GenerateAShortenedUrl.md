# Generate a shortened url

### Description

Returns a shortened url that should be accessible.

### Requests

```http
POST https://{hostname}/shorten
Content-Type: application/json
Authorization: 
{
  "url": "https://www.google.com"
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
  "shortened_url": "https://{hostname}/goto/wLf16-ft"
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
    }
  ]
}
```

404 NotFound
No Response Body

### Generate Logic Description

The shortened url will consist of the prefix of hostname + prefix,
action identifier to redirect /goto and generated unique short identifier.

Unique short identifier will be generated randomly from the ASCII table excluding some characters. It will result up to 62^10 combinations.

#### Characters that will be used:

- Characters from 48 to 57. Numbers
- Characters from 65 to 80. Capital Characters
- Characters from 97 to 122. Small Characters
- Character 45. Dash

#### Characters that will not be used:

- ASCII control characters 0-31
- URL reserved characters
- Other characters that are not listed [above](#characters-that-will-be-used).
