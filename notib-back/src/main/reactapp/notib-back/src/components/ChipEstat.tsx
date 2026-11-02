import { useTranslation } from 'react-i18next';
import { Icon, Typography } from '@mui/material';
import { type TonalColor, tonal, tonalNeutre } from '../utils/tonal';

type Estat = 'ERROR' | 'WARN' | 'OK';

type ChipEstatProps = {
    estat: Estat;
    label?: string;
};

// Etiqueta d'estat d'una integració en color tonal (vegeu utils/tonal.ts). Segueix l'estil de les
// etiquetes d'estat de RIPEA (EstatMessage/StyledLabel), amb les mides que s'hi veuen renderitzades:
// rectangle de cantonades poc arrodonides, text `caption` (12px) i icona de 18px davant, en lloc
// d'un xip amb forma de càpsula. Centrada verticalment a la cel·la: el tema alinea a dalt les
// cel·les amb `renderCell` i la resta de text de la fila va centrat.
const ESTAT_CONFIG: Record<Estat, { color: TonalColor; icona: string; clau: string }> = {
    OK: { color: 'success', icona: 'check', clau: 'page.integracio.detall.estatEnum.ok' },
    WARN: { color: 'warning', icona: 'error_outline', clau: 'page.integracio.detall.estatEnum.warn' },
    ERROR: { color: 'error', icona: 'warning', clau: 'page.integracio.detall.estatEnum.error' },
};

const ChipEstat = ({ estat, label }: ChipEstatProps) => {
    const { t } = useTranslation();
    const config = ESTAT_CONFIG[estat] as (typeof ESTAT_CONFIG)[Estat] | undefined;
    const text = label ?? (config ? t(config.clau) : undefined);

    return (
        <Typography
            variant="caption"
            title={text}
            sx={[
                {
                    px: 1,
                    py: 0.5,
                    borderRadius: '5px',
                    display: 'flex',
                    alignItems: 'center',
                    alignSelf: 'center',
                    width: 'max-content',
                },
                config ? tonal(config.color) : tonalNeutre,
            ]}
        >
            {config && (
                <Icon sx={{ fontSize: '18px', mr: text != null ? 1 : 0 }}>
                    {config.icona}
                </Icon>
            )}
            {text}
        </Typography>
    );
};
export default ChipEstat;
