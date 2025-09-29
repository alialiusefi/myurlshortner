import Redirecting from "components/redirectingComponent/Redirecting";
import { getOriginalUrl } from "app/api/UrlApi";

export default async function HandleRedirect({
  params,
}: {
  params: Promise<{ uniqueId: string }>;
}) {
  const { uniqueId } = await params;
  const response = await getOriginalUrl(uniqueId);

  return <Redirecting original_url={response?.original_url}></Redirecting>;
}
