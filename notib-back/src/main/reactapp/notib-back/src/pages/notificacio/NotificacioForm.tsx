import React from 'react';
import { useParams, useSearchParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import IconButton from '@mui/material/IconButton';
import Icon from '@mui/material/Icon';
import ToggleButton from '@mui/material/ToggleButton';
import ToggleButtonGroup from '@mui/material/ToggleButtonGroup';
import { FormPage, MuiForm, FormField, useFormContext, useFormApiRef } from 'reactlib';
import NotificacioFormEnviaments from './NotificacioFormEnviaments';
import NotificacioFormDocuments from './NotificacioFormDocuments';

const JSonButton: React.FC = () => {
    const { data } = useFormContext();
    const visible = false;
    return (
        visible && (
            <IconButton onClick={() => console.log(data)}>
                <Icon>question_mark</Icon>
            </IconButton>
        )
    );
};

const ProcedimentServeiField: React.FC = () => {
    const { t } = useTranslation();
    const { data, apiRef: formApiRef } = useFormContext();
    const [type, setType] = React.useState<string>('procediment');
    const handleChange = (value: any) => {
        setType(value);
        if (data.procediment != null) {
            formApiRef.current?.setFieldValue('procediment', null);
        }
    };
    if (data.enviamentTipus === 'SIR') {
        return (
            <Grid container spacing={2}>
                <Grid size={3}>
                    <ToggleButtonGroup
                        value={type}
                        exclusive
                        onChange={(_event, value) => handleChange(value)}
                        size="small"
                        fullWidth
                    >
                        <ToggleButton value="procediment">
                            {t('page.notificacio.form.camps.procediment')}
                        </ToggleButton>
                        <ToggleButton value="servei">
                            {t('page.notificacio.form.camps.servei')}
                        </ToggleButton>
                    </ToggleButtonGroup>
                </Grid>
                <Grid size={9}>
                    <FormField
                        name="procediment"
                        label={t('page.notificacio.form.camps.' + type)}
                        filter={"tipus:'" + type.toUpperCase() + "'"}
                        required={data.procedimentRequired}
                    />
                </Grid>
            </Grid>
        );
    } else {
        return (
            <FormField
                name="procediment"
                filter={"tipus:'" + type.toUpperCase() + "'"}
                required={data.procedimentRequired}
            />
        );
    }
};

const NotificacioFormContent: React.FC = () => {
    const { t } = useTranslation();
    return (
        <Grid container spacing={2}>
            <Grid size={12}>
                <Typography variant="h6" sx={{ mb: 2, borderBottom: 1, borderColor: 'divider' }}>
                    {t('page.notificacio.form.tabs.remesa')}
                </Typography>
                <FormField name="concepte" />
            </Grid>
            <Grid size={12}>
                <FormField name="descripcio" type="textarea" />
            </Grid>
            <Grid size={6}>
                <FormField name="organGestor" />
            </Grid>
            <Grid size={6}>
                <ProcedimentServeiField />
            </Grid>
            <Grid size={6}>
                <FormField name="numExpedient" />
            </Grid>
            <Grid size={6}>
                <FormField name="idioma" />
            </Grid>
            <Grid size={6}>
                <FormField name="enviamentDataProgramada" type="date" />
            </Grid>
            <Grid size={2}>
                <FormField name="caducitatDiesNaturals" />
            </Grid>
            <Grid size={4}>
                <FormField name="caducitat" type="date" />
            </Grid>
        </Grid>
    );
};

export const NotificacioForm: React.FC = () => {
    const { t } = useTranslation();
    const { id } = useParams();
    const [searchParams] = useSearchParams();
    const type = searchParams.get('type');
    const formApiRef = useFormApiRef();
    const initialData = {
        enviamentsInfo: [{ id: new Date().valueOf() }],
        documentsInfo: [{ id: new Date().valueOf() }],
    };
    const handleReset = () => {
        // Feim això perquè, si no refrescam l'id de l'enviament i del document que es crea per defecte, React no detecta que ha
        // canviat l'atribut key i no refresca la informació dels formularis.
        formApiRef.current.setFieldValue('enviamentsInfo', [{ id: new Date().valueOf() }]);
        formApiRef.current.setFieldValue('documentsInfo', [{ id: new Date().valueOf() }]);
    };
    return (
        <FormPage>
            <MuiForm
                resourceName="notificacioResource"
                id={id != null ? parseInt(id) : id}
                title={
                    id != null
                        ? t('page.notificacio.form.title.' + type + '.update')
                        : t('page.notificacio.form.title.' + type + '.create')
                }
                initOnChangeRequest
                additionalData={type ? { enviamentTipus: type, ...initialData } : initialData}
                onReset={handleReset}
                apiRef={formApiRef}
                createLink="../"
                toolbarElementsWithPositions={[{ position: 2, element: <JSonButton /> }]}
                componentProps={{ style: { height: '100%' } }}
                commonFieldComponentProps={{ size: 'small' }}
            >
                <NotificacioFormContent />
                <NotificacioFormEnviaments />
                <NotificacioFormDocuments />
            </MuiForm>
        </FormPage>
    );
};

export default NotificacioForm;
