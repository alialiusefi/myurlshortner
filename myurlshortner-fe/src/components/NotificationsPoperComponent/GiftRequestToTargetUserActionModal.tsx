import { Button, Dialog, Grid, Typography } from "@mui/material";
import { GiftRequestToTargetUserNotification } from "./Notification";
import ShortenedUrlInfoCard from "components/ShortenedUrlInfoCardComponent/ShortenedUrlInfoCard";
import {
  AcceptAwaitingGiftRequestFetch,
  DeclineAwaitingGiftRequestFetch,
} from "app/api/GiftRequestApi";
import { useContext } from "react";
import { UserProvider } from "app/context";
import { ReadNotification } from "app/api/NotificationApi";
import { mutate } from "swr";
import { GetAvailableUrlsPath } from "../../app/api/UrlsApi";

interface GiftRequestToTargetUserActionModalParams {
  isOpen: boolean;
  onAction: () => void;
  onCancel: () => void;
  notification: GiftRequestToTargetUserNotification;
}

export default function GiftRequestToTargetUserActionModal(
  params: GiftRequestToTargetUserActionModalParams,
) {
  const userId = useContext(UserProvider);
  return (
    <Dialog open={params.isOpen} onClose={params.onCancel} maxWidth="xl">
      <Grid
        container
        padding={2}
        direction="column"
        rowGap={2}
        sx={{ minWidth: 1000 }}
      >
        <Typography variant="h5">
          Would you like to accept the following shortened url from user{" "}
          {params.notification.params.source_user_id}?
        </Typography>
        <ShortenedUrlInfoCard
          uniqueId={params.notification.params.unique_identifier}
        />
        <Grid
          container
          direction="row"
          columnGap={2}
          sx={{ justifyContent: "center", alignItems: "center" }}
        >
          <Button
            variant="contained"
            color="success"
            onClick={async () => {
              await ReadNotification(userId, params.notification.id);
              await AcceptAwaitingGiftRequestFetch(
                params.notification.params.gift_request_id,
                userId,
              );
              mutate(
                (key) =>
                  typeof key === "string" &&
                  key.startsWith(GetAvailableUrlsPath()),
              );
              params.onAction();
            }}
          >
            Accept
          </Button>
          <Button
            variant="contained"
            color="error"
            onClick={async () => {
              await ReadNotification(userId, params.notification.id);
              await DeclineAwaitingGiftRequestFetch(
                params.notification.params.gift_request_id,
                userId,
              );
              params.onAction();
            }}
          >
            Decline
          </Button>
          <Button variant="contained" onClick={params.onCancel}>
            Cancel
          </Button>
        </Grid>
      </Grid>
    </Dialog>
  );
}
