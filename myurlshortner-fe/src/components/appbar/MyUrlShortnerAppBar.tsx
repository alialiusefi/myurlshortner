"use client";
import AppBar from "@mui/material/AppBar";
import Typography from "@mui/material/Typography";
import Toolbar from "@mui/material/Toolbar";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import { redirect } from "next/navigation";
import { useContext, useState } from "react";
import { UserProvider } from "app/context";
import Grid from "@mui/material/Grid";
import { IconButton, Badge } from "@mui/material";
import NotificationsIcon from "@mui/icons-material/Notifications";
import { NotificationsPopper } from "components/NotificationsPoperComponent/NotificationPoper";
import { GetNotificationsSWR } from "app/api/NotificationApi";

export default function MyUrlShorterAppBar() {
  const [openNotifications, setOpenNotifications] = useState(false);
  const [anchorElement, setAnchorElement] = useState<HTMLButtonElement>();
  const userId = useContext(UserProvider);
  const { data, isLoading, mutate } = GetNotificationsSWR(userId);

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
            <Badge
              badgeContent={
                data?.data.filter((it) => it.read_at == null).length
              }
            >
              <NotificationsIcon />
            </Badge>
          </IconButton>
          <NotificationsPopper
            notifications={isLoading || data == null ? [] : data.data}
            open={openNotifications}
            anchorElem={anchorElement}
            close={() => setOpenNotifications(false)}
            mutate={mutate}
          />
          <Grid container columnSpacing={1}>
            <Typography>UserId: {userId}</Typography>
          </Grid>
        </Toolbar>
      </AppBar>
    </Box>
  );
}
