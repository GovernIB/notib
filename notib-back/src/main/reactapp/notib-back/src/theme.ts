import { createTheme, alpha } from '@mui/material/styles';
import type {} from '@mui/x-tree-view/themeAugmentation';

// Estenem les interfícies de MUI per admetre el nou color
declare module '@mui/material/styles' {
  interface Palette {
    customBackground: string;
    greyBackground: string;
  }
  interface PaletteOptions {
    customBackground?: string;
    greyBackground?: string;
  }
}

const sharedComponents = {
    MuiTreeItem: {
        styleOverrides: {
            content: ({ theme }: any) => ({
                '&:hover': {
                    backgroundColor: alpha(theme.palette.primary.main, 0.10),
                },
                '&.Mui-selected': {
                    backgroundColor: alpha(theme.palette.primary.main, 0.15),
                    '&:hover': {
                        backgroundColor: alpha(theme.palette.primary.main, 0.20),
                    },
                    '&.Mui-focused': {
                        backgroundColor: alpha(theme.palette.primary.main, 0.25),
                    },
                },
            }),
        },
    },
};

export const lightTheme = createTheme({
    palette: {
        mode: 'light',
        primary: { main: '#497e3a' },
        customBackground: '#f5f5f5',
        greyBackground: '#f5f5f5',
    },
    components: sharedComponents,
});

export const darkTheme = createTheme({
    palette: {
        mode: 'dark',
        primary: { main: '#86e56c' },
        customBackground: '#121212',
        greyBackground: '#222222',
    },
    components: sharedComponents,
});

// Paleta oficial del tema Dràcula (https://draculatheme.com), amb el color de fons
// secundari ("paper") i el gris ("greyBackground") ajustats per separar-se de la resta
// de superfícies i mantenir el contrast WCAG amb el text.
export const draculaTheme = createTheme({
    palette: {
        mode: 'dark',
        primary: { main: '#BD93F9', contrastText: '#282A36' },
        secondary: { main: '#F8F8F2' },
        background: { default: '#282A36', paper: '#303341' },
        text: { primary: '#F8F8F2', secondary: '#D6D6C2' },
        divider: '#7a7d8b',
        error: { main: '#FF5555' },
        warning: { main: '#FFB86C' },
        success: { main: '#50FA7B' },
        info: { main: '#8BE9FD' },
        customBackground: '#282A36',
        greyBackground: '#343746',
    },
    components: {
        ...sharedComponents,
        MuiButton: {
            styleOverrides: {
                root: {
                    variants: [
                        {
                            props: ({ color, variant }: any) =>
                                variant === 'outlined' && color === 'primary',
                            style: {
                                color: '#F8F8F2',
                                borderColor: '#BD93F9',
                            },
                        },
                    ],
                },
            },
        },
    },
});
