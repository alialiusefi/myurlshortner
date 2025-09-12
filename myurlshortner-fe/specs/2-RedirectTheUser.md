# Redirect the user

### User flow

- User is redirected to the original url when he accesses the shortened url
- User is shown the 404 page if he accesses a non existent page

### Urls

- the shortned url path the user will have `http://{hostname}/goto/{10-letter-identifier}`
- the user browser will call the backend with the following path `http://{hostname}/urls/{id}`
