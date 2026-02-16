import Box from "@mui/material/Box";
import ShortnetedUrlsTable from "components/BrowseShortenedUrlsPageComponent/ShortenedUrlsTable";

export default async function Browse() {
  return (
    <Box
      sx={{ p: 3 }}
    >
      <ShortnetedUrlsTable />
    </Box>
  );
}
