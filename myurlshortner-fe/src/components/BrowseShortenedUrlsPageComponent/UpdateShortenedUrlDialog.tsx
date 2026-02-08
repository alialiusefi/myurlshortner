import {
  Button,
  Dialog,
  FormControl,
  FormControlLabel,
  FormGroup,
  Switch,
  TextField,
} from "@mui/material";
import Typography from "@mui/material/Typography";
import Grid from "@mui/material/Grid";
import { useContext, useState } from "react";
import { updateShortenedUrl } from "app/api/UrlShortnerApi";
import { apiErrorSnackBar } from "../Utility/ApiErrorSnackBar";
import { ErrorResponse } from "app/api/Errors";
import { UserProvider } from "app/context";
import { TARGET_URL_REGEX, TITLE_ERROR_MESSAGE } from "app/lib/Constants";

type Properties = {
  isOpen: boolean;
  uniqueIdentifier: string;
  originalUrl: string;
  isEnabled: boolean;
  title?: string;
  onClose: () => void;
};

export default function UpdateShortenedUrlDialog(props: Properties) {
  const userId = useContext(UserProvider);
  const [titleInput, setTitleInput] = useState<string>(props.title);
  const [newTargetUrl, setNewTargetUrl] = useState<string>(props.originalUrl);
  const isTitleValid = () => titleInput == null || titleInput?.length < 100;
  const isTargetUrlValid = () => newTargetUrl.match(TARGET_URL_REGEX) != null;
  const [isOpen, setIsOpen] = useState(props.isOpen);
  const [isEnabled, setIsEnabled] = useState<boolean>(props.isEnabled);
  const onCloseCallback = props.onClose;
  const [error, setError] = useState<ErrorResponse>(null);
  const handleApply = async () => {
    const response = await updateShortenedUrl(
      props.uniqueIdentifier,
      userId,
      props.originalUrl !== newTargetUrl ? newTargetUrl : undefined,
      props.isEnabled !== isEnabled ? isEnabled : undefined,
      props.title !== titleInput ? titleInput : undefined,
    );
    if (response instanceof ErrorResponse) {
      setError(response);
    } else {
      setIsOpen(false);
      setError(null);
      onCloseCallback();
    }
  };
  return (
    <Dialog
      open={isOpen}
      onClose={() => {
        setIsOpen(false);
        onCloseCallback();
      }}
    >
      <Grid padding={2} container>
        <FormGroup sx={{ width: 400 }}>
          <Grid>
            <Typography
              data-testid="title-text"
              id="modal-modal-title"
              variant="h6"
              component="h2"
              paddingBottom={2}
            >
              Update Shortened URL:
            </Typography>
          </Grid>
          <Grid padding={2}>
            <TextField
              fullWidth
              label="Title"
              onChange={(e) => {
                setTitleInput(e.target.value);
              }}
              value={titleInput ?? ""}
              error={!isTitleValid()}
              helperText={!isTitleValid() ? TITLE_ERROR_MESSAGE : ""
              }
            />
          </Grid>
          <Grid padding={2}>
            <FormControl fullWidth>
              <TextField
                label="Target URL"
                fullWidth
                value={newTargetUrl}
                onChange={(e) => setNewTargetUrl(e.target.value)}
                error={!isTargetUrlValid()}
              />
            </FormControl>
          </Grid>
          <Grid container spacing={2} padding={1}>
            <FormControl>
              <Button
                variant="contained"
                onClick={handleApply}
                disabled={!isTargetUrlValid() || !isTitleValid()}
              >
                Apply
              </Button>
            </FormControl>
            <FormControl>
              <Button
                variant="outlined"
                onClick={() => {
                  setIsOpen(false);
                  onCloseCallback();
                }}
              >
                Cancel
              </Button>
            </FormControl>
          </Grid>
          <Grid padding={2}>
            <FormControlLabel
              label="Enabled"
              control={
                <Switch
                  checked={isEnabled}
                  onChange={(e) => setIsEnabled(e.target.checked)}
                />
              }
            />
          </Grid>
          <Grid>{apiErrorSnackBar(error)}</Grid>
        </FormGroup>
      </Grid>
    </Dialog>
  );
}
