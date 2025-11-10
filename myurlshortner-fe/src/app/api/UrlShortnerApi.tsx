"use client";

import useSWR from "swr";
import { ErrorResponse } from "./Errors";

export const GenerateUniqueIdSWR = () => {
  const requestConfig = {
    method: "POST",
  };
  const serverUrl = process.env.NEXT_PUBLIC_EXTERNAL_SERVER_URL;
  const fetcher = (url) =>
    fetch(url, requestConfig)
      .then((response) => {
        if (response.ok) {
          return response.json();
        } else {
          throw new Error("Unexpected Error Response!");
        }
      })
      .then((json) => json as GenerateUniqueIdResponse)
      .catch((err) => err);
  return useSWR(`${serverUrl}/unique-identifiers`, fetcher);
};

export const GenerateUniqueIdFetch =
  async (): Promise<GenerateUniqueIdResponse> => {
    const serverUrl = process.env.NEXT_PUBLIC_EXTERNAL_SERVER_URL;
    const requestConfig = {
      method: "POST",
    };
    return fetch(`${serverUrl}/unique-identifiers`, requestConfig)
      .then((response) => {
        if (response.ok) {
          return response.json();
        } else {
          throw new Error("Unexpected Error Response!");
        }
      })
      .then((json) => json as GenerateUniqueIdResponse)
      .catch((err) => err);
  };

export async function shortenUrlOperaton(
  url: string,
  uid: string,
): Promise<ShortenUrlResponse | ErrorResponse> {
  const request = new ShortenUrlRequest(url, uid);
  const requestConfig = {
    method: "POST",
    body: JSON.stringify(request),
    headers: {
      "Content-Type": "application/json",
    },
  };
  const serverUrl = process.env.NEXT_PUBLIC_EXTERNAL_SERVER_URL;
  try {
    const rawResponse = await fetch(`${serverUrl}/shorten`, requestConfig);
    if (!rawResponse.ok) {
      const jsonErrorResponse = await rawResponse.json();
      console.error(`Error while calling the BE! ${jsonErrorResponse}`);
      if (rawResponse.status == 400) {
        return jsonErrorResponse;
      }
    }
    const jsonResponse = await rawResponse.json();
    return new ShortenUrlResponse(jsonResponse.shortened_url);
  } catch (e) {
    console.error(`Error while calling the BE! ${e}`);
  }
}

export async function updateShortenedUrl(
  uniqueIdentifier: string,
  newOriginalUrl: string,
  isEnabled: boolean,
): Promise<ErrorResponse | null> {
  const serverUrl = process.env.NEXT_PUBLIC_EXTERNAL_SERVER_URL;
  const request = new UpdateShortenedUrlRequest(newOriginalUrl, isEnabled);
  const url = `${serverUrl}/shortened-urls/${uniqueIdentifier}`;
  const requestConfig = {
    method: "PATCH",
    body: JSON.stringify(request),
    headers: {
      "Content-Type": "application/json",
    },
  };
  return fetch(url, requestConfig)
    .then((response) => {
      if (!response.ok) {
        console.error(`Unexpected BE response! code: ${response.status}`);
        return response.json();
      }
      return null;
    })
    .then((json) => {
      if (json) {
        return json as ErrorResponse;
      }
      return null;
    })
    .catch((e) => {
      console.error(`Unexpected error! error: ${e}`);
      return null;
    });
}

class UpdateShortenedUrlRequest {
  url: string;
  is_enabled: boolean;
  constructor(url: string, is_enabled: boolean) {
    this.url = url;
    this.is_enabled = is_enabled;
  }
}

class ShortenUrlRequest {
  url: string;
  unique_identifier: string;

  constructor(url: string, unique_identifier: string) {
    this.url = url;
    this.unique_identifier = unique_identifier;
  }
}

export class ShortenUrlResponse {
  shortened_url: string;

  constructor(url: string) {
    this.shortened_url = url;
  }
}

export class GenerateUniqueIdResponse {
  unique_identifier: string;
}
