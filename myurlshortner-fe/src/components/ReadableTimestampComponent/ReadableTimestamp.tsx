import ZonedDateTimeFormatter from "ts-time-format/ZonedDateTimeFormatter";
import { LOCAL_ZONE_ID } from "ts-time/Zone";
import ZonedDateTime from "ts-time/ZonedDateTime";

export function readableTimestamp(datetime?: string) {
  if (datetime == null) {
    return "";
  } else {
    return ZonedDateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss").format(
      ZonedDateTime.parse(datetime).instant.atZone(LOCAL_ZONE_ID),
    );
  }
}
