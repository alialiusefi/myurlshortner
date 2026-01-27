"use client";
import * as React from "react";
import { AppRouterCacheProvider } from "@mui/material-nextjs/v15-appRouter";
import CssBaseline from "@mui/material/CssBaseline";
import { ThemeProvider } from "@mui/material/styles";
import theme from "./ui/theme";
import "./ui/global.css";
import MyUrlShorterAppBar from "components/AppBarComponent/AppBar";
import { UserProvider } from "./context";

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const [userId, setUserId] = React.useState<number>();
  React.useEffect(() => {
    const cookieValue =
      document.cookie
        .split("; ")
        .find((a) => a.startsWith("User-Id"))
        ?.split("=")[1] ?? "1";
    setUserId(parseInt(cookieValue));
  }, []);
  return (
    <html lang="en">
      <body>
        <AppRouterCacheProvider options={{ enableCssLayer: true }}>
          <UserProvider value={userId}>
            <ThemeProvider theme={theme}>
              <MyUrlShorterAppBar />
              {children}
            </ThemeProvider>
          </UserProvider>
          <CssBaseline />
        </AppRouterCacheProvider>
      </body>
    </html>
  );
}
