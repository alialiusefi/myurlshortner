# Redirect User

Started At: 2025-08-26T10:00
BE Development Time: 2 days
BE Testing Time: 1/2 day

### Description

Returns an url for user redirection.

### Tech debt blockers

### Requests

```http
GET https://{hostname}/url/{unique-identifier}
Content-Type: 
Authorization: 
User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0
```

### Responses

```http
307 Temporary Redirect
Location: https://www.example.com?query=test
```

```http
400 Bad Request
{
    errors: [
        {
            "code": UNIQUE_IDENTIFIER_IS_TOO_LONG
            "message": "The unique identifier provided is too long."
        }
    ]
}
{
    errors: [
        {
            "code": UNIQUE_IDENTIFIER_CANNOT_BE_EMPTY
            "message": "The unique identifier cannot be empty."
        },
        {
            "code": USER_AGENT_CANNOT_BE_EMPTY
            "message": "The user agent header cannot be empty."
        }
    ]
}
```

```http
404 Not Found
No Response Body
```

### Redirect Logic Description

After the user visits the shortened link and handled by FE, FE will use this endpoint to get the url to redirect the
user to.
User-Agent is a required header, it will be used for reporting.

There will be an event published when the endpoint is called successfully, as this means that the user has effectively
accessed the original url.

### User data to save
- User Device
- User Browser
- User OS

### Why Status Code 307 was picked
When returning TEMPORARY_REDIRECT, we are allowing the possibility to change where the short url will redirect to.
Also, it does not allow the user to tamper with the redirect request.
