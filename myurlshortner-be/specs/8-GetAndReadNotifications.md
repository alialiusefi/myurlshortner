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
  "data": [
    {
      "id": 1,
      "type": "SHORTENED_URL_REACHED_N_VIEWS",
      "params": {
        "unique_identifier": "abcdabcd11",
        "views": 10
      },
      "read_at": "2021-01-01T15:20:33.000+09:00" // nullable
    },
    {
      "id": 2,
      "type": "GIFT_REQUEST_TO_TARGET_USER",
      "params": {
        "unique_identifier": "abcdabcd11",
        "gift_request_id": 1,
        "source_user_id": 2
      },
      "read_at": "2021-01-01T15:20:33.000+09:00" // nullable
    },
    {
      "id": 3,
      "type": "GIFT_REQUEST_RESPONSE_TO_SOURCE_USER",
      "params": {
        "unique_identifier": "abcdabcd11",
        "target_user_id": 2,
        "status": "ACCEPTED" // | "DECLINED" | "EXPIRED"
      }
    }
  ]
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

---

# Read Notification

Read a specific notification by the same user.

## Request

Authorization: 
User-Id: 1
PUT /notifications/{id}
No Request Body

## Response

200 OK 
No Response Body

404 Not Found
No Response Body

400 Bad Request
```json
{
  "errors": [
    {
      "code": "USER_ID_IS_NOT_CORRECT",
      "message": "The provided user id value %s is not correct. It must be a number above 0"
    },
    {
      "code": "NOTIFICATION_ID_IS_NOT_CORRECT",
      "message": "The provided notification id value %s is not correct. It must be a number above 0"
    }
  ]
}
```
409 Conflict
```json
{
  "errors": [
    { 
      "code": "NOTIFICATION_IS_ALREADY_READ",
      "message": "The notification is already read."
    }
  ]
}
```
