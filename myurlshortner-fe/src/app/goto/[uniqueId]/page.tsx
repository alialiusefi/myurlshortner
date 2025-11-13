import Redirecting from "components/redirectingComponent/Redirecting";
import { getOriginalUrl } from "app/api/UrlApi";
import { headers } from 'next/headers';

export default async function HandleRedirect({
  params,
}: {
  params: Promise<{ uniqueId: string }>;
}) {
  const { uniqueId } = await params;
  const headerList = await headers();
  const response = await getOriginalUrl(uniqueId, headerList.get('User-Agent'));

  return <Redirecting original_url={response?.original_url}></Redirecting>;
}
