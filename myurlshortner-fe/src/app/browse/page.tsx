import Grid from "@mui/material/Grid";
import ShortnetedUrlsTable from "components/shortenedUrlsTable/ShortenedUrlsTable";
import { Suspense } from "react";

export default async function Browse() {
  return (
    <Grid
      container
      direction="row"
      size={7}
      sx={{ justifyContent: "center", alignItems: "center", p: 3 }}
    >
      <ShortnetedUrlsTable />
    </Grid>
  );
}
