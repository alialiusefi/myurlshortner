# Gift a shortened url

A user can send his shortened url as a gift request to another user. 

## Request

```http
Authorization:
User-Id: 1
POST {hostname}/shortened-urls/{uid}/gift-requests
{
    "target_user_id": 2
}
```

## Responses
201 Created

409 Conflict
```json
{
  "errors": [
    {
      "code": "TARGET_USER_ALREADY_HAS_SUCH_GIFT_REQUEST",
      "message": "The target user id %s has already a gift request pending for shortened url with id %s."
    }
  ]
}
```

400 Bad Request
```json
{
  "errors": [
    {
      "code": "SHORTENED_URL_ALREADY_HAS_A_GIFT_REQUEST",
      "message": "The shortened url with id %s has already a gift request pending."
    },
    {
      "code": "TARGET_USER_CANNOT_BE_THE_SOURCE_USER",
      "message": "The provided target user id %s is the source target user id."
    },
    {
      "code": "USER_ID_IS_NOT_CORRECT",
      "message": "The provided user id %s is not correct. It must be a number above 0"
    },
    {
      "code": "UNIQUE_ID_CONTAINS_INVALID_CHARACTERS",
      "message": "Unique identifier contains invalid characters."
    },
    {
      "code": "UNIQUE_IDENTIFIER_CANNOT_BE_EMPTY",
      "message": "The unique identifier cannot be empty."
    },
    {
      "code": "UNIQUE_IDENTIFIER_IS_TOO_LONG",
      "message": "Unique identifier is too long."
    }
  ]
}
```

404 Not Found
```json
{
  "errors": [
    {
      "code": "SHORTENED_URL_WAS_NOT_FOUND",
      "message": "Cannot find a shortened url with unique identifier %s."
    }
  ]
}
```

---
# Get Gift Request

Get a pending gift request for the shortened url.

## Request
```http
Authorization:
User-Id: 1
GET /{hostname}/shortened-urls/{uid}/gift-requests/awaiting
```

## Responses

200 OK
```json
{
  "id": 1,
  "updated_at": null
}
```

400 Bad Request
```json
{
  "errors": [
    {
      "code": "USER_ID_IS_NOT_CORRECT",
      "message": "The provided user id %s is not correct. It must be a number above 0"
    },
    {
      "code": "UNIQUE_ID_CONTAINS_INVALID_CHARACTERS",
      "message": "Unique identifier contains invalid characters."
    },
    {
      "code": "UNIQUE_IDENTIFIER_CANNOT_BE_EMPTY",
      "message": "The unique identifier cannot be empty."
    },
    {
      "code": "UNIQUE_IDENTIFIER_IS_TOO_LONG",
      "message": "Unique identifier is too long."
    }
  ]
}
```

404 Not Found
```json
{
  "errors": [
    {
      "code": "AWAITING_GIFT_REQUEST_WAS_NOT_FOUND",
      "message": "Cannot find an awaiting gift request for unique identifier %s."
    }
  ]
}
```
---
# Cancel Awaiting Gift Request

## Request

```http
Authorization: 
User-Id: 1
PUT /{hostname}/shortened-urls/{uid}/gift-requests/awaiting/{id}/cancel
{
    "updated_at": "2025-01-01T01:05:12.123+09:00"
}
```

## Responses

204 No Content

409 Conflict
```json
{
  "errors": [
    {
      "code": "GIFT_REQUEST_WAS_UPDATED",
      "message": "The gift request was already updated. Please retry."
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
      "message": "The provided user id %s is not correct. It must be a number above 0"
    },
    {
      "code": "UNIQUE_ID_CONTAINS_INVALID_CHARACTERS",
      "message": "Unique identifier contains invalid characters."
    },
    {
      "code": "UNIQUE_IDENTIFIER_CANNOT_BE_EMPTY",
      "message": "The unique identifier cannot be empty."
    },
    {
      "code": "UNIQUE_IDENTIFIER_IS_TOO_LONG",
      "message": "Unique identifier is too long."
    }
  ]
}
```

404 Not Found
```json
{
  "errors": [
    {
      "code": "SHORTENED_URL_WAS_NOT_FOUND",
      "message": "Cannot find a shortened url with unique identifier %s."
    },
    {
      "code": "AWAITING_GIFT_REQUEST_WAS_NOT_FOUND",
      "message": "Cannot find an awaiting gift request with id %s."
    }
  ]
}
```
