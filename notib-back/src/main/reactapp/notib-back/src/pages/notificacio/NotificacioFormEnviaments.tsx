import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import Paper from '@mui/material/Paper';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import { MuiForm, FormField, useFormContext } from 'reactlib';

const NotificacioFormEnviamentPersonaFormContent: React.FC<{ interessat?: boolean }> = (props) => {
    const { interessat } = props;
    const { t } = useTranslation();
    const { data } = useFormContext();
    return (
        <Grid container spacing={2}>
            <Grid size={12} sx={{ mb: 1 }}>
                {t(
                    'page.notificacio.form.interessats.' +
                        (interessat ? 'interessat' : 'representant')
                )}
            </Grid>
            <Grid size={6}>
                <FormField name="interessatTipus" />
            </Grid>
            {data.visibleDocumentTipus && (
                <Grid size={6}>
                    <FormField name="documentTipus" />
                </Grid>
            )}
            {data.visibleNif && (
                <Grid size={6}>
                    <FormField
                        name="nif"
                        label={t(
                            'page.notificacio.form.interessats.nifLabel.' + data.interessatTipus
                        )}
                        required={data.requiredNif}
                        debounce
                    />
                </Grid>
            )}
            {data.visibleNom && (
                <Grid size={6}>
                    <FormField name="nom" required={data.requiredNom} debounce />
                </Grid>
            )}
            {data.visibleLlinatge1 && (
                <Grid size={6}>
                    <FormField name="llinatge1" required={data.requiredLlinatge1} debounce />
                </Grid>
            )}
            {data.visibleLlinatge2 && (
                <Grid size={6}>
                    <FormField name="llinatge2" debounce />
                </Grid>
            )}
            {data.visibleRaoSocial && (
                <Grid size={6}>
                    <FormField name="raoSocial" required={data.requiredRaoSocial} debounce />
                </Grid>
            )}
            {data.visibleDir3Codi && (
                <Grid size={6}>
                    <FormField name="dir3Codi" required={data.requiredDir3Codi} debounce />
                </Grid>
            )}
            {data.visibleTelefon && (
                <Grid size={6}>
                    <FormField name="telefon" debounce />
                </Grid>
            )}
            {data.visibleEmail && (
                <Grid size={6}>
                    <FormField name="email" required={data.requiredEmail} debounce />
                </Grid>
            )}
            {data.visibleIncapacitat && (
                <Grid size={6}>
                    <FormField name="incapacitat" debounce />
                </Grid>
            )}
        </Grid>
    );
};

const NotificacioFormEnviamentPersona: React.FC<{ indexKey?: number; interessat?: boolean }> = (
    props
) => {
    const { indexKey, interessat } = props;
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
            <NotificacioFormEnviamentPersonaFormContent interessat={interessat} />
        </MuiForm>
    );
};

const NotificacioFormEnviament: React.FC<{
    index: number;
    indexKey: number;
    handleRemove: (indexKey: number) => void;
}> = (props) => {
    const { index, indexKey, handleRemove } = props;
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
                <Grid size={12} sx={{ mt: 1 }}>
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
        <Paper sx={{ px: 2, py: 1, mb: 2 }}>
            <Grid container spacing={2}>
                <Grid size={10}>
                    <Typography variant="h6">Enviament {index}</Typography>
                </Grid>
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
            <Typography variant="h6" sx={{ mt: 3, mb: 2, borderBottom: 1, borderColor: 'divider' }}>
                {t('page.notificacio.form.tabs.enviaments')}
            </Typography>
            {enviaments?.map((e: any, i: number) => (
                <NotificacioFormEnviament
                    key={e.id}
                    index={i + 1}
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
