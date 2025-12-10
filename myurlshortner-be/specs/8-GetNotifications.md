# Get Notifications

Returns latest 5 notifications available.

## Request

```http
Authorization:
User-Id: 1
GET {hostname}/notifications
```

## Responses

200 OK
```json
{
  "id": 1,
  "type": "SHORTENED_URL_REACHED_N_VIEWS",
  "params": {
      "uniqueIdentifier": "abcdabcd11",
      "views": 10
    }
}
```

400 Bad Request
```json
{
  "errors": [
    {
      "code": "USER_ID_IS_NOT_CORRECT",
      "message": "The provided user id value %s is not correct. It must be a number above 0"
    }
  ]
}
```
