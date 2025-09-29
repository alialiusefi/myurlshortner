export async function getOriginalUrl(
  uniqueId: string,
): Promise<GetOriginalUrlResponse | null> {
  const serverUrl = process.env.INTERNAL_SERVER_URL;
  return fetch(`${serverUrl}/urls/${uniqueId}`, {
    redirect: "manual",
  })
    .then((response) => {
      if (response.status == 404) {
        return null;
      }
      if (response.status == 307) {
        const originalUrl = response.headers.get("Location");
        return new GetOriginalUrlResponse(originalUrl);
      }
      console.error(`Unexpected response code from BE: ${response.status}`);
    })
    .catch((e) => {
      console.error(`Uncaught exception: ${e}`);
      return null;
    });
}

export class GetOriginalUrlResponse {
  original_url: string;

  constructor(original_url: string) {
    this.original_url = original_url;
  }
}
