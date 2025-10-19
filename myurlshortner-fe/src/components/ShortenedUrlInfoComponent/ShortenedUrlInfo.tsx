"use client";
import { Grid } from "@mui/material";
import Button from "@mui/material/Button";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import { redirect } from "next/navigation";
import ShortenedUrlHistory from "components/ShortenedUrlHistoryComponent/ShortenedUrlHistory";

export default function ShortenedUrlInfo(params: {
  uniqueId: string;
  now: string;
}) {
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
        <ShortenedUrlHistory uniqueId={params.uniqueId} now={params.now} />
      </Grid>
    </Grid>
  );
}
