"use client";
import { notFound, redirect } from "next/navigation";
import Grid from "@mui/material/Grid";

export default function Redirecting(props: { original_url: string | null }) {
  if (props.original_url != null) {
    return (
      <Grid
        container
        direction="row"
        sx={{ justifyContent: "center", alignItems: "center", height: "500px" }}
      >
        <Grid size={2}>
          <div>
            <p>Redirecting you to {props.original_url}</p>
            {redirect(props.original_url)}
          </div>
        </Grid>
      </Grid>
    );
  } else {
    notFound();
  }
}
