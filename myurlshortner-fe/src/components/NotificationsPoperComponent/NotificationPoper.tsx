import Popper from "@mui/material/Popper";
import { shortenedUrlReachedNViewNotificationFactory } from "components/NotificationsPoperComponent/NotificationFactory";
import {
  ListItem,
  Paper,
  List,
  ListItemText,
  Typography,
  Grid,
  Button,
} from "@mui/material";
import { ShortenedUrlReachedNViewsNotification } from "./Notification";
import { GetNotificationsSWR } from "app/api/NotificationApi";
import { useContext } from "react";
import { UserProvider } from "app/context";

export type NotificationsPopperProps = {
  open: boolean;
  anchorElem?: HTMLButtonElement;
  close: () => void;
};

export function NotificationsPopper(props: NotificationsPopperProps) {
  const userId = useContext(UserProvider);
  const { data, isLoading } = GetNotificationsSWR(userId);
  const listComponent = () => {
    if (data == null || data?.data.length == 0) {
      return (
        <Typography variant="caption" color="textSecondary" sx={{ p: 1 }}>
          You have no notifications.
        </Typography>
      );
    } else if (isLoading) {
      return <Typography>Loading</Typography>;
    } else {
      return (
        <List sx={{ overflow: "auto", maxHeight: 300 }}>
          {data.data.map((not: ShortenedUrlReachedNViewsNotification) => {
            return (
              <ListItem key={not.id}>
                <Paper sx={{ p: 1 }}>
                  <ListItemText key={not.id}
                    primary={shortenedUrlReachedNViewNotificationFactory.getTitle(
                      not,
                    )}
                    secondary={shortenedUrlReachedNViewNotificationFactory.getDescription(
                      not,
                    )}
                  />
                </Paper>
              </ListItem>
            );
          })}
        </List>
      );
    }
  };
  return (
    <Popper open={props.open} placement="bottom" anchorEl={props.anchorElem}>
      <Paper>
        <Grid
          container
          sx={{ justifyContent: "center", alignItems: "center" }}
          direction="column"
          width={300}
        >
          <Grid>
            <Typography variant="h6">Notifications</Typography>
          </Grid>
          <Grid container>{listComponent()}</Grid>
          <Grid>
            <Button onClick={() => props.close()}>Close</Button>
          </Grid>
        </Grid>
      </Paper>
    </Popper>
  );
}
