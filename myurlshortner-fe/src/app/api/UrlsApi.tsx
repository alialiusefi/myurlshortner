import useSWR from "swr";
import useSWRInfinite from "swr/infinite";
import { ErrorResponse } from "./Errors";
import { buildUserIdHeader } from "./Utility";

export const GetShortenedUrlInfoSWR = (
  uniqueIdentifier: string,
  userId: number,
) => {
  const serverUrl = process.env.NEXT_PUBLIC_EXTERNAL_SERVER_URL;
  const fetcher = (url) =>
    fetch(url, { headers: { ...buildUserIdHeader(userId) } }).then(
      async (res) => {
        if (res.ok) {
          const json = (await res.json()) as GetShortenedUrlInfoResponse;
          return json;
        }
        if (res.status == 404) {
          throw new GetShortenedUrlInfo404Response();
        }
        throw new Error("Unexpected BE response!");
      },
    );
  return useSWR(`${serverUrl}/shortened-urls/${uniqueIdentifier}`, fetcher);
};

export const GetShortenedUrlInfoFetch = async (
  uniqueIdentifier: string,
  userId: number,
): Promise<GetShortenedUrlInfoResponse> => {
  const serverUrl = process.env.NEXT_PUBLIC_EXTERNAL_SERVER_URL;
  return fetch(`${serverUrl}/shortened-urls/${uniqueIdentifier}`, {
    headers: { ...buildUserIdHeader(userId) },
  })
    .then(async (res) => {
      if (res.ok) {
        const json = (await res.json()) as GetShortenedUrlInfoResponse;
        return json;
      }
      if (res.status == 404) {
        return null;
      }
    })
    .catch(() => null);
};

export const GetAvailableUrlsPath = () => {
  const serverUrl = process.env.NEXT_PUBLIC_EXTERNAL_SERVER_URL;
  return `${serverUrl}/shortened-urls`;
};
export const GetAvailableUrlsSWR = (
  page: number,
  size: number,
  order: string,
  userId: number,
) => {
  const fetcher = (url) =>
    fetch(url, { headers: { ...buildUserIdHeader(userId) } }).then(
      async (res) => {
        if (res.ok) {
          const json = (await res.json()) as GetAvailableUrlsResponse;
          return json;
        }
        if (res.status == 400) {
          const json = (await res.json()) as ErrorResponse;
          console.error(`Bad Request from BE: ${json}`);
          const error = new Error("Unexpected BE error!");
          throw error;
        }
        console.error("Unexpected BE Response!");
        const error = new Error("Unexpected BE response!");
        throw error;
      },
    );
  return useSWR(
    `${GetAvailableUrlsPath()}?page=${page}&size=${size}&order=${order}`,
    fetcher,
  );
};

export const GetShortenedUrlHistorySWR = (
  size: number,
  uniqueIdentifier: string,
  userId: number,
  fromDateTime: string,
) => {
  const serverUrl = process.env.NEXT_PUBLIC_EXTERNAL_SERVER_URL;
  const fetcher = (url) =>
    fetch(url, { headers: { ...buildUserIdHeader(userId) } }).then(
      async (res) => {
        if (res.ok) {
          return (await res.json()) as GetShortenedUrlHistoryResponse;
        }
        if (res.status == 404) {
          throw new GetShortenedUrlHistory404Response();
        }
        console.error("Unexpected BE response!");
        return null;
      },
    );
  return useSWRInfinite((page, prevPageData) => {
    if (prevPageData != null && prevPageData.data.length < size) {
      return null;
    }
    const offset = page * size;
    return `${serverUrl}/shortened-urls/${uniqueIdentifier}/history?size=${size}&offset=${offset}&from=${fromDateTime}`;
  }, fetcher);
};

export class GetShortenedUrlInfoResponse {
  unique_identifier: string;
  shortened_url: string;
  url: string;
  title: string;
  is_enabled: boolean;
  created_at: string;
  updated_at: string;
}

export class GetShortenedUrlInfo404Response {}

export type GetShortenedUrlHistoryRowResponse = {
  type: string
  url?: string;
  title?: string;
  event_date_time: string;
};

export class GetShortenedUrlHistoryResponse {
  data: GetShortenedUrlHistoryRowResponse[];

  constructor() {
    this.data = [];
  }
}

export class GetShortenedUrlHistory404Response {}

export class GetAvailableUrlsResponseData {
  url: string;
  shortened_url: string;
  created_at: string;
  access_count: number;
  is_enabled: boolean;
  title?: string;
}

export class GetAvailableUrlsResponse {
  data: GetAvailableUrlsResponseData[];
  total: number;

  constructor(data, total) {
    this.data = data;
    this.total = total;
  }
}
