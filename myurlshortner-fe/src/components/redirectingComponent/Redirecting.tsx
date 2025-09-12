import { getOriginalUrl } from "app/api/UrlApi";
import { notFound, redirect } from "next/navigation";
import Grid from "@mui/material/Grid";

export default async function Redirecting(props: { uniqueId: string }) {
  const originalUrl = await getOriginalUrl(props.uniqueId);

  if (originalUrl != null) {
    return (
      <Grid
        container
        direction="row"
        sx={{ justifyContent: "center", alignItems: "center", height: "500px" }}
      >
        <Grid size={2}>
          <div>
            <p>Redirecting you to {originalUrl.original_url}</p>
            {redirect(originalUrl.original_url)}
          </div>
        </Grid>
      </Grid>
    );
  } else {
    notFound();
  }
}
