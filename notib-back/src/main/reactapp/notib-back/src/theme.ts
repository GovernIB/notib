import { createTheme } from '@mui/material/styles';

const theme = createTheme({
    colorSchemes: {
        dark: true,
    },
    palette: {
        primary: {
            main: '#ff9523',
            contrastText: '#fff'
        },
    },
});

export default theme;
