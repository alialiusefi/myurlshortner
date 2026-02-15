# Get Available Urls

### Description

Returns list of shortened urls. The list is ordered by `created_at`. `created_at` will follow the system timezone.

### Requests

> page - page number, [1,INTEGER_MAX], required
> size - page size, [1, 100], required
> order - order direction. [asc, desc], optional, default: desc
> title - filter shortened urls that has the provided substring. optional, can be empty. 

```http
Authorization:
User-Id: 1
https://{hostname}/shortened-urls?page=1&size=10&order=desc&title=e
```

### Responses

200 OK
```json
{
  "data": [
    {
      "unique_identifier": "nbjgop38vn",
      "url": "https://www.google.com",
      "shortened_url": "https://{hostname}/goto/nbjgop38vn",
      "created_at": "2025-01-01T01:05:12Z",
      "access_count": 1,
      "title": "red" // nullable
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
      "code": "TITLE_IS_NOT_CORRECT",
      "details": "The title provided is not correct. It cannot exceed 100 characters."
    },
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

---

# Get Matching Titles

### Description

Returns up to 5 distinct titles that match the search query.

### Request

```http
Authorization:
User-Id: 1
GET https://{hostname}/shortened-urls/titles?query=english
```

### Responses

200 OK
```json
[
    "The Oxford Dictionary - British English",
    "English Movies",
    "Amazon Books - Find books in english"
]
```

400 Bad Request
```json
{
  "errors": [
    {
      "code": "TITLE_IS_NOT_CORRECT",
      "details": "The title provided is not correct. It cannot exceed 100 characters."
    }
  ]
}
```

404 Not Found
