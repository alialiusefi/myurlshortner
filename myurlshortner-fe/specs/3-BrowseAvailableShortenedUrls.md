# Browse available shortened urls

### User flow

The user is able to navigate to the 'Browse' page using the navigation panel above.
The browse page returns the available shortned urls starting from the first page.
The ordering default is by created_at, and the direction is changeable.
Support pagination by 10 elements.

Search Bar:
  - Text Input
    - On Input:
      - Show popover with 5 matching titles
        - On Click:
          - Set the title and search
    - Placeholder:
      - Search By Title
    - Validation:
      - Up to 100 chars
  - Search Button
    - Search shortened urls by title
  - Reset Button
    - Reset and fetch first page

Table:
- Is Enabled
  - Enabled/Disabled state
- Title
  - Title ('will be '<empty>' if empty)
  - Updateable
  - Undoable
  - Up to 100 characters
- Shortened Url
  - It is hyperlinked.
- Original Url
  - It is hyperlinked. Also, If the url is too long, truncate it to 90 chars with a toggle to extend/hide.
- Visit Count
  - Showns the number of times the shortened url has been accessed.
- Created At
  - DateTime is to be shown with the browser system time zone.

### Path

```
> page - page number, [1,INTEGER_MAX], optional
> size - page size, [1, 100], optional
> order - order direction. [asc, desc], optional, default: desc
> title - title search param, [0, 100], optional.
```

```http
http://{hostname}/browse?page=5&size=10&order=desc&title=yellow
```
