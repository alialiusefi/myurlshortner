# Get shortened url history

### Description

Get shortened url history.

### Requests

```http
GET https://{hostname}/shortened-urls/{uniqueIdentifier}/history?offset=0&size=2&from=2025-10-15T15:12:42.978495+09:00
Content-Type: application/json
Authorization: 
```

### Responses

200 OK
```json
{
  "data" : [
    {
      "url": "https://www.google.com",
      "shortened_url": "http://{hostname}/goto/CMLqFwlRu7",
      "event_date_time": "2025-10-15T15:13:42.978495+09:00"
    },
    {
      "url": "https://www.youtube.com",
      "shortened_url": "http://{hostname}/goto/CMLqFwlRu7",
      "event_date_time": "2025-10-15T15:12:42.978495+09:00"
    }
  ]
}
```

400 Bad Request
```json
{
  "errors": [
    {
      "code": "OFFSET_PARAM_IS_NOT_CORRECT",
      "details": "The provided offset param '%s' is not correct."
    },
    {
      "code": "SIZE_QUERY_PARAM_IS_NOT_CORRECT",
      "details": "The provided size '%s' should be from 1 to 100."
    },
    {
      "code": "DATETIME_PARAM_IS_NOT_CORRECT",
      "details": "The provided datetime param '%s' is not correct."
    },
    {
      "code": "SHORTENED_URL_NOT_FOUND",
      "details": "The shortened url was not found."
    }
  ]
}
``` 

404 NotFound
No Response Body
