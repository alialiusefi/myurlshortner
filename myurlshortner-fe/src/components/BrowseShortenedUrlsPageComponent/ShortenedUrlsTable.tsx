"use client";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import Table from "@mui/material/Table";
import TableSortLabel from "@mui/material/TableSortLabel";
import { useContext, useEffect, useState } from "react";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import TableBody from "@mui/material/TableBody";
import TableFooter from "@mui/material/TableFooter";
import TablePagination from "@mui/material/TablePagination";
import { GetAvailableUrlsSWR } from "../../app/api/UrlsApi";
import TableContainer from "@mui/material/TableContainer";
import Link from "@mui/material/Link";
import { useSearchParams } from "next/navigation";
import Button from "@mui/material/Button";
import IconButton from "@mui/material/IconButton";
import EditIcon from "@mui/icons-material/Edit";
import UpdateShortenedUrlDialog from "./UpdateShortenedUrlDialog";
import CircleIcon from "@mui/icons-material/Circle";
import { redirect } from "next/navigation";
import NewTabLink from "components/NewTabLinkComponent/NewTabLink";
import { readableTimestamp } from "components/ReadableTimestampComponent/ReadableTimestamp";
import { UserProvider } from "app/context";
import { TextField } from "@mui/material";
import SaveIcon from "@mui/icons-material/Save";
import UndoIcon from "@mui/icons-material/Undo";
import Grid from "@mui/material/Grid";
import { updateShortenedUrl } from "app/api/UrlShortnerApi";

export default function ShortnetedUrlsTable() {
  type Direction = "asc" | "desc";
  const searchParams = useSearchParams();
  const getIntParam = (
    key: string,
    validator: (string: string) => boolean,
    def: number,
  ) => {
    const value = searchParams.get(key);
    if (value && validator(value)) {
      return parseInt(value);
    } else {
      return def;
    }
  };
  const getOrderParam = () => {
    const value = searchParams.get("order");
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
  const userId = useContext(UserProvider);
  const { data, mutate } = GetAvailableUrlsSWR(
    page + 1,
    size,
    directonState,
    userId,
  );
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
              <TableCell>Status</TableCell>
              <TableCell>Title</TableCell>
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
              <TableCell></TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {data?.data.map((one, idx) => (
              <TableRow key={one.shortened_url}>
                <TableCell>
                  {one.is_enabled ? (
                    <CircleIcon sx={{ p: 1, fontSize: 30 }} color="success" />
                  ) : (
                    <CircleIcon sx={{ p: 1, fontSize: 30 }} color="error" />
                  )}
                </TableCell>
                <TableCell>
                  <Title
                    title={one.title}
                    updateTitle={async (newTitle: string) => {
                      const updated = await updateShortenedUrl(
                        one.unique_identifier,
                        userId,
                        undefined,
                        undefined,
                        newTitle,
                      );
                      const newOne = {
                        unique_identifier: one.unique_identifier,
                        title: updated.title,
                        created_at: updated.created_at,
                        is_enabled: updated.is_enabled,
                        url: updated.url,
                        shortened_url: updated.shortened_url,
                        access_count: one.access_count,
                      };
                      mutate(
                        () => {
                          return {
                            data: data.data.toSpliced(idx, 1, newOne),
                            total: data.total,
                          };
                        },
                        { revalidate: false },
                      );
                    }}
                  />
                </TableCell>
                <TableCell>
                  <NewTabLink url={one.shortened_url} />
                </TableCell>
                <TableCell>{one.access_count}</TableCell>
                <TableCell>
                  <OriginalUrl url={one.url} />
                </TableCell>
                <TableCell>
                  <Typography>{readableTimestamp(one?.created_at)}</Typography>
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
                        uniqueIdentifier={one.unique_identifier}
                        originalUrl={one.url}
                        isEnabled={one.is_enabled}
                        onClose={() => {
                          setCurrentSelectedForEdit(null);
                          mutate();
                        }}
                        title={one.title}
                      />
                    ) : (
                      <></>
                    )}
                  </IconButton>
                </TableCell>
                <TableCell>
                  <Button
                    onClick={() => {
                      const infoUrl = `/browse/${one.unique_identifier}/info`;
                      redirect(infoUrl);
                    }}
                  >
                    INFO
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
          <TableFooter>
            <TableRow>
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
            </TableRow>
          </TableFooter>
        </Table>
      </TableContainer>
    </Box>
  );
}

function Title(params: {
  title?: string;
  updateTitle: (newTitle: string) => void;
}) {
  const [title, setTitle] = useState<string>(params.title);
  // the mutation will reload the props with new values however it will not update, since title has its own state already,
  useEffect(() => {
    setTitle(params.title)
  }, [params.title])
  const isValid = (input?: string) => {
    return input == null || input?.length < 100;
  };
  const isEdited = (input?: string) => {
    return input !== params.title;
  };
  return (
    <Grid container direction="row">
      <TextField
        sx={{ width: 200 }}
        placeholder="<empty>"
        hiddenLabel
        variant="outlined"
        size="small"
        value={title ?? ""}
        error={!isValid(title)}
        helperText={
          !isValid(title) ? "The provided title exceeds 100 characters" : ""
        }
        onChange={(e) => setTitle(e.target.value)}
      />
      <IconButton
        disabled={!isEdited(title) || !isValid(title)}
        onClick={() => params.updateTitle(title.trim())}
        title="Save"
      >
        <SaveIcon fontSize="small" />
      </IconButton>
      <IconButton
        disabled={!isEdited(title)}
        onClick={() => setTitle(params.title)}
        title="Undo"
      >
        <UndoIcon fontSize="small" />
      </IconButton>
    </Grid>
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
