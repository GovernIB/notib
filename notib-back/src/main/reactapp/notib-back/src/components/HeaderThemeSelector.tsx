import React from 'react';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import { useColorScheme } from '@mui/material/styles';

const TYPOGRAPHY_VARIANT = 'h6';

const HeaderThemeSelector: React.FC = () => {
    const { mode, setMode } = useColorScheme();
    const handleModeChange = (mode: any) => {
        setMode(mode);
    }
    return <Box sx={{ display: 'flex' }}>
        <Typography
            variant={TYPOGRAPHY_VARIANT}
            onClick={mode !== 'light' ? () => handleModeChange('light') : undefined}
            sx={mode === 'light' ? { fontWeight: 'bold' } : { cursor: 'pointer', fontWeight: '400' }}>Light</Typography>
        <Typography variant={TYPOGRAPHY_VARIANT} sx={{ mx: 1 }}>|</Typography>
        <Typography
            variant={TYPOGRAPHY_VARIANT}
            onClick={mode !== 'dark' ? () => handleModeChange('dark') : undefined}
            sx={mode === 'dark' ? { fontWeight: 'bold' } : { cursor: 'pointer', fontWeight: '400' }}>Dark</Typography>
    </Box>;
}

export default HeaderThemeSelector;