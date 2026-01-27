# Notifications

Show the list of notifications when the bell icon on the right side of the app bar is clicked. The bell icon has a badge on unread notifications.
The maximum amount of notifications shown is 5.

Every notification consists of:

- Title (faded when read otherwise bold)
- Description
- "Mark as read" button to mark the notification as read. It cannot be unmarked.

## Notification Types

1. Shortened Url reached 10 views!

- Title: Congrats! Your shortened url reached 10 views!
- Description: Your shortened url with id **abcdabcd11**(links to info page) has reached 10 views!

2. Target User received shortened url gift request

- Title: You have received a shortened url gift request!
- Description: The user with id **1** have sent you a shortened url with id **abcdabcd11**.
- Action button: The action button opens a modal with shortened url info and to accept/reject or exit.

3. Source User received gift request result

- Title:
  - Your gift request of shortened url with id **abcdabcd11** to user id 2 was accepted.
  - Your gift request of shortened url with id **abcdabcd11** to user id 2 was declined.
  - Your gift request of shortened url with id **abcdabcd11** to user id 2 has expired after 24 hours.
- Description:
  - empty
