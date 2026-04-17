import React from 'react';
import Grid from '@mui/material/Grid';
import Button from '@mui/material/Button';
import Icon from '@mui/material/Icon';
import TextField from '@mui/material/TextField';
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
        flex: 2,
    },
    {
        field: 'denominacio',
        flex: 6,
    },
    {
        field: 'cif',
        flex: 2,
    },
    {
        field: 'sir',
        flex: 1,
        renderCell: (params: any) => {
            return params.value ? (
                <Icon color="success">check_circle</Icon>
            ) : (
                <Icon color="disabled">cancel</Icon>
            );
        },
    },
    {
        field: 'selectable',
        flex: 2,
        renderCell: (params: any) => {
            return params.value ? (
                <Icon color="success">check_circle</Icon>
            ) : (
                <>
                    <Icon color="warning" sx={{ mr: 1 }}>
                        warning
                    </Icon>
                    {params.row.noCif && 'Sense CIF'}
                    {params.row.noSir && 'Sense SIR'}
                    {params.row.viaValib && 'Via VALIB'}
                </>
            );
        },
    },
];

const Dir3SearchFilterContent: React.FC = () => {
    const { data } = useFormContext();
    const { apiRef: filterApiRef } = useFilterContext();
    return (
        <Grid container spacing={2}>
            <Grid size={3}>
                <FormField name="codi" />
            </Grid>
            <Grid size={6}>
                <FormField name="denominacio" />
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
    const [dir3Name, setDir3Name] = React.useState<string>();
    const { apiRef: formApiRef } = useFormContext();
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
    const handleSearchClick = () => {
        gridDialogApiRef.current
            .show()
            .then((value) => {
                formApiRef.current?.setFieldValue(name, value.codi);
                setDir3Name(value.denominacio);
            })
            .catch(() => {});
    };
    return (
        <>
            <Grid container spacing={2}>
                <Grid size={2}>
                    <FormField name={name} required={required} readOnly />
                </Grid>
                <Grid size={9}>
                    <TextField value={dir3Name ?? ''} disabled size="small" fullWidth />
                </Grid>
                <Grid size={1}>
                    <Button
                        variant="outlined"
                        startIcon={<Icon>search</Icon>}
                        fullWidth
                        onClick={handleSearchClick}
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
                    readOnly: true,
                    isRowSelectable: (params: any) => params.row.selectable,
                    getRowClassName: (params: any) =>
                        params.row.selectable ? 'selectable-row' : 'no-hover',
                    sx: {
                        '& .selectable-row': {
                            cursor: 'pointer',
                        },
                        '& .no-hover:hover': {
                            backgroundColor: 'transparent',
                        },
                    },
                    autoFindDisabled: true,
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
                onRowClickEnabled={(row) => row.selectable}
            />
        </>
    );
};

export default Dir3SearchInput;
