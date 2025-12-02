import React from 'react';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import { useBaseAppContext } from 'reactlib';

const TYPOGRAPHY_VARIANT = 'h6';

const allLanguages = [{
    locale: 'ca',
    name: 'Català'
}, {
    locale: 'es',
    name: 'Castellà'
}];

export type LanguageItem = {
    locale: string;
    name: string;
} & any;

type HeaderLanguageSelectorProps = {
    languages?: LanguageItem[];
    onLanguageChange?: (language?: string) => void;
} & any;

const HeaderLanguageSelector: React.FC<HeaderLanguageSelectorProps> = (props) => {
    const {
        languages,
        onLanguageChange,
        ...otherProps
    } = props;
    const { sx: otherSx, ...otherOtherProps } = otherProps;
    const { currentLanguage, setCurrentLanguage } = useBaseAppContext();
    React.useEffect(() => {
        onLanguageChange?.(currentLanguage);
    }, [currentLanguage]);
    return languages ? <Box sx={{ ...otherSx, display: 'flex' }} {...otherOtherProps}>
        {allLanguages?.map((l: LanguageItem, i: number) => <React.Fragment key={l.locale}>
            {currentLanguage === l.locale ? <Typography
                variant={TYPOGRAPHY_VARIANT}
                sx={{ fontWeight: 'bold' }}>{l.locale.toUpperCase()}</Typography> : <Typography
                    variant={TYPOGRAPHY_VARIANT}
                    onClick={() => setCurrentLanguage(l.locale)}
                    sx={{ cursor: 'pointer', fontWeight: '400' }}>
                {l.locale.toUpperCase()}
            </Typography>}
            {i < allLanguages.length - 1 && <Typography key={'locale_sep_' + i} variant={TYPOGRAPHY_VARIANT} sx={{ mx: 1 }}>|</Typography>}
        </React.Fragment>)}
    </Box> : null;
}

export default HeaderLanguageSelector;