import useSWR from "swr";
import { ErrorResponse } from "./Errors";

export const GetAvailableUrlsSWR = (
  page: number,
  size: number,
  order: string,
  refreshInterval: number = 0,
) => {
  const serverUrl = process.env.NEXT_PUBLIC_EXTERNAL_SERVER_URL;
  const fetcher = (url) =>
    fetch(url).then(async (res) => {
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
    });
  return useSWR(
    `${serverUrl}/shortened-urls?page=${page}&size=${size}&order=${order}`,
    fetcher,
    { refreshInterval: refreshInterval },
  );
};

export class GetAvailableUrlsResponse {
  data: [
    {
      url: string;
      shortened_url: string;
      created_at: string;
      access_count: number;
    },
  ];
  total: number;

  constructor(data, total) {
    this.data = data;
    this.total = total;
  }
}
