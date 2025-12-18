import { createTheme } from '@mui/material/styles';

const theme = createTheme({
    colorSchemes: {
        dark: true,
    },
    palette: {
        primary: {
            main: '#497e3a',
            contrastText: '#fff'
        },
    },
});

export default theme;
