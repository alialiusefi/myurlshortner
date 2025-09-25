import Snackbar from "@mui/material/Snackbar";
import { ErrorResponse } from "app/api/Errors";

export const apiErrorSnackBar = (errorResponse: ErrorResponse) => {
  const error = errorResponse?.errors[0];
  return (
    <Snackbar
      message={error?.message}
      key={error?.code}
      open={errorResponse != null}
      anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
    />
  );
};
