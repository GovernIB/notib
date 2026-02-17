import React from 'react';
import Grid from '@mui/material/Grid';
import Paper from '@mui/material/Paper';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import { MuiForm, FormField, useFormContext, useFormApiRef } from 'reactlib';

const NotificacioFormEnviamentPersona: React.FC<{ title: string }> = (props) => {
    const { title } = props;
    return (
        <MuiForm
            resourceName="personaResource"
            hiddenToolbar
            commonFieldComponentProps={{ size: 'small' }}
            componentProps={{ sx: { mb: 3 } }}>
            <Grid container spacing={2}>
                <Grid size={12}>{title}</Grid>
                <Grid size={6}>
                    <FormField name="interessatTipus" />
                </Grid>
                <Grid size={6}>
                    <FormField name="nif" />
                </Grid>
                <Grid size={6}>
                    <FormField name="nom" />
                </Grid>
                <Grid size={6}>
                    <FormField name="llinatge1" />
                </Grid>
                <Grid size={6}>
                    <FormField name="llinatge2" />
                </Grid>
                <Grid size={6}>
                    <FormField name="email" />
                </Grid>
            </Grid>
        </MuiForm>
    );
};

const NotificacioFormEnviament: React.FC<{
    indexKey: string;
    handleRemove: (indexKey: string) => void;
}> = (props) => {
    const { indexKey, handleRemove } = props;
    const [ambRepresentant, setAmbRepresentant] = React.useState<boolean>(false);
    const formApiRef = useFormApiRef();
    const { data: parentFormData, apiRef: parentFormApiRef } = useFormContext();
    const handleDataChange = (data: any) => {
        const enviamentsWithData = parentFormData?.enviaments?.map((e: any) =>
            e.id === indexKey ? { id: indexKey, ...data } : e
        );
        parentFormApiRef.current?.setFieldValue('enviaments', enviamentsWithData);
    };
    return (
        <MuiForm
            resourceName="notificacioEnviamentResource"
            onDataChange={handleDataChange}
            apiRef={formApiRef}
            hiddenToolbar
            componentProps={{ sx: { mb: 2 } }}
            commonFieldComponentProps={{ size: 'small' }}>
            <Paper sx={{ p: 2 }}>
                <Grid container spacing={2}>
                    <Grid size={10}>{indexKey}</Grid>
                    <Grid size={2} sx={{ textAlign: 'right' }}>
                        <IconButton onClick={() => handleRemove(indexKey)}>
                            <Icon fontSize="small">delete</Icon>
                        </IconButton>
                    </Grid>
                    <Grid size={12}>
                        <FormField name="serveiTipus" />
                    </Grid>
                    <Grid size={12}>
                        <NotificacioFormEnviamentPersona title="Interessat" />
                        {ambRepresentant && (
                            <NotificacioFormEnviamentPersona title="Representant" />
                        )}
                        <Button
                            variant="contained"
                            startIcon={<Icon>{ambRepresentant ? 'remove' : 'add'}</Icon>}
                            onClick={() => setAmbRepresentant((r) => !r)}
                            size="small">
                            {ambRepresentant ? 'Eliminar representant' : 'Afegir representant'}
                        </Button>
                    </Grid>
                </Grid>
            </Paper>
        </MuiForm>
    );
};

const NotificacioFormEnviaments: React.FC = () => {
    const { data, apiRef } = useFormContext();
    const enviaments = data?.enviaments;
    const handleAddClick = () => {
        apiRef.current?.setFieldValue('enviaments', [...enviaments, { id: new Date().valueOf() }]);
    };
    const handleRemoveClick = (indexKey: string) => {
        apiRef.current?.setFieldValue(
            'enviaments',
            enviaments.filter((e: any) => e.id !== indexKey)
        );
    };
    return (
        <>
            <Typography variant="h6" sx={{ mt: 3, mb: 1 }}>
                Enviaments
            </Typography>
            {enviaments?.map((e: any) => (
                <NotificacioFormEnviament
                    key={e.id}
                    indexKey={e.id}
                    handleRemove={handleRemoveClick}
                />
            ))}
            <Button
                variant="contained"
                startIcon={<Icon>add</Icon>}
                onClick={handleAddClick}
                size="small">
                Afegir enviament
            </Button>
        </>
    );
};

export default NotificacioFormEnviaments;
