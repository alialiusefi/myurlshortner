export enum NotificationType {
  SHORTENED_URL_REACHED_N_VIEWS = "SHORTENED_URL_REACHED_N_VIEWS",
  GIFT_REQUEST_TO_TARGET_USER = "GIFT_REQUEST_TO_TARGET_USER",
}

export interface ShortenedUrlNotification {
  id: number;
  type: NotificationType;
  read_at: string | null;
}

export type ShortenedUrlReachedNViewsParams = {
  unique_identifier: string;
  views: number;
};
export class ShortenedUrlReachedNViewsNotification
  implements ShortenedUrlNotification
{
  id: number;
  type: NotificationType;
  params: ShortenedUrlReachedNViewsParams;
  read_at: string | null;
}

export type GiftRequestToTargetUserParams = {
  unique_identifier: string;
  gift_request_id: number;
  source_user_id: number;
};
export class GiftRequestToTargetUserNotification
  implements ShortenedUrlNotification
{
  id: number;
  type: NotificationType;
  params: GiftRequestToTargetUserParams;
  read_at: string | null;
}
