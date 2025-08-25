"use client";

export default async function shortenUrlOperaton(
  url: string,
): Promise<ShortenUrlResponse | ErrorResponse> {
  const request = new ShortenUrlRequest((url = url));
  const requestConfig = {
    method: "POST",
    body: JSON.stringify(request),
    headers: {
      "Content-Type": "application/json",
    },
  };
  try {
    const rawResponse = await fetch(
      "http://localhost:8080/shorten",
      requestConfig,
    );
    if (!rawResponse.ok) {
      const jsonErrorResponse = await rawResponse.json();
      console.error(`Error while calling the BE! ${jsonErrorResponse}`);
      if (rawResponse.status == 400) {
        return jsonErrorResponse;
      }
    }
    const jsonResponse = await rawResponse.json();
    console.log(jsonResponse);
    return new ShortenUrlResponse(jsonResponse.shortened_url);
  } catch (e) {
    console.error(`Error while calling the BE! ${e}`);
  }
}

export class ShortenUrlRequest {
  url: string;

  constructor(url: string) {
    this.url = url;
  }
}

export class ShortenUrlResponse {
  shortened_url: string;

  constructor(url: string) {
    this.shortened_url = url;
  }
}

export class ErrorResponse {
  errors: [Error];

  constructor(errors: [Error]) {
    this.errors = errors;
  }
}

export class Error {
  code: string;
  message: string;

  constructor(code: string, message: string) {
    this.code = code;
    this.message = message;
  }
}
