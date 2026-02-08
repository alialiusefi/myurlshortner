"use client";
import { Grid, Typography, Card } from "@mui/material";
import { redirect } from "next/navigation";
import {
  GetShortenedUrlInfo404Response,
  GetShortenedUrlInfoSWR,
} from "app/api/UrlsApi";
import ArrowForwardIcon from "@mui/icons-material/ArrowForward";
import DoNotDisturbOnIcon from "@mui/icons-material/DoNotDisturbOn";
import NewTabLink from "components/NewTabLinkComponent/NewTabLink";
import { readableTimestamp } from "components/ReadableTimestampComponent/ReadableTimestamp";
import { useContext } from "react";
import { UserProvider } from "app/context";
import { buildBrowsePagePath } from "app/lib/Constants";

export default function ShortenedUrlInfoCard(params: { uniqueId: string }) {
  const userId = useContext(UserProvider);
  const { data, isLoading, error } = GetShortenedUrlInfoSWR(
    params.uniqueId,
    userId,
  );
  if (isLoading || data == null) {
    return <></>;
  }
  if (error instanceof GetShortenedUrlInfo404Response) {
    redirect(buildBrowsePagePath());
  }
  return (
    <Card>
      <Typography variant="h3" sx={{ p: 2 }}>
        Info
      </Typography>
      <Grid container spacing={1}>
        <Typography variant="h5" paddingLeft={2}>
          Unique Identifier:
        </Typography>
        <Typography variant="h5" paddingLeft={2} fontFamily="monospaced">
          {params.uniqueId}
        </Typography>
      </Grid>
      <Grid>
        <Typography variant="h5" paddingLeft={2}>
          Title: {data.title == null || data.title.length == 0 ? ('<empty>') : (data.title)}
        </Typography>
      </Grid>
      <Grid container spacing={1} padding={2}>
        <Typography variant="h5">
          <NewTabLink url={data.shortened_url} />
        </Typography>
        {data.is_enabled ? (
          <ArrowForwardIcon fontSize="large" color={"success"} />
        ) : (
          <DoNotDisturbOnIcon fontSize="large" color={"error"} />
        )}
        <Grid size={5}>
          <Typography variant="h5">
            <NewTabLink url={data.url} />
          </Typography>
        </Grid>
      </Grid>
      <Grid container>
        <Typography
          gutterBottom
          paddingLeft={2}
          sx={{ color: "text.secondary", fontSize: 14 }}
        >
          Created At: {readableTimestamp(data?.created_at)}
        </Typography>
        <Typography
          gutterBottom
          paddingLeft={2}
          sx={{ color: "text.secondary", fontSize: 14 }}
        >
          Last Updated At: {readableTimestamp(data?.updated_at)}
        </Typography>
      </Grid>
    </Card>
  );
}
