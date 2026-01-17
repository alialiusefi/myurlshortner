import { Typography, Link } from "@mui/material";
import { ReactElement } from "react";
import {
  ShortenedUrlReachedNViewsNotification,
  ShortenedUrlNotification,
  GiftRequestToTargetUserNotification,
  NotificationType,
} from "./Notification";

interface ShortenedUrlNotificationComponentFactory {
  getTitle(notification: ShortenedUrlNotification): ReactElement;
  getDescription(notification: ShortenedUrlNotification): ReactElement;
}

// todo use map
class ShortenedUrlNotificationComponentFactoryDelegator
  implements ShortenedUrlNotificationComponentFactory
{
  getTitle(notification: ShortenedUrlNotification): ReactElement {
    if (notification.type === NotificationType.SHORTENED_URL_REACHED_N_VIEWS) {
      return shortenedUrlReachedNViewNotificationFactory.getTitle(
        notification as ShortenedUrlReachedNViewsNotification,
      );
    }
    if (notification.type === NotificationType.GIFT_REQUEST_TO_TARGET_USER) {
      return giftRequestToTargetUserNotificationFactory.getTitle(
        notification as GiftRequestToTargetUserNotification,
      );
    }
  }

  getDescription(notification: ShortenedUrlNotification): ReactElement {
    if (notification.type === NotificationType.SHORTENED_URL_REACHED_N_VIEWS) {
      return shortenedUrlReachedNViewNotificationFactory.getDescription(
        notification as ShortenedUrlReachedNViewsNotification,
      );
    }
    if (notification.type === NotificationType.GIFT_REQUEST_TO_TARGET_USER) {
      return giftRequestToTargetUserNotificationFactory.getDescription(
        notification as GiftRequestToTargetUserNotification,
      );
    }
  }
}
export const delegator =
  new ShortenedUrlNotificationComponentFactoryDelegator();

class GiftRequestToTargetUserNotificationFactory
  implements ShortenedUrlNotificationComponentFactory
{
  getTitle(notification: GiftRequestToTargetUserNotification): ReactElement {
    return (
      <Typography
        variant="body1"
        color="textPrimary"
        fontWeight={notification.read_at === null ? "bold" : "regular"}
      >
        You have received a shortened url gift request!
      </Typography>
    );
  }

  getDescription(
    notification: GiftRequestToTargetUserNotification,
  ): ReactElement {
    const url = `${process.env.NEXT_PUBLIC_EXTERNAL_CLIENT_URL}/goto/${notification.params.unique_identifier}`;
    return (
      <Typography variant="caption" color="textSecondary">
        The user with id {notification.params.source_user_id} have sent you a
        shortened url with id{" "}
        {
          <Link
            target="_blank"
            rel="noopener noreferrer"
            href={url}
            underline="always"
            title="Go to target url"
          >
            {notification.params.unique_identifier}
          </Link>
        }
        .
      </Typography>
    );
  }
}
export const giftRequestToTargetUserNotificationFactory =
  new GiftRequestToTargetUserNotificationFactory();

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
        fontWeight={notification.read_at === null ? "bold" : "regular"}
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
