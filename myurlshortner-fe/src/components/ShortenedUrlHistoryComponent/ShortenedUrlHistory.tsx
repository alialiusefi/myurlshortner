"use client";
import {
  GetShortenedUrlHistorySWR,
  GetShortenedUrlHistory404Response,
} from "app/api/UrlsApi";
import { Grid, Paper, Typography } from "@mui/material";
import { Virtuoso } from "react-virtuoso";
import Card from "@mui/material/Card";
import { sleep } from "app/lib/Utility";
import { redirect } from "next/navigation";
import { readableTimestamp } from "components/ReadableTimestampComponent/ReadableTimestamp";
import NewTabLink from "components/NewTabLinkComponent/NewTabLink";
import { useContext } from "react";
import { UserProvider } from "app/context";

export default function ShortenedUrlHistory(params: {
  uniqueId: string;
  now: string;
}) {
  const userId = useContext(UserProvider);
  const { data, isLoading, setSize, error } = GetShortenedUrlHistorySWR(
    5,
    params.uniqueId,
    userId,
    params.now,
  );
  if (isLoading || data == null) {
    return <></>;
  }
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
          {
            <Virtuoso
              style={{ minHeight: "200px" }}
              endReached={async () => {
                await sleep(500);
                setSize((a) => a + 1);
              }}
              data={result}
              itemContent={(index, comp) => {
                return (
                  <Grid container direction="column">
                    <Card variant="outlined">
                      <Grid>
                        <Typography gutterBottom sx={{ fontSize: 14, p: 2 }}>
                          {readableTimestamp(comp.event_date_time)}
                        </Typography>
                      </Grid>
                      {comp.url != null ? (
                        <Grid>
                          <Typography sx={{ p: 2 }}>
                            Target URL: {<NewTabLink url={comp.url} />}
                          </Typography>
                        </Grid>
                      ) : (
                        <></>
                      )}
                      {comp.title != null ? (
                        <Grid>
                          <Typography sx={{ p: 2 }}>
                            Title: {comp.title}
                          </Typography>
                        </Grid>
                      ) : (
                        <></>
                      )}
                    </Card>
                  </Grid>
                );
              }}
            />
          }
        </Grid>
      </Grid>
    </Paper>
  );
}
