export enum NotificationType {
  SHORTENED_URL_REACHED_N_VIEWS,
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
  read_at: string | null
}

export interface ShortenedUrlNotification {
  id: number;
  type: NotificationType;
  read_at: string | null;
}
