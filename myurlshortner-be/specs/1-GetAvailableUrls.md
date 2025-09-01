# Get Available Urls

StartedAt: 2025-09-01
Time to develop: 2hr
Time to test: 1hr

### Description

Returns list of shortened urls instead of mock.

### Requests

> page - page number, [1,INTEGER_MAX]
> size - page size, [1, 100]

```http
Authorization:
https://{hostname}/shortened-urls?page=1&size=10
```

### Responses

200 OK
```json
{
  "data": [
    {
      "url": "https://www.google.com",
      "shortened_url": "https://{hostname}/goto/nbjgop38vn"
    }
  ],
  "total": 1
}

```

400 Bad Request
```json
{
  "errors": [
    {
      "code": "PAGE_QUERY_PARAM_IS_NOT_CORRECT",
      "message": "The provided page '$s' should start from 1."
    },
    {
      "code": "SIZE_QUERY_PARAM_IS_NOT_CORRECT",
      "message": "The provided size '$s' should be from 1 to 100."
    }
  ]
}
```

404 NotFound
No Response Body

### Tech Debt and Blockers
- Integration tests do not check content.
- No Error handling.
- Mock data.
