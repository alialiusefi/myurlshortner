# Gift a shortened url

A user can send his shortened url as a gift request to another user. 

## Request

```http
Authorization:
User-Id: 1
POST {hostname}/shortened-urls/{uid}/gift-request
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
