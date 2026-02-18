import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import Paper from '@mui/material/Paper';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import { MuiForm, FormField, useFormContext } from 'reactlib';

const NotificacioFormEnviamentPersona: React.FC<{ indexKey?: number; interessat?: boolean }> = (
    props
) => {
    const { indexKey, interessat } = props;
    const { t } = useTranslation();
    const { data: parentFormData, apiRef: parentFormApiRef } = useFormContext();
    const handleDataChange = (data: any) => {
        if (interessat) {
            parentFormApiRef.current?.setFieldValue('titularInfo', data);
        } else {
            if (parentFormData?.representantsInfo != null) {
                const representantsWithData = parentFormData?.representantsInfo?.map((e: any) =>
                    e.id === indexKey ? { id: indexKey, ...data } : e
                );
                parentFormApiRef.current?.setFieldValue('representantsInfo', representantsWithData);
            } else {
                const representantsWithData = [{ id: indexKey, ...data }];
                parentFormApiRef.current?.setFieldValue('representantsInfo', representantsWithData);
            }
        }
    };
    return (
        <MuiForm
            resourceName="personaResource"
            onDataChange={handleDataChange}
            initOnChangeRequest
            hiddenToolbar
            commonFieldComponentProps={{ size: 'small' }}
            componentProps={{ sx: { mb: 3 } }}>
            <Grid container spacing={2}>
                <Grid size={12}>
                    {t(
                        'page.notificacio.form.interessats.' +
                            (interessat ? 'interessat' : 'representant')
                    )}
                </Grid>
                <Grid size={6}>
                    <FormField name="interessatTipus" />
                </Grid>
                <Grid size={6}>
                    <FormField name="nif" debounce />
                </Grid>
                <Grid size={6}>
                    <FormField name="nom" debounce />
                </Grid>
                <Grid size={6}>
                    <FormField name="llinatge1" debounce />
                </Grid>
                <Grid size={6}>
                    <FormField name="llinatge2" debounce />
                </Grid>
                <Grid size={6}>
                    <FormField name="email" debounce />
                </Grid>
            </Grid>
        </MuiForm>
    );
};

const NotificacioFormEnviament: React.FC<{
    indexKey: number;
    handleRemove: (indexKey: number) => void;
}> = (props) => {
    const { indexKey, handleRemove } = props;
    const { t } = useTranslation();
    const [ambRepresentant, setAmbRepresentant] = React.useState<boolean>(false);
    const { data: parentFormData, apiRef: parentFormApiRef } = useFormContext();
    const handleDataChange = (data: any) => {
        const enviamentsWithData = parentFormData?.enviaments?.map((e: any) =>
            e.id === indexKey ? { id: indexKey, ...data } : e
        );
        parentFormApiRef.current?.setFieldValue('enviaments', enviamentsWithData);
    };
    const formContent = React.useMemo(
        () => (
            <Grid container>
                <Grid size={12}>
                    <FormField name="serveiTipus" />
                </Grid>
                <Grid size={12}>
                    <NotificacioFormEnviamentPersona interessat />
                    {ambRepresentant && <NotificacioFormEnviamentPersona indexKey={indexKey} />}
                    <Button
                        variant="contained"
                        startIcon={<Icon>{ambRepresentant ? 'remove' : 'add'}</Icon>}
                        onClick={() => setAmbRepresentant((r) => !r)}
                        size="small">
                        {ambRepresentant
                            ? t('page.notificacio.form.interessats.remove')
                            : t('page.notificacio.form.interessats.add')}
                    </Button>
                </Grid>
            </Grid>
        ),
        [ambRepresentant]
    );
    return (
        <Paper sx={{ p: 2 }}>
            <Grid container spacing={2}>
                <Grid size={10}>{indexKey}</Grid>
                <Grid size={2} sx={{ textAlign: 'right' }}>
                    {indexKey !== 0 && (
                        <IconButton onClick={() => handleRemove(indexKey)}>
                            <Icon
                                fontSize="small"
                                title={t('page.notificacio.form.enviaments.remove')}>
                                delete
                            </Icon>
                        </IconButton>
                    )}
                </Grid>
                <Grid size={12}>
                    <MuiForm
                        resourceName="notificacioEnviamentResource"
                        onDataChange={handleDataChange}
                        hiddenToolbar
                        componentProps={{ sx: { mb: 2 } }}
                        commonFieldComponentProps={{ size: 'small' }}>
                        {formContent}
                    </MuiForm>
                </Grid>
            </Grid>
        </Paper>
    );
};

const NotificacioFormEnviaments: React.FC = () => {
    const { t } = useTranslation();
    const { data, apiRef: formApiRef } = useFormContext();
    const enviaments = data?.enviaments;
    React.useEffect(() => {
        const reset = !enviaments?.length;
        if (reset) {
            console.log('>>> reset', reset);
            formApiRef.current?.setFieldValue('enviaments', [{ id: new Date().valueOf() }]);
        }
    }, [enviaments]);
    const handleAddClick = () => {
        formApiRef.current?.setFieldValue('enviaments', [
            ...(enviaments ?? []),
            { id: new Date().valueOf() },
        ]);
    };
    const handleRemoveClick = (indexKey: number) => {
        formApiRef.current?.setFieldValue(
            'enviaments',
            enviaments.filter((e: any) => e.id !== indexKey)
        );
    };
    return (
        <>
            <Typography variant="h6" sx={{ mt: 3, mb: 1 }}>
                {t('page.notificacio.form.tabs.enviaments')}
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
                {t('page.notificacio.form.enviaments.add')}
            </Button>
        </>
    );
};

export default NotificacioFormEnviaments;
