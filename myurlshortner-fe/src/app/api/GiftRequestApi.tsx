import useSWR from "swr";
import { buildUserIdHeader } from "./Utility";
import { ErrorResponse } from "./Errors";

type GetAwaitingGiftRequestResponse = {
  id: string;
  updated_at: string;
};

export function GetAwaitingGiftRequestSWR(
  uniqueIdentifier: string,
  userId: number,
) {
  const serverUrl = process.env.NEXT_PUBLIC_EXTERNAL_SERVER_URL;
  const fetcher = (url) =>
    fetch(url, { headers: { ...buildUserIdHeader(userId) } })
      .then(async (res) => {
        if (res.ok) {
          return (await res.json()) as GetAwaitingGiftRequestResponse;
        } else if (res.status == 404) {
          return null;
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
  return useSWR(
    `${serverUrl}/shortened-urls/${uniqueIdentifier}/gift-requests/awaiting`,
    fetcher,
  );
}

export function CreateAwaitingGiftRequestFetch(
  uniqueIdentifier: string,
  targetUserId: number,
  userId: number,
) {
  const serverUrl = process.env.NEXT_PUBLIC_EXTERNAL_SERVER_URL;
  const requestBody = {
    target_user_id: targetUserId,
  };
  return fetch(
    `${serverUrl}/shortened-urls/${uniqueIdentifier}/gift-requests`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        ...buildUserIdHeader(userId),
      },
      body: JSON.stringify(requestBody),
    },
  ).then(async (res) => {
    if (!res.ok) {
      switch (res.status) {
        case 409: {
          const parsed = (await res.json()) as ErrorResponse;
          return parsed.errors.find(
            (a) => a.code === "TARGET_USER_ALREADY_HAS_SUCH_GIFT_REQUEST",
          );
        }
        case 400: {
          const parsed = (await res.json()) as ErrorResponse;
          const error = parsed.errors.find(
            (a) => a.code === "SHORTENED_URL_ALREADY_HAS_A_GIFT_REQUEST",
          );
          if (error != null) {
            return error;
          } else {
            return parsed as ErrorResponse;
          }
        }
        default: {
          console.error("Unexpected BE Error!");
          return res;
        }
      }
    } else {
      return res;
    }
  });
}

export function CancelAwaitingGiftRequestFetch(
  giftRequestId: string,
  giftRequestUpdatedAt: string,
  userId: number,
) {
  const serverUrl = process.env.NEXT_PUBLIC_EXTERNAL_SERVER_URL;
  const requestBody = {
    updated_at: giftRequestUpdatedAt,
  };
  return fetch(`${serverUrl}/gift-requests/awaiting/${giftRequestId}/cancel`, {
    method: "PUT",
    headers: {
      ...buildUserIdHeader(userId),
      "Content-Type": "application/json",
    },
    body: JSON.stringify(requestBody),
  }).then(async (res) => {
    if (res.ok) {
      return res;
    } else {
      const parsed = (await res.json()) as ErrorResponse;
      if (res.status == 409) {
        return parsed.errors.find(
          (it) => it.code === "GIFT_REQUEST_WAS_UPDATED",
        );
      }
      console.error("Unexpected BE response!");
      return parsed;
    }
  });
}

export function AcceptAwaitingGiftRequestFetch(
  giftRequestId: number,
  userId: number,
) {
  const serverUrl = process.env.NEXT_PUBLIC_EXTERNAL_SERVER_URL;
  const requestBody = {
    updated_at: null,
  };
  return fetch(`${serverUrl}/gift-requests/awaiting/${giftRequestId}/accept`, {
    method: "PUT",
    headers: {
      ...buildUserIdHeader(userId),
      "Content-Type": "application/json",
    },
    body: JSON.stringify(requestBody),
  }).then(async (res) => {
    if (res.ok) {
      return res;
    } else {
      const parsed = (await res.json()) as ErrorResponse;
      if (res.status == 409) {
        return parsed.errors.find(
          (it) => it.code === "GIFT_REQUEST_WAS_UPDATED",
        );
      }
      console.error("Unexpected BE response!");
      return parsed;
    }
  });
}

export function DeclineAwaitingGiftRequestFetch(
  giftRequestId: number,
  userId: number,
) {
  const serverUrl = process.env.NEXT_PUBLIC_EXTERNAL_SERVER_URL;
  return fetch(`${serverUrl}/gift-requests/awaiting/${giftRequestId}/decline`, {
    method: "PUT",
    headers: {
      ...buildUserIdHeader(userId),
      "Content-Type": "application/json",
    },
  }).then(async (res) => {
    if (res.ok) {
      return res;
    } else {
      const parsed = (await res.json()) as ErrorResponse;
      if (res.status == 409) {
        return parsed.errors.find(
          (it) => it.code === "GIFT_REQUEST_WAS_UPDATED",
        );
      }
      console.error("Unexpected BE response!");
      return parsed;
    }
  });
}
