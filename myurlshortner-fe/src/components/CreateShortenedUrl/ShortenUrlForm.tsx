"use client";
import {
  shortenUrlOperaton,
  ShortenUrlResponse,
  GenerateUniqueIdSWR,
  GenerateUniqueIdFetch,
} from "../../app/api/UrlShortnerApi";
import { ErrorResponse } from "../../app/api/Errors";
import Button from "@mui/material/Button";
import { useContext, useState } from "react";
import Dialog from "@mui/material/Dialog";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import Link from "@mui/material/Link";
import Grid from "@mui/material/Grid";
import { apiErrorSnackBar } from "components/Utility/ApiErrorSnackBar";
import Tabs from "@mui/material/Tabs";
import Tab from "@mui/material/Tab";
import Paper from "@mui/material/Paper";
import Grow from "@mui/material/Grow";
import { TextField } from "@mui/material";
import { GetShortenedUrlInfoFetch } from "app/api/UrlsApi";
import RefreshIcon from "@mui/icons-material/Refresh";
import { UserProvider } from "app/context";
import {
  TARGET_URL_REGEX,
  UID_INPUT_REGEX,
  TITLE_ERROR_MESSAGE,
} from "app/lib/Constants";

const AUTO_TYPE_VALUE = 0;
const CUSTOM_TYPE_VALUE = 1;

export default function ShortenUrlForm() {
  const [buttonIsLoadingState, setLoadingButtonState] = useState(false);
  const [targetUrlInput, setTargetUrlInput] = useState("");
  const [selectedMode, setSelectedMode] = useState(AUTO_TYPE_VALUE);
  const { data } = GenerateUniqueIdSWR();
  const [uidInput, setUidInput] = useState(null);
  const [shortenedUrlState, setShortenedUrlState] = useState({
    errorResponse: null,
    shortenedUrl: "",
  });
  const [titleInput, setTitleInput] = useState<string>(null);
  const [openModalUrlState, setOpenModalUrlState] = useState(false);
  const [uidExists, setUidExists] = useState(false);
  const userId = useContext(UserProvider);
  const validateUidExists = async (uidInput: string) => {
    const uid = uidInput == null ? data?.unique_identifier : uidInput;
    const res = await GetShortenedUrlInfoFetch(uid, userId);
    setUidExists(res != null);
  };

  const handleUniqueIdChange = async (uniqueId: string) => {
    setUidInput(uniqueId);
    validateUidExists(uniqueId);
  };
  const titleIsValid = () => titleInput == null || titleInput.length < 100;
  const validateForm = (targetUrl: string, uid?: string): boolean => {
    const urlInputValidation = targetUrl.match(TARGET_URL_REGEX) != null;
    const uidInputValidation = uid?.match(UID_INPUT_REGEX) != null;
    return urlInputValidation && uidInputValidation && titleIsValid();
  };

  const handleSubmit = async () => {
    setLoadingButtonState(true);
    const result = await shortenUrlOperaton(
      targetUrlInput,
      uidInput == null ? data?.unique_identifier : uidInput,
      userId,
      titleInput == null ? titleInput : titleInput.trim(),
    );
    if (result instanceof ShortenUrlResponse) {
      const shortenedUrl = (result as ShortenUrlResponse).shortened_url;
      setShortenedUrlState({ errorResponse: null, shortenedUrl: shortenedUrl });
      if (selectedMode == CUSTOM_TYPE_VALUE) {
        setUidExists(true);
      }
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
                target="_blank"
                rel="noopener noreferrer"
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
    <Paper sx={{ minWidth: 400, minHeight: 200 }}>
      <Grid container direction="column" rowSpacing={2} padding={2}>
        <Tabs
          sx={{ paddingBottom: 2 }}
          data-testid="tabs-selection"
          value={selectedMode}
          onChange={async (e, value) => {
            setSelectedMode(value);
            handleUniqueIdChange((await GenerateUniqueIdFetch()).unique_identifier);
          }}
        >
          <Tab
            data-testid="tabs-selection-0"
            label="Auto"
            tabIndex={AUTO_TYPE_VALUE}
          />
          <Tab
            data-testid="tabs-selection-1"
            label="Custom"
            tabIndex={CUSTOM_TYPE_VALUE}
          />
        </Tabs>
        <Grow
          in={selectedMode == CUSTOM_TYPE_VALUE}
          hidden={selectedMode == AUTO_TYPE_VALUE}
        >
          <Box>
            <TextField
              value={titleInput ?? ""}
              sx={{ paddingBottom: 2 }}
              onChange={(e) => setTitleInput(e.target.value)}
              label={"Title"}
              error={!titleIsValid()}
              helperText={
                !titleIsValid()
                  ? TITLE_ERROR_MESSAGE
                  : "Short text that describes the shortened url"
              }
              placeholder="Your Title"
              fullWidth
            />
            <Paper sx={{ padding: 1 }} variant="outlined">
              <Typography>Shortened URL:</Typography>
              <Grid container direction="row" rowSpacing={3} columnSpacing={1}>
                <Typography alignContent={"center"}>
                  {`${process.env.NEXT_PUBLIC_EXTERNAL_CLIENT_URL}/`}
                </Typography>
                <TextField
                  type="text"
                  size="small"
                  label="Unique ID"
                  value={
                    (uidInput == null ? data?.unique_identifier : uidInput) ??
                    ""
                  }
                  onChange={(e) => handleUniqueIdChange(e.target.value)}
                  placeholder="fancyid"
                  slotProps={{
                    htmlInput: { "data-testid": "unique-id-input" },
                  }}
                  helperText={uidExists ? "The unique id already exists." : ""}
                  error={uidExists}
                  required
                />
                <Button
                  data-testid="refresh-button"
                  onClick={async () =>
                    handleUniqueIdChange(
                      (await GenerateUniqueIdFetch()).unique_identifier,
                    )
                  }
                >
                  <RefreshIcon />
                </Button>
              </Grid>
            </Paper>
          </Box>
        </Grow>
        <TextField
          type="url"
          value={targetUrlInput}
          onChange={(e) => setTargetUrlInput(e.target.value)}
          placeholder="https://www.example.com"
          slotProps={{ htmlInput: { "data-testid": "url-input" } }}
          label="Target URL"
          required
          fullWidth
        />
        <Button
          data-testid="shorten-button-input"
          onClick={handleSubmit}
          loading={buttonIsLoadingState}
          disabled={
            !validateForm(
              targetUrlInput,
              uidInput == null ? data?.unique_identifier : uidInput,
            ) || uidExists
          }
        >
          Shorten!
        </Button>
        {successDialog()}
        {apiErrorSnackBar(shortenedUrlState.errorResponse)}
      </Grid>
    </Paper>
  );
}
