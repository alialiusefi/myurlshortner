# Shorten An Url

Started At: 2025-08-21T19:58

### User flow

When the user opens the homepage, he's greeted with a form the allows him to submit a valid url to shorten.
The form consists of:

- Selection between Auto & Custom

Auto:
- Shortened Url
  - Shortened Url Label
  - App Url
  - Unique ID Input
    - Refresh button that regenerates the id
    - Input that is validated
- Submit Button
  - Text: 'Shorten'

Custom:
- Title
  - Up to 100 characters
  - Sets as "" by default
- Shortened Url
  - Shortened Url Label
  - App Url
  - Unique ID Input
    - Refresh button that regenerates the id
    - Input that is validated
- Target URL
  - Label: Target URL
  - Placeholder: https://www.example.com
  - Validation:
    - Must be a valid http url.
- Submit Button
  - Text: 'Shorten'

Upon submission, a small closeable modal must appear with the short url. The modal consists of:

- Success Message:
  - Success!
- Description:
  - 'URL: <shortened-url-here>'
- Short url
  - It is presented in a selectable text and linked to the link itself.
