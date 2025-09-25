"use client";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import Table from "@mui/material/Table";
import TableSortLabel from "@mui/material/TableSortLabel";
import { useState } from "react";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import TableBody from "@mui/material/TableBody";
import TablePagination from "@mui/material/TablePagination";
import { GetAvailableUrlsSWR } from "../../app/api/UrlsApi";
import TableContainer from "@mui/material/TableContainer";
import ZonedDateTimeFormatter from "ts-time-format/ZonedDateTimeFormatter";
import Link from "@mui/material/Link";
import { LOCAL_ZONE_ID } from "ts-time/Zone";
import ZonedDateTime from "ts-time/ZonedDateTime";
import { redirect, RedirectType, useSearchParams } from "next/navigation";
import Button from "@mui/material/Button";
import IconButton from "@mui/material/IconButton";
import EditIcon from "@mui/icons-material/Edit";
import UpdateShortenedUrlDialog from "../UpdateShortenedUrlDialog/UpdateShortenedUrlDialog";

export default function ShortnetedUrlsTable() {
  type Direction = "asc" | "desc";
  const getIntParam = (
    key: string,
    validator: (string: string) => Boolean,
    def: number,
  ) => {
    const value = useSearchParams().get(key);
    if (value && validator(value)) {
      return parseInt(value);
    } else {
      return def;
    }
  };
  const getOrderParam = () => {
    const value = useSearchParams().get("order");
    if (value == "desc" || value == "asc") {
      return value;
    } else {
      return "desc";
    }
  };
  const sizeParam = getIntParam(
    "size",
    (string: string) => {
      return parseInt(string) > 0 && parseInt(string) <= 101;
    },
    10,
  );
  const pageParam = getIntParam(
    "page",
    (string: string) => {
      return parseInt(string) > 0;
    },
    1,
  );
  const orderParam = getOrderParam();
  const [directonState, setDirectionState] = useState<Direction>(orderParam);
  const [size, setSizeState] = useState(sizeParam);
  const [page, setPageState] = useState(pageParam - 1);
  const { data, mutate } = GetAvailableUrlsSWR(page + 1, size, directonState);
  const toggleDirection = () => {
    if (directonState == "desc") {
      setDirectionState("asc");
    } else {
      setDirectionState("desc");
    }
  };
  const [currentSelectedForEdit, setCurrentSelectedForEdit] = useState(null);
  return (
    <Box>
      <Typography>Browse Shortened Urls:</Typography>
      <TableContainer>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Shortened URL</TableCell>
              <TableCell>Access Count</TableCell>
              <TableCell>Original URL</TableCell>
              <TableCell>
                Created At
                <TableSortLabel
                  active={true}
                  direction={directonState}
                  onClick={toggleDirection}
                />
              </TableCell>
              <TableCell></TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {data?.data.map((one) => (
              <TableRow key={one.shortened_url}>
                <TableCell>
                  <Link href={one.shortened_url} underline="none">
                    {one.shortened_url}
                  </Link>
                </TableCell>
                <TableCell>{one.access_count}</TableCell>
                <TableCell>
                  <OriginalUrl url={one.url} />
                </TableCell>
                <TableCell>
                  {ZonedDateTimeFormatter.ofPattern(
                    "YYYY-MM-dd HH:mm:ss",
                  ).format(
                    ZonedDateTime.parse(one.created_at).instant.atZone(
                      LOCAL_ZONE_ID,
                    ),
                  )}
                </TableCell>
                <TableCell>
                  <IconButton
                    onClick={() => {
                      setCurrentSelectedForEdit(one.shortened_url);
                    }}
                  >
                    <EditIcon />
                    {currentSelectedForEdit === one.shortened_url ? (
                      <UpdateShortenedUrlDialog
                        isOpen={currentSelectedForEdit === one.shortened_url}
                        uniqueIdentifier={one.shortened_url.substring(
                          one.shortened_url.indexOf("/goto/") + 6,
                        )}
                        originalUrl={one.url}
                        onClose={() => {
                          setCurrentSelectedForEdit(null);
                          mutate({ ...data });
                        }}
                      />
                    ) : (
                      <></>
                    )}
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
        <TablePagination
          rowsPerPageOptions={[5, 10, 15]}
          count={data?.total}
          rowsPerPage={size}
          page={page}
          onPageChange={(event, page) => {
            setPageState(page);
          }}
          onRowsPerPageChange={(event) => {
            setSizeState(parseInt(event.target.value));
          }}
        />
      </TableContainer>
    </Box>
  );
}

function OriginalUrl(props: { url: string }) {
  const truncate = (url: string, truncate: boolean) => {
    if (url.length > 90 && truncate) {
      return `${url.substring(0, 90)}...`;
    } else {
      return url;
    }
  };
  const [doTruncateState, doTruncate] = useState<boolean>(true);
  const button = () => {
    if (props.url.length > 90) {
      return (
        <Button
          size="small"
          onClick={() => doTruncate(!doTruncateState)}
          variant="outlined"
        >
          {doTruncateState ? "Extend" : "Hide"}
        </Button>
      );
    }
  };
  return (
    <Box>
      <Link sx={{ p: 2 }} href={props.url} underline="none">
        {truncate(props.url, doTruncateState)}
      </Link>
      {button()}
    </Box>
  );
}
