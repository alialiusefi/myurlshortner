import { Button } from "@mui/material";
import CardGiftcardIcon from "@mui/icons-material/CardGiftcard";
import CancelScheduleSendIcon from "@mui/icons-material/CancelScheduleSend";
import { CancelAwaitingGiftRequestFetch } from "app/api/GiftRequestApi";
import { useContext } from "react";
import { UserProvider } from "app/context";

interface GiftShortenedUrlButtonParams {
  giftRequestId?: string;
  giftRequestUpdatedAt?: string;
  uniqueIdentifier: string;
  openDialog: () => void;
  mutateAwaitingGiftRequest: () => void;
}

export function GiftShortenedUrlButton(params: GiftShortenedUrlButtonParams) {
  const userId = useContext(UserProvider);
  if (params.giftRequestId == null) {
    return (
      <Button
        variant="contained"
        startIcon={<CardGiftcardIcon />}
        onClick={() => params.openDialog()}
      >
        Gift
      </Button>
    );
  } else {
    return (
      <Button
        variant="outlined"
        color="error"
        disabled={false}
        startIcon={<CancelScheduleSendIcon />}
        onClick={async () => {
          await CancelAwaitingGiftRequestFetch(
            params.giftRequestId,
            params.giftRequestUpdatedAt,
            userId,
          );
          params.mutateAwaitingGiftRequest();
        }}
      >
        Cancel Gift Request
      </Button>
    );
  }
}
