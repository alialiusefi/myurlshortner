import Redirecting from "components/redirectingComponent/Redirecting";

export default async function HandleRedirect({
  params,
}: {
  params: Promise<{ uniqueId: string }>;
}) {
  const { uniqueId } = await params;
  return <Redirecting uniqueId={uniqueId}></Redirecting>;
}
