import Link from "@mui/material/Link";

export default function NewTabLink(params: { url: string }) {
  return (
    <Link
      target="_blank"
      rel="noopener noreferrer"
      href={params.url}
      underline="none"
    >
      {params.url}
    </Link>
  );
}
