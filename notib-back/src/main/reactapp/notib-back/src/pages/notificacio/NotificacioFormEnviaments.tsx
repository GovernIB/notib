import React from 'react';
import Grid from '@mui/material/Grid';
import Paper from '@mui/material/Paper';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import { MuiForm, FormField } from 'reactlib';

const NotificacioFormEnviamentPersona: React.FC = () => {
    return (
        <MuiForm
            resourceName="personaResource"
            hiddenToolbar
            commonFieldComponentProps={{ size: 'small' }}>
            <Grid container spacing={2}>
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
    onRemove: (indexKey: string) => void;
}> = (props) => {
    const { indexKey, onRemove } = props;
    return (
        <MuiForm
            resourceName="notificacioEnviamentResource"
            hiddenToolbar
            commonFieldComponentProps={{ size: 'small' }}>
            <Paper sx={{ p: 2 }}>
                <Grid container spacing={2}>
                    <Grid size={10}>{indexKey}</Grid>
                    <Grid size={2} sx={{ textAlign: 'right' }}>
                        <IconButton onClick={() => onRemove(indexKey)}>
                            <Icon fontSize="small">delete</Icon>
                        </IconButton>
                    </Grid>
                    <Grid size={12}>
                        <FormField name="serveiTipus" />
                    </Grid>
                    <Grid size={12}>
                        <NotificacioFormEnviamentPersona />
                    </Grid>
                </Grid>
            </Paper>
        </MuiForm>
    );
};

const NotificacioFormEnviaments: React.FC = () => {
    const [enviaments, setEnviaments] = React.useState({ first: {} });
    const handleAddClick = () => {
        setEnviaments((e) => ({ ...e, ['' + new Date().valueOf()]: {} }));
    };
    const handleRemoveClick = (indexKey: string) => {
        setEnviaments((e: any) => {
            const { [indexKey]: removed, ...otherKeys } = e;
            return otherKeys;
        });
    };
    return (
        <>
            <Typography variant="h6" sx={{ mt: 3, mb: 1 }}>
                Enviaments
            </Typography>
            {Object.keys(enviaments).map((k) => (
                <NotificacioFormEnviament key={k} indexKey={k} onRemove={handleRemoveClick} />
            ))}
            <Button
                variant="outlined"
                startIcon={<Icon>add</Icon>}
                onClick={handleAddClick}
                sx={{ mt: 1 }}>
                Afegir enviament
            </Button>
        </>
    );
};

export default NotificacioFormEnviaments;
