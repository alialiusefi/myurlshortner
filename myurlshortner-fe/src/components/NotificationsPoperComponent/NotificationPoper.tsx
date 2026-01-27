import Popper from "@mui/material/Popper";
import { delegator } from "components/NotificationsPoperComponent/NotificationFactory";
import {
  ListItem,
  Paper,
  List,
  ListItemText,
  Typography,
  Grid,
  Button,
} from "@mui/material";
import {
  ShortenedUrlNotification,
  NotificationType,
  GiftRequestToTargetUserNotification,
} from "./Notification";
import { ReadNotification } from "app/api/NotificationApi";
import { useContext, useState } from "react";
import { UserProvider } from "app/context";
import GiftRequestToTargetUserActionModal from "../NotificationsPoperComponent/GiftRequestToTargetUserActionModal";
export type NotificationsPopperProps = {
  notifications: ShortenedUrlNotification[];
  open: boolean;
  anchorElem?: HTMLButtonElement;
  close: () => void;
  mutate: () => any;
};

export function NotificationsPopper(props: NotificationsPopperProps) {
  const userId = useContext(UserProvider);
  const [openActionDialog, setOpenActionDialog] = useState(false);
  const listComponent = () => {
    if (props.notifications.length == 0) {
      return (
        <Typography variant="caption" color="textSecondary" sx={{ p: 1 }}>
          You have no notifications.
        </Typography>
      );
    } else {
      return (
        <List sx={{ overflow: "auto", maxHeight: 300 }}>
          {props.notifications.map((not: ShortenedUrlNotification) => {
            return (
              <ListItem key={not.id}>
                <Paper sx={{ p: 1 }}>
                  <Grid container direction="row">
                    <ListItemText
                      key={not.id}
                      primary={delegator.getTitle(not)}
                      secondary={delegator.getDescription(not)}
                    />
                    <Button
                      disabled={not.read_at !== null}
                      onClick={async () => {
                        await ReadNotification(userId, not.id);
                        props.mutate();
                      }}
                    >
                      Mark as read
                    </Button>
                    {not.type ===
                    NotificationType.GIFT_REQUEST_TO_TARGET_USER ? (
                      <div>
                        <Button onClick={() => setOpenActionDialog((e) => !e)}>
                          Action
                        </Button>
                        <GiftRequestToTargetUserActionModal
                          isOpen={openActionDialog}
                          onCancel={() => setOpenActionDialog((e) => !e)}
                          onAction={async () => {
                            setOpenActionDialog((e) => !e);
                            props.mutate();
                            props.close();
                          }}
                          notification={
                            not as GiftRequestToTargetUserNotification
                          }
                        />
                      </div>
                    ) : (
                      <></>
                    )}
                  </Grid>
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
