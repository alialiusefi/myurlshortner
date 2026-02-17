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
import { RedirectType, useSearchParams } from "next/navigation";
import Button from "@mui/material/Button";
import IconButton from "@mui/material/IconButton";
import EditIcon from "@mui/icons-material/Edit";
import UpdateShortenedUrlDialog from "./UpdateShortenedUrlDialog";
import CircleIcon from "@mui/icons-material/Circle";
import { redirect } from "next/navigation";
import NewTabLink from "components/NewTabLinkComponent/NewTabLink";
import { readableTimestamp } from "components/ReadableTimestampComponent/ReadableTimestamp";
import { UserProvider } from "app/context";
import { TextField, Paper } from "@mui/material";
import SaveIcon from "@mui/icons-material/Save";
import UndoIcon from "@mui/icons-material/Undo";
import Grid from "@mui/material/Grid";
import { updateShortenedUrl } from "app/api/UrlShortnerApi";
import {
  buildBrowsePagePath,
  buildInfoPagePath,
  EMPTY_VALUE,
  TITLE_ERROR_MESSAGE,
} from "app/lib/Constants";

export default function ShortnetedUrlsTable() {
  type Direction = "asc" | "desc";
  const searchParams = useSearchParams();
  const sizeParam = coaleseIntParam(
    searchParams.get("size"),
    (string: string) => parseInt(string) > 0 && parseInt(string) < 101,
    10,
  );
  const pageParam = coaleseIntParam(
    searchParams.get("page"),
    (string: string) => parseInt(string) > 0,
    1,
  );
  const orderParam = coaleseOrderParam(searchParams.get("order"));
  const titleParam = searchParams.get("title");
  const [directonState, setDirectionState] = useState<Direction>(orderParam);
  const [size, setSizeState] = useState(sizeParam);
  const [page, setPageState] = useState(pageParam - 1);
  const [titleInput, setTitle] = useState<string>(titleParam);
  const [searchTitle, setSearchTitle] = useState<string>(titleInput);
  const [currentSelectedForEdit, setCurrentSelectedForEdit] =
    useState<string>(null);
  const isTitleValid = (titleInput: string) =>
    titleInput == null || titleInput.length < 101;
  const userId = useContext(UserProvider);
  const { data, mutate } = GetAvailableUrlsSWR(
    page + 1,
    size,
    directonState,
    userId,
    searchTitle == null ? undefined : searchTitle,
  );

  return (
    <Grid container direction="column" rowGap={1}>
      <Typography padding={2}>Browse Shortened Urls:</Typography>
      <Grid
        container
        direction="row"
        columnSpacing={2}
        alignItems="center"
        justifyContent="center"
      >
        <Grid size={10}>
          <TextField
            fullWidth
            size="small"
            onChange={(e) => setTitle(e.target.value)}
            placeholder="Search by title"
            value={titleInput ?? ""}
            helperText={isTitleValid(titleInput) ? "" : TITLE_ERROR_MESSAGE}
            error={!isTitleValid(titleInput)}
          />
        </Grid>
        <Grid>
          <Button
            variant="contained"
            disabled={!isTitleValid(titleInput)}
            onClick={() => {
              if (titleInput !== null) {
                setSearchTitle(titleInput);
              } else if (titleInput == null) {
                setSearchTitle("");
              }
            }}
          >
            Search
          </Button>
        </Grid>
        <Grid>
          <Button
            variant="outlined"
            disabled={searchTitle == null}
            onClick={() => {
              setSearchTitle(null);
              setTitle(null);
              redirect(buildBrowsePagePath(), RedirectType.push);
            }}
          >
            Reset
          </Button>
        </Grid>
      </Grid>
      <Paper sx={{ p: 1 }}>
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
                  {searchTitle === null ? (
                    <TableSortLabel
                      active={true}
                      direction={directonState}
                      onClick={() => {
                        if (directonState == "desc") {
                          setDirectionState("asc");
                        } else {
                          setDirectionState("desc");
                        }
                      }}
                    />
                  ) : (
                    <></>
                  )}
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
                    <Typography>
                      {readableTimestamp(one?.created_at)}
                    </Typography>
                  </TableCell>
                  <TableCell>
                    <IconButton
                      onClick={() =>
                        setCurrentSelectedForEdit(one.shortened_url)
                      }
                    >
                      <EditIcon />
                    </IconButton>
                    {currentSelectedForEdit !== null ? (
                      <UpdateShortenedUrlDialog
                        isOpen={currentSelectedForEdit === one.shortened_url}
                        uniqueIdentifier={one.unique_identifier}
                        originalUrl={one.url}
                        isEnabled={one.is_enabled}
                        close={() => {
                          setCurrentSelectedForEdit(null);
                        }}
                        onApply={() => {
                          mutate();
                          setCurrentSelectedForEdit(null);
                        }}
                        title={one.title}
                      />
                    ) : (
                      <></>
                    )}
                  </TableCell>
                  <TableCell>
                    <Button
                      onClick={() => {
                        redirect(buildInfoPagePath(one.unique_identifier));
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
      </Paper>
    </Grid>
  );
}

const coaleseIntParam = (
  value: string,
  validator: (string: string) => boolean,
  def: number,
) => {
  if (value && validator(value)) {
    return parseInt(value);
  } else {
    return def;
  }
};

const coaleseOrderParam = (value: string) => {
  if (value == "desc" || value == "asc") {
    return value;
  } else {
    return "desc";
  }
};

function Title(params: {
  title?: string;
  updateTitle: (newTitle: string) => void;
}) {
  const [title, setTitle] = useState<string>(params.title);
  // the mutation will reload the props with new values however it will not update, since title has its own state already,
  useEffect(() => {
    setTitle(params.title);
  }, [params.title]);
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
        placeholder={EMPTY_VALUE}
        hiddenLabel
        variant="outlined"
        size="small"
        value={title ?? ""}
        error={!isValid(title)}
        helperText={!isValid(title) ? TITLE_ERROR_MESSAGE : ""}
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
