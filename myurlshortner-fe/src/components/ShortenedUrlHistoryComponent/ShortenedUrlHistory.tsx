"use client";
import { useState } from "react";
import { GetShortenedUrlHistorySWR } from "app/api/UrlsApi";
import ZonedDateTime from "ts-time/ZonedDateTime";
import { Grid, Paper, Typography } from "@mui/material";
import { Virtuoso } from "react-virtuoso";
import ZonedDateTimeFormatter from "ts-time-format/ZonedDateTimeFormatter";
import { LOCAL_ZONE_ID } from "ts-time/Zone";
import Link from "@mui/material/Link";
import Button from "@mui/material/Button";
import Card from "@mui/material/Card";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import { redirect } from "next/navigation";
import { sleep } from "app/lib/Utility";

export default function ShortenedUrlHistory(params: {
  uniqueId: string;
  now: string;
}) {
  const { data, isLoading, setSize } = GetShortenedUrlHistorySWR(
    5,
    params.uniqueId,
    params.now,
  );
  const result = data?.map((res) => res?.data).flat();
  return (
    <Grid
      container
      sx={{ justifyContent: "center", p: 4 }}
      rowSpacing={3}
      direction="column"
    >
      <Grid>
        <Button
          variant="contained"
          startIcon={<ArrowBackIcon />}
          onClick={() => redirect("/browse")}
        >
          Back
        </Button>
      </Grid>
      <Grid>
        <Paper>
          <Grid container minHeight={200} direction="column">
            <Grid>
              <Typography variant="h3" sx={{ p: 2 }}>
                History
              </Typography>
            </Grid>
            <Grid sx={{ p: 2, minHeight: "200px" }}>
              {isLoading ? null : (
                <Virtuoso
                  style={{ minHeight: "200px" }}
                  endReached={async () => {
                    await sleep(500);
                    setSize((a) => a + 1);
                  }}
                  data={result}
                  itemContent={(index, comp) => {
                    if (comp != null) {
                      return (
                        <Grid container direction="column">
                          <Card variant="outlined">
                            <Grid>
                              <Typography
                                gutterBottom
                                sx={{ fontSize: 14, p: 2 }}
                              >
                                {ZonedDateTimeFormatter.ofPattern(
                                  "YYYY-MM-dd HH:mm:ss",
                                ).format(
                                  ZonedDateTime.parse(
                                    comp.event_date_time,
                                  ).instant.atZone(LOCAL_ZONE_ID),
                                )}
                              </Typography>
                            </Grid>
                            <Grid>
                              <Typography sx={{ p: 2 }}>
                                Target URL:{" "}
                                {
                                  <Link
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    href={comp.url}
                                    underline="none"
                                  >
                                    {comp.url}
                                  </Link>
                                }
                              </Typography>
                            </Grid>
                          </Card>
                        </Grid>
                      );
                    } else {
                      return null;
                    }
                  }}
                />
              )}
            </Grid>
          </Grid>
        </Paper>
      </Grid>
    </Grid>
  );
}
