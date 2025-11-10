# Generate a unique identifier

### Description

Generates a valid random unique identifier. It always generates 10 characters.

### Requests

```http
POST https://{hostname}/unique-identifiers
Content-Type: application/json
Authorization: 
```

### Responses

201 Created
```json
{
  "unique_identifier": "abcabcabc1"
}
```


### Generate Logic Description

The shortened url will consist of the prefix of hostname + prefix,
action identifier to redirect /goto and generated unique short identifier.

Unique short identifier will be generated randomly from the ASCII table excluding some characters. It will result up to 62^10 combinations.

#### Characters that will be used:

- Characters from 48 to 57. Numbers
- Characters from 65 to 80. Capital Characters
- Characters from 97 to 122. Small Characters
- Character 45. Dash

#### Characters that will not be used:

- ASCII control characters 0-31
- URL reserved characters
- Other characters that are not listed [above](#characters-that-will-be-used).
