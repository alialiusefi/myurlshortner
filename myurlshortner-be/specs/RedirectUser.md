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

```
307 Temporary Redirect
Location: https://www.example.com?query=test
```

```
404 Not Found
No Response Body
```

### Redirect Logic Description

After the user visits the shortened link and handled by FE, FE will use this endpoint to get the url to redirect the user to.
User-Agent is a required header, it will be used for reporting.

There will be an event published when the endpoint is called successfully, as this means that the user has effectively accessed the original url. 

### User Redirected Event Example

Headers: 
- Key: {unique-identifier}
- Partition: 0
- Timestamp: 2025-08266T05:29:27Z // Example
- Topic: shortened-url-events
- Schema: shortened-url-events.com.acme.events.UserAccessedShortenedUrl
Event:
```json
{
  "data": {
    "unique_identifier": "OLIcbYN7iW",
    "user_agent": "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0" 
  }
}
```

### Why Status Code 307 was picked
