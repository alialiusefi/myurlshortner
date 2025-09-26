"use client";
import {
  shortenUrlOperaton,
  ShortenUrlResponse,
} from "../../app/api/UrlShortnerApi";
import { ErrorResponse } from "../../app/api/Errors";
import Button from "@mui/material/Button";
import Input from "@mui/material/Input";
import InputLabel from "@mui/material/InputLabel";
import FormGroup from "@mui/material/FormGroup";
import FormControl from "@mui/material/FormControl";
import { useState } from "react";
import Dialog from "@mui/material/Dialog";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import Link from "@mui/material/Link";
import Grid from "@mui/material/Grid";
import { apiErrorSnackBar } from "components/utility/ApiErrorSnackBar";

export default function ShortenUrlForm() {
  const [buttonIsLoadingState, setLoadingButtonState] = useState(false);
  const [buttonIsActiveState, setActiveButtonState] = useState(false);
  const [urlInputState, setUrlInputState] = useState("");
  const [shortenedUrlState, setShortenedUrlState] = useState({
    errorResponse: null,
    shortenedUrl: "",
  });
  const [openModalUrlState, setOpenModalUrlState] = useState(false);
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const input = e.target.value;
    const result =
      input.match(
        /(http(s)?:\/\/.)?(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&=]*)/g,
      ) != null;
    setUrlInputState(input);
    setActiveButtonState(result);
  };

  const handleSubmit = async () => {
    setLoadingButtonState(true);
    const result = await shortenUrlOperaton(urlInputState);
    if (result instanceof ShortenUrlResponse) {
      const shortenedUrl = (result as ShortenUrlResponse).shortened_url;
      setShortenedUrlState({ errorResponse: null, shortenedUrl: shortenedUrl });
      setOpenModalUrlState(true);
    } else {
      const error = result as ErrorResponse;
      setShortenedUrlState({ errorResponse: error, shortenedUrl: null });
    }
    setLoadingButtonState(false);
  };

  const successDialog = () => {
    const style = {
      left: "400",
      top: "200",
      position: "fixed",
    };
    return (
      <Dialog
        data-testid="success-dialog"
        sx={style}
        open={openModalUrlState}
        onClose={() => setOpenModalUrlState(false)}
      >
        <Grid container sx={{ justifyContent: "center", alignItems: "center" }}>
          <Box padding={2}>
            <Typography
              data-testid="title-text"
              id="modal-modal-title"
              variant="h6"
              component="h2"
            >
              Success!
            </Typography>
            <Typography id="modal-modal-description">
              URL:
              <Link
                data-testid="shortened-url-link"
                sx={{ padding: 0.5 }}
                href={shortenedUrlState.shortenedUrl}
                underline="hover"
              >
                {shortenedUrlState.shortenedUrl}
              </Link>
            </Typography>
          </Box>
        </Grid>
      </Dialog>
    );
  };

  return (
    <div>
      <FormGroup>
        <FormControl>
          <InputLabel>URL</InputLabel>
          <Input
            type="url"
            value={urlInputState}
            onChange={handleInputChange}
            placeholder="https://www.example.com"
            data-testid="url-input"
            required
          />
          <Button
            data-testid="shorten-button-input"
            onClick={handleSubmit}
            loading={buttonIsLoadingState}
            disabled={!buttonIsActiveState}
          >
            Shorten!
          </Button>
          {successDialog()}
          {apiErrorSnackBar(shortenedUrlState.errorResponse)}
        </FormControl>
      </FormGroup>
    </div>
  );
}
