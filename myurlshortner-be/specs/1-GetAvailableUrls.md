# Get Available Urls

### Description

Returns list of shortened urls that should be accessible with their metadata.

### Requests

> page - page number
> size - page size
> status - filtering by status of url

```http
https://{hostname}/shortened-urls?page=1&size=10&status=available
```

### Responses

200 OK
```json
{
  "data": [
    {
      "url": "https://www.google.com",
      "shortened_url": "https://www.shortner.com/hfu2--$31D123c"
    }
  ],
  "total": 1
}

```

400 Bad Request
```json
{
  "errors": [
  ]
}
```

404 NotFound
No Response Body

### Dependencies
