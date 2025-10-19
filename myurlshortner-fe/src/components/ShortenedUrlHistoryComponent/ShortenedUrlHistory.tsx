"use client";
import {
  GetShortenedUrlHistorySWR,
  GetShortenedUrlHistory404Response,
} from "app/api/UrlsApi";
import ZonedDateTime from "ts-time/ZonedDateTime";
import { Grid, Paper, Typography } from "@mui/material";
import { Virtuoso } from "react-virtuoso";
import ZonedDateTimeFormatter from "ts-time-format/ZonedDateTimeFormatter";
import { LOCAL_ZONE_ID } from "ts-time/Zone";
import Link from "@mui/material/Link";
import Card from "@mui/material/Card";
import { sleep } from "app/lib/Utility";
import { redirect } from "next/navigation";

export default function ShortenedUrlHistory(params: {
  uniqueId: string;
  now: string;
}) {
  const { data, isLoading, setSize, error } = GetShortenedUrlHistorySWR(
    5,
    params.uniqueId,
    params.now,
  );
  if (error instanceof GetShortenedUrlHistory404Response) {
    redirect("/browse");
  }
  const result = data?.map((res) => res?.data).flat();
  return (
    <Paper>
      <Grid container minHeight={200} direction="column">
        <Grid>
          <Typography variant="h3" sx={{ p: 2 }}>
            History
          </Typography>
        </Grid>
        <Grid sx={{ p: 2, minHeight: "200px" }}>
          {isLoading ? null : (
            <Virtuoso
              style={{ minHeight: "200px" }}
              endReached={async () => {
                await sleep(500);
                setSize((a) => a + 1);
              }}
              data={result}
              itemContent={(index, comp) => {
                if (comp != null) {
                  return (
                    <Grid container direction="column">
                      <Card variant="outlined">
                        <Grid>
                          <Typography gutterBottom sx={{ fontSize: 14, p: 2 }}>
                            {ZonedDateTimeFormatter.ofPattern(
                              "YYYY-MM-dd HH:mm:ss",
                            ).format(
                              ZonedDateTime.parse(
                                comp.event_date_time,
                              ).instant.atZone(LOCAL_ZONE_ID),
                            )}
                          </Typography>
                        </Grid>
                        <Grid>
                          <Typography sx={{ p: 2 }}>
                            Target URL:{" "}
                            {
                              <Link
                                target="_blank"
                                rel="noopener noreferrer"
                                href={comp.url}
                                underline="none"
                              >
                                {comp.url}
                              </Link>
                            }
                          </Typography>
                        </Grid>
                      </Card>
                    </Grid>
                  );
                } else {
                  return null;
                }
              }}
            />
          )}
        </Grid>
      </Grid>
    </Paper>
  );
}
