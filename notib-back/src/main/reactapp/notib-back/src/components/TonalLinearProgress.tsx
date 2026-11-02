import type { ReactNode } from 'react';
import { Box, LinearProgress, type LinearProgressProps, type SxProps, type Theme } from '@mui/material';
import { type TonalColor, tonalProgress } from '../utils/tonal';

type TonalLinearProgressProps = Omit<LinearProgressProps, 'color' | 'variant'> & {
    color?: TonalColor;
    /** Estils del contenidor (posició, marges...). */
    boxSx?: SxProps<Theme>;
    /**
     * Text sobre la barra, normalment el percentatge. Obligatori a la pràctica: el color tonal sol no
     * té prou contrast (WCAG 1.4.11) i el nivell no es pot transmetre només amb el color.
     */
    children?: ReactNode;
};

/**
 * Barra de progrés en color tonal amb el valor escrit a sobre, en el color de text del tema. El text
 * és `aria-hidden` perquè el lector de pantalla ja llegeix el valor de la barra (`aria-valuenow`).
 */
const TonalLinearProgress = (props: TonalLinearProgressProps) => {
    const { color = 'success', boxSx, sx, children, ...other } = props;

    return (
        <Box position="relative" width="100%" sx={boxSx}>
            <LinearProgress
                variant="determinate"
                {...other}
                sx={[
                    { width: '100%', borderRadius: '4px' },
                    tonalProgress(color),
                    ...(sx === undefined ? [] : Array.isArray(sx) ? sx : [sx]),
                ]}
            />
            <Box
                color="text.primary"
                fontWeight={500}
                position="absolute"
                top={0}
                left={0}
                width="100%"
                height="100%"
                display="flex"
                alignItems="center"
                justifyContent="center"
                aria-hidden
            >
                {children}
            </Box>
        </Box>
    );
};

export default TonalLinearProgress;
