import Box from "@mui/material/Box";
import ShortnetedUrlsTable from "components/BrowseShortenedUrlsPageComponent/ShortenedUrlsTable";
import { Suspense } from "react";

export default async function Browse() {
  return (
    <Box sx={{ p: 3 }}>
      <Suspense>
        <ShortnetedUrlsTable />
      </Suspense>
    </Box>
  );
}
