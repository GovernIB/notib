import { createTheme } from '@mui/material/styles';
import backgroundPattern from './assets/background-pattern.png';

const theme = createTheme({
    palette: {
        primary: {
            main: '#ff9523',
            contrastText: '#fff',
        },
    },
    components: {
        MuiCssBaseline: {
            styleOverrides: {
                body: {
                    backgroundImage: `url(${backgroundPattern})`,
                    color: '#666666',
                },
            },
        },
    },
});

export default theme;
