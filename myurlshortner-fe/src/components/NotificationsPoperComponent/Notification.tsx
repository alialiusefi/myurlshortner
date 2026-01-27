export enum NotificationType {
  SHORTENED_URL_REACHED_N_VIEWS = "SHORTENED_URL_REACHED_N_VIEWS",
  GIFT_REQUEST_TO_TARGET_USER = "GIFT_REQUEST_TO_TARGET_USER",
  GIFT_REQUEST_RESPONSE_TO_SOURCE_USER = "GIFT_REQUEST_RESPONSE_TO_SOURCE_USER"
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
  implements ShortenedUrlNotification {
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
  implements ShortenedUrlNotification {
  id: number;
  type: NotificationType;
  params: GiftRequestToTargetUserParams;
  read_at: string | null;
}

export enum GiftRequestResponseToSourceUserParamsType {
  ACCEPTED = 'ACCEPTED',
  DECLINED = 'DECLINED',
  EXPIRED = 'EXPIRED'
}
export type GiftRequestResponseToSourceUserParams = {
  gift_request_id: string,
  target_user_id: number,
  unique_identifier: string,
  type: GiftRequestResponseToSourceUserParamsType
}
export class GiftRequestResponseToSourceUserNotification implements ShortenedUrlNotification {
  id: number;
  type: NotificationType;
  params: GiftRequestResponseToSourceUserParams;
  read_at: string | null;
}
