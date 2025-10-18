# Get shortened url info

### User flow

When the user is directed to the info page, the user can view the history of the shortened url. If the unique identifier provided doesnt exist, redirect user to 404.

### Path

```http
http://{hostname}/browse/{uniqueIdentifier}/info
```

### Page view

- Back Button
  - Redirects the user back to the browse page.
- Title: History
  - Scrollable list of shortened url states.
  - 2 elements can be viewed at once.
