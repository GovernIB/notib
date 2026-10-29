import React, { useContext, useRef } from 'react';
import { Grid } from '@mui/material';
import { MuiFilter, useAuthContext, useBaseAppContext, useFilterApiRef, useFormContext } from 'reactlib';
import { useTranslation } from 'react-i18next';
import { GridButton } from './GridFormField';
import FilterAutoApply from './FilterAutoApply';
import { FilterAutoApplyContext, FilterClearContext, normalizeFilterData } from './FilterAutoApplyContext';
import { alpha, type Theme } from '@mui/material/styles';
import { accentColor } from '../theme';
import { icons } from '../utils/icons';

/**
 * Botons dels filtres dels llistats: mateixa alçada que els camps (ocupen l'alçada de la fila de la
 * graella del filtre), text a 14px i colors de la barra de títol (text en el color d'accent i vora
 * suau), perquè el verd base de l'aplicació no té prou contrast com a text sobre blanc.
 */
const filterButtonSx = (theme: Theme) => ({
    minHeight: '40px',
    margin: 0,
    fontSize: '14px',
    fontWeight: 400,
    letterSpacing: '.4px',
    textTransform: 'none' as const,
    color: theme.palette.mode === 'dark' ? theme.palette.text.primary : accentColor(theme),
    borderColor: alpha(accentColor(theme), 0.4),
    '&:hover': { borderColor: accentColor(theme) },
});

/** Només la icona: sense padding perquè hi càpiga en una cel·la estreta. */
const filterIconOnlyButtonSx = { minWidth: 0, px: 0 };

/**
 * Filtre estàndard dels llistats.
 *
 * Port de `src/components/StyledMuiFilter.tsx` de Distribució i ConCSV. Embolcalla el `MuiFilter` de
 * reactlib i hi afegeix el que la llibreria no porta:
 * <ul>
 *   <li>l'aplicació automàtica del filtre en canviar-ne els camps, sense botó "Filtra" (veure
 *       FilterAutoApply), amb el filtre en mode `buttonControlled` perquè la llibreria no l'apliqui
 *       a cada tecla,</li>
 *   <li>el botó "Neteja" només amb icona i el commutador de cerca avançada opcional (`advancedSearch`),
 *       que desa el seu estat al camp `advanced` de les dades del filtre (`useFormContext().data.advanced`),</li>
 *   <li>el layout Grid dels camps (separació de 8px) i l'estil `styledFilter` (theme.ts).</li>
 * </ul>
 *
 * A diferència de Distribució no desa les dades del filtre a la sessió (`sessionStorage`) sinó al
 * `localStorage`, amb la clau i el format de la llibreria (Filter.tsx), que és qui les recupera
 * (`persistentStateActive`). La llibreria només les desa quan el filtre no és `buttonControlled`, per això
 * es desen aquí en cada aplicació.
 *
 * El filtre ha de ser d'una sola fila, botons inclosos: els camps de cada filtre han de sumar
 * `12 - FILTER_BUTTONS_COLUMNS.md` a `md` (o `12 - FILTER_BUTTONS_COLUMNS_ADVANCED.md` amb cerca
 * avançada). Si hi ha més camps, es posen només a la cerca avançada.
 */

const filterStyle = { className: 'styledFilter' };

/**
 * Columnes (de 12) que ocupa el bloc de botons del filtre, per punt de ruptura. És l'única font de
 * les mides del bloc: es pot ajustar tot des d'aquí. Les mateixes que a Distribució i ConCSV.
 *
 * El bloc simple només duu el botó "Neteja" (amb icona). Amb la cerca avançada (`advancedSearch`) n'hi ha
 * dos: el commutador (fletxa de desplegar/contreure + text "Avançat"/"Bàsic") i "Neteja" (només icona).
 */
export const FILTER_BUTTONS_COLUMNS = { xs: 12, sm: 2, md: 0.5 } as const;
export const FILTER_BUTTONS_COLUMNS_ADVANCED = { xs: 12, sm: 3, md: 1.5 } as const;

// Amplada (px) per sota de la qual els botons d'acció del filtre avançat es redueixen a
// només icona, perquè el text no quedi retallat dins del seu contenidor.
export const FILTER_ADVANCED_ICON_ONLY_BREAKPOINT = 1615;

export type FilterButtonProps = {
    value: string;
    text?: string;
    icon?: string;
    componentProps?: any;
};

