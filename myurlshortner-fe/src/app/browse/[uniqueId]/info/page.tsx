import ShortenedUrlHistory from "components/ShortenedUrlHistoryComponent/ShortenedUrlHistory";
import Instant from "ts-time/Instant";
import ZonedDateTime from "ts-time/ZonedDateTime";
import { LOCAL_ZONE_ID } from "ts-time/Zone";

export default async function Info({
  params,
}: {
  params: Promise<{ uniqueId: string }>;
}) {
  const { uniqueId } = await params;
  const instant = Instant.now();
  const now = ZonedDateTime.ofInstant(
    instant,
    LOCAL_ZONE_ID.offsetAtInstant(instant),
  ).toString();
  return <ShortenedUrlHistory uniqueId={uniqueId} now={now} />;
}
