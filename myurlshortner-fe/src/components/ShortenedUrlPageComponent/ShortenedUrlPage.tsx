"use client";
import { Grid, Paper } from "@mui/material";
import Button from "@mui/material/Button";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import { redirect } from "next/navigation";
import ShortenedUrlHistory from "components/ShortenedUrlHistoryComponent/ShortenedUrlHistory";
import ShortenedUrlInfoCard from "components/ShortenedUrlInfoCardComponent/ShortenedUrlInfoCard";
import { useContext, useState } from "react";
import { GiftShortenedURLDialog } from "./GiftShortenedUrlDialog";
import { GiftShortenedUrlButton } from "./GiftShortenedUrlButton";
import { GetAwaitingGiftRequestSWR } from "app/api/GiftRequestApi";
import { UserProvider } from "app/context";
import { buildBrowsePagePath } from "app/lib/Constants";

export default function ShortenedUrlPage(params: {
  uniqueId: string;
  now: string;
}) {
  const userId = useContext(UserProvider);
  const [openGiftShortenedURLDialog, setOpenGiftShortenedURLDialog] =
    useState<boolean>(false);
  const { data, mutate } = GetAwaitingGiftRequestSWR(params.uniqueId, userId);
  return (
    <Grid
      container
      sx={{ justifyContent: "center", p: 4 }}
      rowSpacing={3}
      direction="column"
    >
      <Grid container direction="row" columnSpacing={2}>
        <Button
          variant="contained"
          startIcon={<ArrowBackIcon />}
          onClick={() => redirect(buildBrowsePagePath())}
        >
          Back
        </Button>
        <GiftShortenedUrlButton
          mutateAwaitingGiftRequest={() => mutate()}
          giftRequestId={data?.id}
          giftRequestUpdatedAt={data?.updated_at}
          uniqueIdentifier={params.uniqueId}
          openDialog={() => setOpenGiftShortenedURLDialog((e) => !e)}
        />
        <GiftShortenedURLDialog
          mutateAwaitingGiftRequest={() => mutate()}
          isOpen={openGiftShortenedURLDialog}
          uniqueIdentifier={params.uniqueId}
          close={() => {
            setOpenGiftShortenedURLDialog((e) => !e);
          }}
        />
      </Grid>
      <Grid>
        <Paper>
          <ShortenedUrlInfoCard uniqueId={params.uniqueId} />
        </Paper>
      </Grid>
      <Grid>
        <ShortenedUrlHistory uniqueId={params.uniqueId} now={params.now} />
      </Grid>
    </Grid>
  );
}