/** Botó que commuta la cerca avançada: desa l'estat al camp `advanced` del formulari del filtre. */
const AdvancedButton: React.FC<{ size: number; iconOnlyBreakpoint: any }> = ({ size, iconOnlyBreakpoint }) => {
    const { t } = useTranslation();
    const { data, apiRef } = useFormContext();
    const active = !!data?.advanced;
    const text = t(active ? 'comu.basic' : 'comu.avancat');
    return (
        <GridButton
            size={size}
            title={t(active ? 'comu.tancarFiltreAvançat' : 'comu.obrirFiltreAvançat')}
            icon={active ? icons.cercaAvancadaTanca : icons.cercaAvancadaObre}
            iconOnlyFrom="sm"
            iconOnlyBreakpoint={iconOnlyBreakpoint}
            aria-expanded={active}
            onClick={() => apiRef?.current?.setFieldValue?.('advanced', !active)}
            sx={[filterButtonSx, { whiteSpace: 'nowrap', overflow: 'hidden' }]}
        >
            {text}
        </GridButton>
    );
};

/**
 * Dóna al botó «Neteja» la neteja del filtre,
 * amb els valors per defecte: els camps que en duen un (`field.value`, que ve del backend) i
 * `initialData`. Ha d'anar dins el filtre per poder llegir-ne els camps.
 */
const FilterClearScope: React.FC<{
    initialData?: Record<string, unknown>;
    onClear: (defaults: Record<string, unknown>) => void;
    children: React.ReactNode;
}> = ({ initialData, onClear, children }) => {
    const { fields } = useFormContext();
    const clear = () => {
        const defaults: Record<string, unknown> = {};
        fields?.forEach((f: any) => f.value && (defaults[f.name] = f.value));
        onClear({ ...defaults, ...initialData });
    };
    return <FilterClearContext.Provider value={clear}>{children}</FilterClearContext.Provider>;
};

/** Botó del bloc de botons del filtre (només icona): el de netejar fa servir FilterClearScope. */
const FilterActionButton: React.FC<{
    size: number;
    button: FilterButtonProps;
    iconOnlyBreakpoint: any;
    onClick: () => void;
}> = ({ size, button, iconOnlyBreakpoint, onClick }) => {
    const clearFilter = useContext(FilterClearContext);
    return (
        <GridButton
            size={size}
            title={button.text}
            icon={button.icon}
            iconOnlyBreakpoint={iconOnlyBreakpoint}
            sx={[filterButtonSx, filterIconOnlyButtonSx]}
            onClick={() => {
                if (button.value === 'clear') clearFilter?.();
                onClick();
            }}
            {...button.componentProps}
        />
    );
};

