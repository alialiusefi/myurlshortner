# Get Available Urls

Time to develop: 2 days
Time to test: 1hr

### Description

Returns list of shortened urls. The list is ordered by `created_at`. `created_at` will follow the system timezone.

### Requests

> page - page number, [1,INTEGER_MAX], required
> size - page size, [1, 100], required
> order - order direction. [asc, desc], optional, default: desc

```http
Authorization:
https://{hostname}/shortened-urls?page=1&size=10&order=desc
```

### Responses

200 OK
```json
{
  "data": [
    {
      "url": "https://www.google.com",
      "shortened_url": "https://{hostname}/goto/nbjgop38vn",
      "created_at": "2025-01-01T01:05:12Z",
      "access_count": 1
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
    },
    {
      "code": "ORDER_PARAM_IS_NOT_CORRECT",
      "message": "The provided order param '$s' is not correct."
    }
  ]
}
```

### Tech Debt
- Persistence Layer
  - Table
  - ORM
  - Integration test setup
  - Supporting existing logic
- Consumer doesnt save unique identifier
