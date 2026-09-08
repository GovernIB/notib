import React from 'react';
import { ThemeProvider } from '@mui/material/styles';
import { CssBaseline, useMediaQuery } from '@mui/material';
import { lightTheme, darkTheme, draculaTheme } from '../theme';
import { useNotibContext } from './NotibContext';

interface ThemePreview {
    tema?: string;
    estilMenu?: string;
}

interface ThemeUserContextProps {
    estilMenu: string | undefined;
    setPreview: (value: ThemePreview) => void;
    removePreview: () => void;
}

const ThemeUserContext = React.createContext<ThemeUserContextProps | undefined>(undefined);

export const useThemeUserContext = () => {
    const context = React.useContext(ThemeUserContext);
    if (context === undefined) {
        throw new Error('useThemeUserContext must be used within a ThemeUserProvider');
    }
    return context;
};

export const ThemeUserProvider: React.FC<React.PropsWithChildren> = ({ children }) => {
    const { currentUser } = useNotibContext();
    const prefersDarkMode = useMediaQuery('(prefers-color-scheme: dark)');
    const [preview, setPreviewState] = React.useState<ThemePreview | undefined>();

    // En carregar/refrescar la configuració desada (p. ex. en tancar sessió i entrar amb un
    // altre usuari, o després de desar el perfil) es descarta la previsualització, que ja no
    // fa falta perquè coincideix amb el que s'acaba de desar.
    React.useEffect(() => {
        setPreviewState(undefined);
    }, [currentUser?.tema, currentUser?.estilMenu]);

    const setPreview = (value: ThemePreview) =>
        setPreviewState((previous) => ({ ...previous, ...value }));
    const removePreview = () => setPreviewState(undefined);

    const tema = preview?.tema ?? currentUser?.tema;
    const estilMenu = preview?.estilMenu ?? currentUser?.estilMenu;

    const theme = React.useMemo(() => {
        switch (tema) {
            case 'LIGHT':
                return lightTheme;
            case 'DARK':
                return darkTheme;
            case 'DRACULA':
                return draculaTheme;
            case 'SISTEMA':
            default:
                return prefersDarkMode ? darkTheme : lightTheme;
        }
    }, [tema, prefersDarkMode]);

    const contextValue = { estilMenu, setPreview, removePreview };
    return (
        <ThemeUserContext.Provider value={contextValue}>
            <ThemeProvider theme={theme}>
                <CssBaseline />
                {children}
            </ThemeProvider>
        </ThemeUserContext.Provider>
    );
};

export default ThemeUserProvider;
