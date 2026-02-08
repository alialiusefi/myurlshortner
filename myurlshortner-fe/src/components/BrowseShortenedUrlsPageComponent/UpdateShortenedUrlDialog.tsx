import {
  Button,
  Dialog,
  FormControlLabel,
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
  close: () => void;
  onApply: () => void;
};

export default function UpdateShortenedUrlDialog(props: Properties) {
  const userId = useContext(UserProvider);
  const [titleInput, setTitleInput] = useState<string>(props.title);
  const [newTargetUrl, setNewTargetUrl] = useState<string>(props.originalUrl);
  const [isEnabled, setIsEnabled] = useState<boolean>(props.isEnabled);
  const [error, setError] = useState<ErrorResponse>(null);
  const isTitleValid = () => titleInput == null || titleInput?.length < 100;
  const isTargetUrlValid = () => newTargetUrl.match(TARGET_URL_REGEX) != null;
  const isEdited = () =>
    props.isEnabled !== isEnabled || props.originalUrl !== newTargetUrl;
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
      setError(null);
      props.onApply();
    }
  };
  return (
    <Dialog open={props.isOpen} onClose={props.close}>
      <Grid
        padding={3}
        spacing={2}
        container
        flexDirection={"column"}
        minWidth={400}
      >
        <Typography
          data-testid="title-text"
          id="modal-modal-title"
          variant="h6"
          component="h2"
          paddingBottom={2}
        >
          Update Shortened URL:
        </Typography>
        <TextField
          fullWidth
          label="Title"
          onChange={(e) => {
            setTitleInput(e.target.value);
          }}
          value={titleInput ?? ""}
          error={!isTitleValid()}
          helperText={!isTitleValid() ? TITLE_ERROR_MESSAGE : ""}
        />
        <TextField
          label="Target URL"
          fullWidth
          value={newTargetUrl}
          onChange={(e) => setNewTargetUrl(e.target.value)}
          error={!isTargetUrlValid()}
        />
        <FormControlLabel
          label="Enabled"
          control={
            <Switch
              checked={isEnabled}
              onChange={(e) => setIsEnabled(e.target.checked)}
            />
          }
        />
        <Grid container spacing={2} paddingTop={1} justifyContent={"center"}>
          <Button
            variant="contained"
            onClick={handleApply}
            disabled={!isTargetUrlValid() || !isTitleValid() || !isEdited()}
          >
            Apply
          </Button>
          <Button
            variant="outlined"
            onClick={() => {
              props.close();
            }}
          >
            Cancel
          </Button>
        </Grid>
        {apiErrorSnackBar(error)}
      </Grid>
    </Dialog>
  );
}
