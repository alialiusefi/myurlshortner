"use client";
import {
  GetShortenedUrlHistorySWR,
  GetShortenedUrlHistoryRowResponse,
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
import { buildBrowsePagePath, EMPTY_VALUE } from "app/lib/Constants";

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
    redirect(buildBrowsePagePath());
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
                switch (comp.type) {
                  case "USER_UPDATED_TITLE":
                    return <TitleUpdatedRow row={comp} />;
                  case "USER_UPDATED_ORIGINAL_URL":
                    return <TargetUrlUpdatedRow row={comp} />;
                  case "USER_CREATED_SHORTENED_URL":
                    return <ShortenedUrlCreatedRow row={comp} />;
                }
              }}
            />
          }
        </Grid>
      </Grid>
    </Paper>
  );
}

const TitleUpdatedRow = (params: {
  row: GetShortenedUrlHistoryRowResponse;
}) => {
  return (
    <Card variant="outlined">
      <Grid>
        <Typography gutterBottom sx={{ fontSize: 14, p: 2 }}>
          Title was updated - {readableTimestamp(params.row.event_date_time)}
        </Typography>
      </Grid>
      <Grid>
        <Typography sx={{ p: 2 }}>
          Title:{" "}
          {params.row.title == null || params.row.title.length == 0
            ? EMPTY_VALUE
            : params.row.title}
        </Typography>
      </Grid>
    </Card>
  );
};

const ShortenedUrlCreatedRow = (params: {
  row: GetShortenedUrlHistoryRowResponse;
}) => {
  return (
    <Card variant="outlined">
      <Grid>
        <Typography gutterBottom sx={{ fontSize: 14, p: 2 }}>
          Created - {readableTimestamp(params.row.event_date_time)}
        </Typography>
      </Grid>
      <Grid>
        <Typography sx={{ p: 2 }}>
          Target URL: {<NewTabLink url={params.row.url} />}
        </Typography>
      </Grid>
      <Grid>
        <Typography sx={{ p: 2 }}>
          Title:{" "}
          {params.row.title == null || params.row.title.length == 0
            ? EMPTY_VALUE
            : params.row.title}
        </Typography>
      </Grid>
    </Card>
  );
};

const TargetUrlUpdatedRow = (params: {
  row: GetShortenedUrlHistoryRowResponse;
}) => {
  return (
    <Card variant="outlined">
      <Grid>
        <Typography gutterBottom sx={{ fontSize: 14, p: 2 }}>
          Target Url was updated -{" "}
          {readableTimestamp(params.row.event_date_time)}
        </Typography>
      </Grid>
      <Grid>
        <Typography sx={{ p: 2 }}>
          Target URL: {<NewTabLink url={params.row.url} />}
        </Typography>
      </Grid>
    </Card>
  );
};
