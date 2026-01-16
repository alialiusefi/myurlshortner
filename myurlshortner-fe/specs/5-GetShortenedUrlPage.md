# Get shortened url page

Time: 1 day

### User flow

When the user is directed to the page, the user can:

1. See the info of a shortened url.
2. View the history of the shortened url.

If the unique identifier provided doesnt exist, redirect user back to `http://{hostname}/browse`.

### Path

```http
http://{hostname}/browse/{uniqueIdentifier}/info
```

### Page view

- Back Button
  - Redirects the user back to the browse page.

- Gift Button
  - Opens a modal with a form to create a gift request of the current shortened url to a user.

- Title: Info
  - Your unique identifier: {unique_identifier}
  - {shortened_link} green arrow -> (red X if disabled) {target_url}
  - Created At
  - Last Updated At

- Title: History
  - Scrollable list of shortened url states.
  - 2 elements can be viewed at once.
