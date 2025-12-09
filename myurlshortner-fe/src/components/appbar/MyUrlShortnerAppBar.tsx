"use client";
import AppBar from "@mui/material/AppBar";
import Typography from "@mui/material/Typography";
import Toolbar from "@mui/material/Toolbar";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import { redirect } from "next/navigation";
import { useContext, useEffect, useState } from "react";
import { UserProvider } from "app/context";
import Grid from "@mui/material/Grid";
import { IconButton } from "@mui/material";
import NotificationsIcon from "@mui/icons-material/Notifications";
import { NotificationsPopper } from "components/NotificationsPoperComponent/NotificationPoper";

export default function MyUrlShorterAppBar() {
  const [openNotifications, setOpenNotifications] = useState(false);
  const [anchorElement, setAnchorElement] = useState<HTMLButtonElement>();
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
          <IconButton
            size="small"
            color="inherit"
            onClick={(event) => {
              setOpenNotifications((a) => !a);
              setAnchorElement(event.currentTarget);
            }}
          >
            {/* <Badge></Badge> */}
            <NotificationsIcon />
          </IconButton>
          <NotificationsPopper
            open={openNotifications}
            anchorElem={anchorElement}
            close={() => setOpenNotifications(false)}
          />
          <Grid container columnSpacing={1}>
            <Typography>UserId: {userId}</Typography>
          </Grid>
        </Toolbar>
      </AppBar>
    </Box>
  );
}
