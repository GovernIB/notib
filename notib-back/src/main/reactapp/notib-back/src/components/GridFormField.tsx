import { Breakpoint, Button, Grid, GridSize, Icon, Typography, useMediaQuery, useTheme } from '@mui/material';
import { FormField, FormFieldProps, useFormContext } from 'reactlib';
import { isTypingFieldType, useFilterAutoApply } from './FilterAutoApplyContext';
type ResponsiveStyleValue<T> = T | Array<T | null> | { [key in Breakpoint]?: T | null };

type GridFormFieldProps = FormFieldProps & {
    size: ResponsiveStyleValue<GridSize>;
};

/** Botó que ocupa una cel·la del Grid; per sota de `iconOnlyBreakpoint` amaga el text. */
export const GridButton = (props: any) => {
    const { title, icon, size, children, hidden, sx, iconOnlyBreakpoint = 'md', iconOnlyFrom, ...other } = props;
    const theme = useTheme();
    // `iconOnlyFrom`: només icona entre aquest breakpoint i `iconOnlyBreakpoint` (p. ex. a xs el bloc ocupa
    // tota l'amplada i el text hi cap); sense ell, només icona per sota de `iconOnlyBreakpoint`.
    const iconOnly = useMediaQuery(
        iconOnlyFrom ? theme.breakpoints.between(iconOnlyFrom, iconOnlyBreakpoint) : theme.breakpoints.down(iconOnlyBreakpoint)
    );
    const iconNode = typeof icon === 'string' ? <Icon aria-hidden sx={{ mr: !iconOnly && children ? 0.5 : 0 }}>{icon}</Icon> : icon;

    return (
        <Grid size={size} title={title} hidden={hidden}>
            <Button
                variant="outlined"
                sx={[{ borderRadius: '4px', width: '100%', height: '100%', minWidth: 0 }, ...(Array.isArray(sx) ? sx : [sx])]}
                style={{ margin: 0 }}
                aria-label={title && (iconOnly || !children) ? title : undefined}
                {...other}
            >
                {iconNode}
                {!iconOnly && children}
            </Button>
        </Grid>
    );
};

export const GridButtonField = (props: any) => {
    const { name, icon, label, hiddenLabel = false, ...other } = props;
    const { data, apiRef, fields } = useFormContext();

    return (
        <GridButton
            onClick={() => {
                apiRef?.current?.setFieldValue?.(name, !data?.[name]);
            }}
            variant={data?.[name] ? 'contained' : 'outlined'}
            title={fields?.find?.((item) => item?.name === name)?.label || ''}
            {...other}
        >
            <Icon sx={{ m: 0, mr: hiddenLabel ? 0 : 1 }}>{icon}</Icon>
            {!hiddenLabel && (
                <Typography variant="subtitle2" sx={{ textTransform: 'none' }}>
                    {fields?.find?.((item) => item?.name === name)?.label || label || ''}
                </Typography>
            )}
        </GridButton>
    );
};

const GridFormField: React.FC<GridFormFieldProps> = (props) => {

    const { size } = props;
    const { fields } = useFormContext();
    const autoApply = useFilterAutoApply();
    const field = fields?.find?.((item: any) => item?.name === props.name);
    return (
        <Grid size={size}>
            {/* Dins un filtre que s'aplica sol, els camps de text no retarden el canvi pel seu compte
                (el seu debounce de 300 ms): el retard i el Tab els gestiona FilterAutoApply. Es posa
                abans de `props` perquè una pantalla ho pugui sobreescriure. */}
            <FormField
                {...(autoApply && isTypingFieldType(field?.type) ? { debounceDisabled: true } : null)}
                {...props}
            />
        </Grid>
    );
};
export default GridFormField;
