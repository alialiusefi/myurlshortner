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
import { useState } from "react";
import { updateShortenedUrl } from "app/api/UrlShortnerApi";
import { apiErrorSnackBar } from "../utility/ApiErrorSnackBar";
import { ErrorResponse } from "app/api/Errors";

type Properties = {
  isOpen: boolean;
  uniqueIdentifier: string;
  originalUrl: string;
  isEnabled: boolean;
  onClose: () => void;
};

export default function UpdateShortenedUrlDialog(props: Properties) {
  const [newTargetUrl, setNewTargetUrl] = useState<string>(props.originalUrl);
  const [isOpen, setIsOpen] = useState(props.isOpen);
  const [isValid, setIsValid] = useState(true);
  const [isEnabled, setIsEnabled] = useState<boolean>(props.isEnabled);
  const onCloseCallback = props.onClose;
  const [error, setError] = useState<ErrorResponse>(null);
  const handleApply = async () => {
    const response = await updateShortenedUrl(
      props.uniqueIdentifier,
      newTargetUrl,
      isEnabled,
    );
    if (response) {
      setError(response);
    } else {
      setIsOpen(false);
      setError(null);
      onCloseCallback();
    }
  };
  const handleTargetUrlChange = (input) => {
    const result =
      input.match(
        /(http(s)?:\/\/.)?(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&=]*)/g,
      ) != null;
    setNewTargetUrl(input);
    setIsValid(result);
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
            >
              Update Shortened URL:
            </Typography>
          </Grid>
          <Grid padding={2}>
            <FormControl fullWidth>
              <TextField
                label="Target URL"
                fullWidth
                value={newTargetUrl}
                onChange={(e) => {
                  handleTargetUrlChange(e.target.value);
                }}
                error={!isValid}
              />
            </FormControl>
          </Grid>
          <Grid container spacing={2} padding={1}>
            <FormControl>
              <Button
                variant="contained"
                onClick={handleApply}
                disabled={!isValid}
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
