import ShortenUrlForm from "components/CreateShortenedUrl/ShortenUrlForm";
import Grid from "@mui/material/Grid";

export default function Page() {
  return (
    <Grid
      container
      direction="column"
      sx={{ justifyContent: "center", alignItems: "center", height: "500px" }}
    >
      <ShortenUrlForm />
    </Grid>
  );
}
