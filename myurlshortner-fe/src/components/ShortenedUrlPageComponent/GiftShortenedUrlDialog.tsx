import {
  Dialog,
  Box,
  Grid,
  Typography,
  FormControl,
  TextField,
  Button,
} from "@mui/material";
import SendIcon from "@mui/icons-material/Send";
import { useContext, useState } from "react";
import { CreateAwaitingGiftRequestFetch } from "app/api/GiftRequestApi";
import { UserProvider } from "app/context";

interface GiftShortenedURLModalParams {
  uniqueIdentifier: string;
  isOpen: boolean;
  close: () => void;
  mutateAwaitingGiftRequest: () => void;
}

export function GiftShortenedURLDialog(params: GiftShortenedURLModalParams) {
  const userId = useContext(UserProvider);
  const [targetUserId, setTargetUserId] = useState<string>("");
  const validUserId = (str) =>
    str.match(/^\d+$/) != null &&
    Number.parseInt(str) !== userId &&
    Number.parseInt(str) !== 0;
  return (
    <Dialog open={params.isOpen}>
      <Box padding={2}>
        <Grid container direction="column" rowGap={1}>
          <Typography>
            To whom would you like to gift this shortened url?
          </Typography>
          <FormControl fullWidth>
            <TextField
              label="User ID"
              fullWidth
              value={targetUserId}
              onChange={(e) => {
                setTargetUserId(e.target.value);
              }}
              error={!validUserId(targetUserId) && targetUserId !== ""}
            />
          </FormControl>
          <Grid container direction="row" columnGap={1} justifyContent="center">
            <Button
              variant="contained"
              startIcon={<SendIcon />}
              onClick={async () => {
                await CreateAwaitingGiftRequestFetch(
                  params.uniqueIdentifier,
                  Number.parseInt(targetUserId),
                  userId,
                );
                params.mutateAwaitingGiftRequest();
                params.close();
              }}
              disabled={!validUserId(targetUserId)}
            >
              Send Gift Request
            </Button>
            <Button
              variant="contained"
              onClick={() => {
                params.close();
              }}
            >
              Cancel
            </Button>
          </Grid>
        </Grid>
      </Box>
    </Dialog>
  );
}
