import { Typography, Link } from "@mui/material";
import { ReactElement } from "react";
import {
  ShortenedUrlReachedNViewsNotification,
  ShortenedUrlNotification,
} from "./Notification";

interface ShortenedUrlNotificationComponentFactory {
  getTitle(notification: ShortenedUrlNotification): ReactElement;
  getDescription(notification: ShortenedUrlNotification): ReactElement;
}

// todo: extend implementation to support different types.
class ShortenedUrlReachedNViewNotificationFactory
  implements ShortenedUrlNotificationComponentFactory
{
  getTitle = (
    notification: ShortenedUrlReachedNViewsNotification,
  ): ReactElement => {
    return (
      <Typography
        variant="body1"
        color="textPrimary"
        fontWeight={notification.read_at == null ? "bold" : "regular"}
      >
        Congrats! Your shortened url reached {notification.params.views} views!
      </Typography>
    );
  };

  getDescription = (
    notification: ShortenedUrlReachedNViewsNotification,
  ): ReactElement => {
    const pathToInfoPage = `/browse/${notification.params.unique_identifier}/info`;
    return (
      <Typography variant="caption" color="textSecondary">
        Your shortened url with id{" "}
        {
          <Link href={pathToInfoPage}>
            {notification.params.unique_identifier}
          </Link>
        }{" "}
        has reached {notification.params.views} views!
      </Typography>
    );
  };
}

export const shortenedUrlReachedNViewNotificationFactory =
  new ShortenedUrlReachedNViewNotificationFactory();
