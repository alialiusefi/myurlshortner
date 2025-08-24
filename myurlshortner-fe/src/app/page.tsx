import ShortenUrlForm from "components/generateShortenedUrlForm/ShortenUrlForm";
import Grid from '@mui/material/Grid';

export default function Page() {
  return (
    <Grid container direction="row" sx={{justifyContent: 'center', alignItems: 'center', height: '500px'}}>
      <Grid size={2}>
        <ShortenUrlForm/>
      </Grid>
    </Grid>
  )
}
