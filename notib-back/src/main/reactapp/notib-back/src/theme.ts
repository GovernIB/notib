import { createTheme } from '@mui/material/styles';

const theme = createTheme({
    colorSchemes: {
        light: {
            palette: {
                mode: 'light',
                primary: { main: '#497e3a' },
            },
        },
        dark: {
            palette: {
                mode: 'dark',
                primary: { main: '#86e56c' },
            },
        },
    }
});

export default theme;