const StyledMuiFilter = (props: any) => {
    const { t } = useTranslation();
    const filterRef = useFilterApiRef();

    const defaultButtons: FilterButtonProps[] = [{ value: 'clear', text: t('comu.neteja'), icon: icons.neteja }];

    const {
        buttons = defaultButtons,
        buttonCallback,
        buttonGridProps,
        apiRef = filterRef,
        springFilterBuilder,
        onSpringFilterChange,
        onDataChange: externalOnDataChange,
        commonFieldComponentProps,
        componentProps,
        children,
        // Es crida una sola vegada, quan el filtre ja té l'estat inicial (dades recuperades o filtre buit).
        onReady,
        // Es crida si l'estat inicial és el filtre buit: no s'aplica res, i la graella ha de saber-ho.
        onEmptyInitialData,
        advancedSearch = false,
        buttonIconOnlyBreakpoint = 'lg',
        // Contingut sota la fila del filtre (p. ex. una llegenda)
        footer,
        persistentStateActive,
        persistentStateKey,
        persistentStateStorage,
        ...other
    } = props;

    // El filtre de la llibreria només desa el seu estat quan s'aplica sol (no amb `buttonControlled`), així
    // que es desa aquí en cada aplicació, amb la mateixa clau i el mateix format que el de la llibreria
    // (Filter.tsx), que és qui el recupera en tornar a muntar el filtre.
    const { code: appCode } = useBaseAppContext();
    const { isAuthenticated, getUserId } = useAuthContext();
    const persistFilterData = (data: any) => {
        if (!persistentStateActive) return;
        try {
            const storage = persistentStateStorage !== 'session' ? localStorage : sessionStorage;
            const key = (persistentStateKey ?? other.resourceName ?? '').toUpperCase();
            const userSuffix = isAuthenticated ? '_' + getUserId().toUpperCase() : '';
            storage.setItem(appCode + '_FLT_' + key + userSuffix, JSON.stringify(data ?? {}));
        } catch {
            // Sense emmagatzematge disponible, el filtre simplement no es recorda
        }
    };

    // Dades (normalitzades) de l'últim filtre aplicat i cancel·lació de l'aplicació automàtica pendent
    // (veure FilterAutoApply, que els fa servir per no repetir cerques).
    const lastAppliedRef = useRef<string | undefined>(undefined);
    const autoApplyCancelRef = useRef<(() => void) | undefined>(undefined);
    const initialDoneRef = useRef(false);
    const readyRef = useRef(false);

    const cercar = () => {
        apiRef?.current?.filter?.();
    };
    // `defaults`: valors per defecte del filtre (veure FilterClearScope). No es fa servir `clear()` de la
    // llibreria: fa un revert a les dades amb què es va muntar el formulari, que són les recuperades de
    // l'estat desat, i el filtre no s'esborraria mai després de recarregar.
    const netejar = (defaults: Record<string, unknown> = {}) => {
        autoApplyCancelRef.current?.();
        apiRef?.current?.reset?.(defaults);
        // El filtre buit (o amb els camps per defecte) s'aplica de seguida, sense esperar el retard dels
        // camps de text de FilterAutoApply (que no repeteix la cerca, perquè ja és l'últim filtre aplicat).
        setTimeout(cercar, 0);
    };

    const callback = (value: string) => {
        if (value === 'search') cercar();
        buttonCallback?.(value);
    };

    const marcarLlest = () => {
        if (!readyRef.current) {
            readyRef.current = true;
            onReady?.();
        }
    };

    // El filtre ho aplica tot per aquí (botó, Intro i aplicació automàtica): s'hi recorda què s'ha aplicat.
    const handleSpringFilterChange = (springFilter: any) => {
        const data = apiRef?.current?.getData?.();
        lastAppliedRef.current = normalizeFilterData(data);
        persistFilterData(data);
        onSpringFilterChange?.(springFilter);
        marcarLlest();
    };

    // Amb cerca avançada el commutador duu icona (fletxa) + text i ocupa la major part del bloc;
    // la resta de botons (només icona) es reparteixen el que queda.
    const advancedButtonSize = buttons.length > 0 ? 8 : 12;
    const otherButtonSize = advancedSearch ? 4 / Math.max(buttons.length, 1) : 12 / Math.max(buttons.length, 1);

    const autoApply = <FilterAutoApply lastAppliedRef={lastAppliedRef} cancelRef={autoApplyCancelRef} />;

    return (
        <MuiFilter
            apiRef={apiRef}
            commonFieldComponentProps={{ size: 'small', ...commonFieldComponentProps }}
            componentProps={{ ...filterStyle, ...componentProps }}
            buttonControlled
            persistentStateActive={persistentStateActive}
            persistentStateKey={persistentStateKey}
            persistentStateStorage={persistentStateStorage}
            filterOnFieldEnterKeyPressed
            springFilterBuilder={springFilterBuilder}
            onSpringFilterChange={handleSpringFilterChange}
            onDataChange={(data: any) => {
                const hasData = data && Object.keys(data).length > 0;
                if (!initialDoneRef.current) {
                    initialDoneRef.current = true;
                    if (hasData) {
                        // Dades recuperades (estat persistit o valors per defecte): s'apliquen.
                        cercar();
                    } else {
                        onEmptyInitialData?.();
                        marcarLlest();
                    }
                }
                externalOnDataChange?.(data);
            }}
            {...other}
        >
            <FilterAutoApplyContext.Provider value>
                <FilterClearScope initialData={other.initialData} onClear={netejar}>
                    <Grid container direction="row" columnSpacing={1} rowSpacing={1}>
                        {children}
                        {autoApply}

                        <Grid
                            container
                            direction="row"
                            columnSpacing={1}
                            rowSpacing={1}
                            size={advancedSearch ? FILTER_BUTTONS_COLUMNS_ADVANCED : FILTER_BUTTONS_COLUMNS}
                            sx={{ display: 'flex', justifyContent: 'end', marginLeft: 'auto' }}
                            {...buttonGridProps}
                        >
                            {advancedSearch && (
                                <AdvancedButton size={advancedButtonSize} iconOnlyBreakpoint={buttonIconOnlyBreakpoint} />
                            )}
                            {buttons?.map((button: FilterButtonProps) => (
                                <FilterActionButton
                                    size={otherButtonSize}
                                    key={button.value}
                                    button={button}
                                    iconOnlyBreakpoint={buttonIconOnlyBreakpoint}
                                    onClick={() => callback(button.value)}
                                />
                            ))}
                        </Grid>
                    </Grid>
                    {footer}
                </FilterClearScope>
            </FilterAutoApplyContext.Provider>
        </MuiFilter>
    );
};

export default StyledMuiFilter;
