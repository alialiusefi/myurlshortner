import useSWR from "swr";
import { buildUserIdHeader } from "./Utility";
import { ShortenedUrlNotification } from "components/NotificationsPoperComponent/Notification";
import { ErrorResponse } from "./Errors";

type ShortenedUrlNotificationResponse = {
  data: ShortenedUrlNotification[];
};

export const GetNotificationsSWR = (userId: number) => {
  const serverUrl = process.env.NEXT_PUBLIC_EXTERNAL_SERVER_URL;
  const fetcher = (url) =>
    fetch(url, { headers: { ...buildUserIdHeader(userId) } })
      .then(async (res) => {
        if (res.ok) {
          return (await res.json()) as ShortenedUrlNotificationResponse;
        } else {
          console.error("Unexpected BE response!");
          return (await res.json()) as ErrorResponse;
        }
      })
      .then((res) => {
        if (res instanceof ErrorResponse) {
          throw new Error(JSON.stringify(res.errors));
        } else {
          return res;
        }
      });
  return useSWR(`${serverUrl}/notifications`, fetcher);
};
