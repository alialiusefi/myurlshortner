# Shorten An Url

Started At: 2025-08-21T19:58

### User flow

When the user opens the homepage, he's greeted with a form the allows him to submit a valid url to shorten.
The form consists of:

- Title 
- URL Input
  - Label: URL
  - Placeholder: https://www.example.com
  - Validation:
    - Must be a valid http url.
- Submit Button
  - Text: 'Shorten'

Upon submission, a small closeable modal must appear with the short url. The modal consists of:

- Success Message:
  - Your url has been shortened successfully.
- Short url
  - It is presented in an easy selectable box, with a button on the right side to copy.
- Exit Button on the top right

