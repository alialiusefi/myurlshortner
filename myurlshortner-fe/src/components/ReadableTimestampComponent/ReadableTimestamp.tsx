import ZonedDateTimeFormatter from "ts-time-format/ZonedDateTimeFormatter";
import { LOCAL_ZONE_ID } from "ts-time/Zone";
import ZonedDateTime from "ts-time/ZonedDateTime";

export default function ReadableTimestamp(params: { datetime: string }) {
  return ZonedDateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss").format(
    ZonedDateTime.parse(params.datetime).instant.atZone(LOCAL_ZONE_ID),
  );
}
