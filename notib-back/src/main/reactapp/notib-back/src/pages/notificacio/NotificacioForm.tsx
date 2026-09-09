import React from 'react';
import { useParams, useSearchParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import IconButton from '@mui/material/IconButton';
import Icon from '@mui/material/Icon';
import ToggleButton from '@mui/material/ToggleButton';
import ToggleButtonGroup from '@mui/material/ToggleButtonGroup';
import Box from '@mui/material/Box';
import {
    FormPage,
    MuiForm,
    FormField,
    useFormContext,
    useFormApiRef,
    useResourceApiContext,
} from 'reactlib';
import NotificacioFormEnviaments from './NotificacioFormEnviaments';
import NotificacioFormDocuments from './NotificacioFormDocuments';
import GridFormField from '../../components/GridFormField';

const JSonButton: React.FC = () => {
    const { data } = useFormContext();
    // TODO: Revisar que es aixo i per que serveix
    const visible = false;
    return (
        visible && (
            <IconButton onClick={() => console.log(data)}>
                <Icon>question_mark</Icon>
            </IconButton>
        )
    );
};

// Agrupació dels resultats del desplegable de procediments/serveis, de forma similar a com es fa
// al formulari JSP: primer els procediments/serveis comuns i després els de l'òrgan gestor. Els
// dos grups es construeixen a mà com a opcions més (no seleccionables) dins la mateixa llista de
// resultats, ja que el component genèric `FormField` no ofereix agrupació nativa: `optionsRequest`
// permet substituir la consulta per defecte i `optionRenderer` en personalitza la representació.
const PROC_SER_HEADER_COMU_ID = '__procSerGroup:comuns__';
const PROC_SER_HEADER_ORGAN_ID = '__procSerGroup:organs__';

type ProcSerOption = {
    id: string | number;
    description: string;
    disabled?: boolean;
    comu?: boolean;
};

const useProcSerOptionsRequest = (type: string) => {
    const { t } = useTranslation();
    const { fields } = useFormContext();
    const { requestHref } = useResourceApiContext();
    const dataSource = fields?.find((f) => f.name === 'procediment')?.dataSource;
    return React.useCallback(
        (q: string) => {
            if (dataSource == null) {
                return Promise.resolve({ options: [] });
            }
            const templateData = {
                quickFilter: q?.length ? q : null,
                filter: "tipus:'" + type.toUpperCase() + "'",
                page: 'UNPAGED',
            };
            return requestHref(dataSource.href, templateData).then((state) => {
                const items = state.getEmbedded().map((e) => ({
                    id: e.data[dataSource.valueField],
                    description: e.data[dataSource.labelField],
                    comu: e.data.comu,
                }));
                const comuns = items.filter((item) => item.comu);
                const organs = items.filter((item) => !item.comu);
                const options: ProcSerOption[] = [];
                if (comuns.length > 0) {
                    options.push({
                        id: PROC_SER_HEADER_COMU_ID,
                        description: t(`page.notificacio.form.camps.${type}Comuns`),
                        disabled: true,
                    });
                    options.push(...comuns);
                }
                if (organs.length > 0) {
                    options.push({
                        id: PROC_SER_HEADER_ORGAN_ID,
                        description: t(`page.notificacio.form.camps.${type}Organs`),
                        disabled: true,
                    });
                    options.push(...organs);
                }
                return { options };
            });
        },
        [dataSource, requestHref, type, t]
    );
};

const procSerOptionRenderer = ({ id, description }: { id: string | number; description: string }) => {
    if (id === PROC_SER_HEADER_COMU_ID || id === PROC_SER_HEADER_ORGAN_ID) {
        return (
            <Box
                sx={{
                    // Marges negatius perquè el fons ressaltat ocupi tota l'amplada de l'opció,
                    // compensant el padding horitzontal per defecte de les opcions de l'Autocomplete.
                    width: '100%',
                    mx: -2,
                    px: 2,
                    py: 0.75,
                    fontWeight: 'bold',
                    fontSize: '0.8125rem',
                    textTransform: 'uppercase',
                    letterSpacing: '0.04em',
                    bgcolor: 'primary.main',
                    color: 'primary.contrastText',
                }}
            >
                {description}
            </Box>
        );
    }
    return <Box sx={{ pl: 2 }}>{description}</Box>;
};

const ProcedimentServeiField: React.FC = () => {
    const { t } = useTranslation();
    const { data, apiRef: formApiRef } = useFormContext();
    const [type, setType] = React.useState<string>('procediment');
    const procSerOptionsRequest = useProcSerOptionsRequest(type);

    const handleChange = (value: any) => {
        setType(value);
        console.log(data);
        if (data.procediment != null) {
            formApiRef.current?.setFieldValue('procediment', null);
        }
    };

    const handleChangeProcediment = (value: any) => {
        console.log(value);
        if (data.procediment != null) {
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
                        onChange={(value) => handleChangeProcediment(value)}
                        label={t(`page.notificacio.form.camps.${type}`)}
                        required={data.procedimentRequired}
                        optionsRequest={procSerOptionsRequest}
                        optionRenderer={procSerOptionRenderer}
                    />
                </Grid>
            </Grid>
        );
    } else {
        return (
            <FormField
                name="procediment"
                onChange={(value) => handleChangeProcediment(value)}
                label={t(`page.notificacio.form.camps.procediment`)}
                required={data.procedimentRequired}
                optionsRequest={procSerOptionsRequest}
                optionRenderer={procSerOptionRenderer}
            />
        );
    }
};

export const NotificacioFormContent: React.FC = () => {
    const { t } = useTranslation();
    return (
        <Grid container spacing={2}>
            <Grid size={12}>
                <Typography variant="h6" sx={{ mb: 2, borderBottom: 1, borderColor: 'divider' }}>
                    {t('page.notificacio.form.tabs.remesa')}
                </Typography>
                <GridFormField size={12} name="concepte" />
            </Grid>
            <GridFormField size={12} name="descripcio" type="textarea" />
            <GridFormField size={6} name="organGestor" namedQueries={`PERM_READ`} />
            <Grid size={6}>
                <ProcedimentServeiField />
            </Grid>
            <GridFormField size={6} name="numExpedient" />
            <GridFormField size={6} name="idioma" />
            <GridFormField size={6} name="enviamentDataProgramada" type="date" />
            <GridFormField size={2} name="caducitatDiesNaturals" />
            <GridFormField size={4} name="caducitat" type="date" />
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
        formApiRef.current?.setFieldValue('enviamentsInfo', [{ id: new Date().valueOf() }]);
        formApiRef.current?.setFieldValue('documentsInfo', [{ id: new Date().valueOf() }]);
        formApiRef.current?.setFieldValue('origen', 'WEB');
        formApiRef.current?.setFieldValue('tipusUsuari', 'INTERFICIE_WEB');
    };

    return (
        <FormPage>
            <MuiForm
                resourceName="notificacioResource"
                id={id != null ? parseInt(id) : id}
                title={
                    id != null
                        ? t(`page.notificacio.form.title.${type}.update`)
                        : t(`page.notificacio.form.title.${type}.create`)
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
