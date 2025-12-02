"use client";
import AppBar from "@mui/material/AppBar";
import Typography from "@mui/material/Typography";
import Toolbar from "@mui/material/Toolbar";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import { redirect } from "next/navigation";
import { useContext } from "react";
import { UserProvider } from "app/context";
import Grid from "@mui/material/Grid";

export default function MyUrlShorterAppBar() {
  const userId = useContext(UserProvider);
  return (
    <Box sx={{ flexGrow: 1 }}>
      <AppBar position="static">
        <Toolbar sx={{ alignContent: "start" }}>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            MyUrlShortner
          </Typography>
          <Button onClick={() => redirect("/")} color="inherit">
            Home
          </Button>
          <Button onClick={() => redirect("/browse")} color="inherit">
            Browse
          </Button>
          <Grid container columnSpacing={1}>
            <Typography>UserId: {userId}</Typography>
          </Grid>
        </Toolbar>
      </AppBar>
    </Box>
  );
}
