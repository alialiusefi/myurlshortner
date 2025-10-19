"use client";
import { Grid, Paper, Typography, Card } from "@mui/material";
import Button from "@mui/material/Button";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import { redirect } from "next/navigation";
import ShortenedUrlHistory from "components/ShortenedUrlHistoryComponent/ShortenedUrlHistory";
import { GetShortenedUrlInfoSWR } from "app/api/UrlsApi";
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import DoNotDisturbOnIcon from '@mui/icons-material/DoNotDisturbOn';
import NewTabLink from "components/NewTabLinkComponent/NewTabLink";
import ReadableTimestamp from "components/ReadableTimestampComponent/ReadableTimestamp";

export default function ShortenedUrlInfo(params: {
  uniqueId: string;
  now: string;
}) {
  const { data, isLoading } = GetShortenedUrlInfoSWR(params.uniqueId)
  if (isLoading) {
    return null
  }
  return (
    <Grid
      container
      sx={{ justifyContent: "center", p: 4 }}
      rowSpacing={3}
      direction="column"
    >
      <Grid>
        <Button
          variant="contained"
          startIcon={<ArrowBackIcon />}
          onClick={() => redirect("/browse")}
        >
          Back
        </Button>
      </Grid>
      <Grid>
        <Paper>
          <Card>
            <Typography variant="h3" sx={{ p: 2 }}>Info</Typography>
            <Grid container spacing={1}>
              <Typography variant="h5" paddingLeft={2}>Unique Identifier:</Typography>
              <Typography variant="h5" paddingLeft={2} fontFamily="monospaced">
                {params.uniqueId}
              </Typography>
            </Grid>
            <Grid container spacing={1} padding={2}>
              <Typography variant="h5">
                <NewTabLink url={data?.shortened_url} />
              </Typography>
              {data?.is_enabled ? (<ArrowForwardIcon fontSize="large" color={"success"} />) : ((<DoNotDisturbOnIcon fontSize="large" color={"error"} />))}
              <Grid size={5}>
                <Typography variant="h5">
                  <NewTabLink url={data?.url} />
                </Typography>
              </Grid>
            </Grid>
            <Grid container>
              <Typography gutterBottom paddingLeft={2} sx={{ color: 'text.secondary', fontSize: 14 }}>
                Created At: {<ReadableTimestamp datetime={data?.created_at}/>}
              </Typography>
              <Typography gutterBottom paddingLeft={2} sx={{ color: 'text.secondary', fontSize: 14 }}>
                Last Updated At: {<ReadableTimestamp datetime={data?.updated_at}/>}
              </Typography>
            </Grid>
          </Card>
        </Paper>
      </Grid>
      {/* <Grid container>
        
      </Grid> */}
      <Grid>
        <ShortenedUrlHistory uniqueId={params.uniqueId} now={params.now} />
      </Grid>
    </Grid>
  );
}
