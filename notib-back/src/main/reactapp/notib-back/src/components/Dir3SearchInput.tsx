import React from 'react';
import Grid from '@mui/material/Grid';
import Button from '@mui/material/Button';
import Icon from '@mui/material/Icon';
import {
    FormField,
    MuiDataGridDialog,
    useMuiDataGridDialogApiRef,
    MuiFilter,
    useFormContext,
    useFilterContext,
    springFilterBuilder as filterBuilder,
} from 'reactlib';

const dir3DialogColumns = [
    {
        field: 'codi',
        flex: 1,
    },
    {
        field: 'denominacio',
        flex: 4,
    },
    {
        field: 'sir',
        flex: 1,
    },
];

const Dir3SearchFilterContent: React.FC = () => {
    const { data } = useFormContext();
    const { apiRef: filterApiRef } = useFilterContext();
    return (
        <Grid container spacing={2}>
            <Grid size={3}>
                <FormField name="codi" debounce />
            </Grid>
            <Grid size={6}>
                <FormField name="denominacio" debounce />
            </Grid>
            <Grid size={3}>
                <FormField name="nivellAdministracio" />
            </Grid>
            <Grid size={3}>
                <FormField name="comunitatAutonoma" autocomplete />
            </Grid>
            <Grid size={3}>
                <FormField
                    name="provincia"
                    autocomplete
                    requestParams={
                        data?.comunitatAutonoma != null
                            ? { comunitatAutonoma: data?.comunitatAutonoma }
                            : undefined
                    }
                />
            </Grid>
            <Grid size={3}>
                <FormField
                    name="municipi"
                    autocomplete
                    requestParams={
                        data?.provincia != null ? { provincia: data?.provincia } : undefined
                    }
                />
            </Grid>
            <Grid size={1.5}>
                <Button
                    variant="outlined"
                    onClick={() => filterApiRef.current?.clear()}
                    startIcon={<Icon>filter_alt_off</Icon>}
                    fullWidth
                >
                    Netejar
                </Button>
            </Grid>
            <Grid size={1.5}>
                <Button
                    variant="contained"
                    onClick={() => filterApiRef.current?.filter()}
                    startIcon={<Icon>filter_alt</Icon>}
                    fullWidth
                >
                    Filtrar
                </Button>
            </Grid>
        </Grid>
    );
};

export const Dir3SearchInput: React.FC<{ name: string; required?: true }> = (props) => {
    const { name, required } = props;
    const gridDialogApiRef = useMuiDataGridDialogApiRef();
    const springFilterBuilder = (data: any) => {
        return filterBuilder.and(
            filterBuilder.eq('codi', data?.codi),
            filterBuilder.eq('denominacio', data?.denominacio),
            filterBuilder.eq('nivellAdministracio', data?.nivellAdministracio),
            filterBuilder.eq('comunitatAutonoma', data?.comunitatAutonoma),
            filterBuilder.eq('provincia', data?.provincia),
            filterBuilder.eq('municipi', data?.municipi)
        );
    };
    return (
        <>
            <Grid container spacing={2}>
                <Grid size={11}>
                    <FormField name={name} required={required} disabled />
                </Grid>
                <Grid size={1}>
                    <Button
                        variant="outlined"
                        startIcon={<Icon>search</Icon>}
                        fullWidth
                        onClick={() => gridDialogApiRef.current.show()}
                    >
                        Cercar
                    </Button>
                </Grid>
            </Grid>
            <MuiDataGridDialog
                resourceName="dir3Resource"
                title="Consulta d'administracions públiques a DIR3"
                columns={dir3DialogColumns}
                dataGridComponentProps={{
                    autoFindDisabled: true,
                    rows: [],
                    toolbarHide: true,
                    toolbarAdditionalRow: (
                        <MuiFilter
                            resourceName="dir3Resource"
                            code="FILTER_DIR3"
                            springFilterBuilder={springFilterBuilder}
                            buttonControlled
                            validationActive
                            commonFieldComponentProps={{ size: 'small' }}
                            componentProps={{ sx: { mb: 2 } }}
                        >
                            <Dir3SearchFilterContent />
                        </MuiFilter>
                    ),
                    height: 500,
                }}
                dialogComponentProps={{ fullWidth: true, maxWidth: 'lg' }}
                apiRef={gridDialogApiRef}
            />
        </>
    );
};

export default Dir3SearchInput;
