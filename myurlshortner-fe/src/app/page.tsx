"use client";
import ShortenUrlForm from "components/CreateShortenedUrl/ShortenUrlForm";
import Grid from "@mui/material/Grid";
import { Suspense } from "react";
import { Typography } from "@mui/material";

export default function Page() {
  return (
    <Grid
      container
      direction="column"
      sx={{ justifyContent: "center", alignItems: "center", height: "500px" }}
    >
      <Suspense fallback={<Typography>Loading...</Typography>}>
        <ShortenUrlForm />
      </Suspense>
    </Grid>
  );
}
