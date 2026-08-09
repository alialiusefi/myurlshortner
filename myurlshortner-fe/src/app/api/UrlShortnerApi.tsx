"use client";

import useSWR from "swr";
import { ErrorResponse } from "./Errors";
import { buildUserIdHeader } from "./Utility";

const GenerateUniqueIdUrl = `${process.env.NEXT_PUBLIC_EXTERNAL_SERVER_URL}/unique-identifiers`;
const fetcher = (url): Promise<GenerateUniqueIdResponse> => {
  const requestConfig = {
    method: "POST",
  };
  return fetch(url, requestConfig)
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
export const GenerateUniqueIdSWR = () =>
  useSWR(GenerateUniqueIdUrl, fetcher, {
    suspense: true,
    fallbackData: { unique_identifier: "" },
  });
export const GenerateUniqueIdFetch = () => fetcher(GenerateUniqueIdUrl);

export async function shortenUrlOperaton(
  url: string,
  uid: string,
  userId: number,
  title?: string,
): Promise<ShortenUrlResponse | ErrorResponse> {
  const request = new ShortenUrlRequest(url, uid, title);
  const requestConfig = {
    method: "POST",
    body: JSON.stringify(request),
    headers: {
      ...buildUserIdHeader(userId),
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

type PatchShortenedUrlResponse = {
  unique_identifier: string;
  shortened_url: string;
  url: string;
  is_enabled: boolean;
  title: string;
  created_at: string;
  updated_at: string;
  user_id: number;
};

/**
 * Patch shortened url.
 * @param uniqueIdentifier Required Field
 * @param userId Required Field
 * @param newOriginalUrl Optional by passing 'undefined' or 'string'
 * @param isEnabled Optional by passing 'undefined' or 'string'
 * @param title Optional by passing 'undefined' otherwise can be 'null' or 'string'
 * @returns Updated Shortened Url
 */
export async function updateShortenedUrl(
  uniqueIdentifier: string,
  userId: number,
  newOriginalUrl: string,
  isEnabled: boolean,
  title?: string,
): Promise<PatchShortenedUrlResponse> {
  const serverUrl = process.env.NEXT_PUBLIC_EXTERNAL_SERVER_URL;
  const request = {
    url: newOriginalUrl,
    is_enabled: isEnabled,
    title: title,
  };
  const url = `${serverUrl}/shortened-urls/${uniqueIdentifier}`;
  const requestConfig = {
    method: "PATCH",
    body: JSON.stringify(request),
    headers: {
      "Content-Type": "application/json",
      ...buildUserIdHeader(userId),
    },
  };
  return fetch(url, requestConfig)
    .then(async (response) => {
      if (!response.ok) {
        console.error(`Unexpected BE response! code: ${response.status}`);
        return (await response.json()) as ErrorResponse;
      }
      return (await response.json()) as PatchShortenedUrlResponse;
    })
    .catch((e) => {
      console.error(`Unexpected error! error: ${e}`);
      return null;
    });
}

class ShortenUrlRequest {
  url: string;
  unique_identifier: string;
  title?: string;

  constructor(url: string, unique_identifier: string, title?: string) {
    this.url = url;
    this.unique_identifier = unique_identifier;
    this.title = title;
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
