import React from 'react';
import { useParams, useLocation } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import Badge from '@mui/material/Badge';
import Typography from '@mui/material/Typography';
import Toolbar from '@mui/material/Toolbar';
import Paper from '@mui/material/Paper';
import { useTheme } from '@mui/material/styles';
import {
    FormPage,
    MuiForm,
    MuiFormTabs,
    MuiFormTabContent,
    TextAvatar,
    useAuthContext,
    useFormContext,
    useResourceApiService,
} from 'reactlib';
import EntitatFormTabTipusDocs from './EntitatFormTabTipusDocs';
import EntitatFormTabAplicacions from './EntitatFormTabAplicacions';
import EntitatFormTabPermisos from './EntitatFormTabPermisos';
import goibLogoLight from '../../assets/goib_logo_light.svg';
import notibLogoLight from '../../assets/notib_logo_light.png';
import GridFormField from '../../components/GridFormField';
import { useNotibContext } from '../../components/NotibContext';

const useEntitatId = () => {
    const { id: paramId } = useParams();
    const location = useLocation();
    const { isReady, currentEntitatId } = useNotibContext();
    const isCurrentPathname = location.pathname.endsWith('current');
    const id = isCurrentPathname ? currentEntitatId : paramId != null ? parseInt(paramId) : paramId;
    return {
        isReady: isCurrentPathname ? isReady : true,
        id,
        hiddenBackButton: isCurrentPathname,
    };
};

const CustomToolbar: React.FC = () => {
    const theme = useTheme();
    const { id, data } = useFormContext();
    const { isReady: apiIsReady, fieldDownload: apiFieldDownload } =
        useResourceApiService('entitatResource');
    const { getTokenParsed } = useAuthContext();
    const [logoUrl, setLogoUrl] = React.useState<string>();
    const [tokenParsed, setTokenParsed] = React.useState<any>();
    const dataIsReady = data != null;
    const backgroundColor = data?.colorFons ?? '#fff';
    React.useEffect(() => {
        setTokenParsed(getTokenParsed());
    }, []);
    React.useEffect(() => {
        if (apiIsReady && dataIsReady) {
            if (data.logoCapsalera) {
                const args = { fieldName: 'logoCapsalera' };
                apiFieldDownload(id, args).then((blobFileName) => {
                    setLogoUrl(URL.createObjectURL(blobFileName.blob));
                });
            } else {
                setLogoUrl(goibLogoLight);
            }
        }
    }, [apiIsReady, dataIsReady]);
    React.useEffect(() => {
        if (data.logoCapsalera) {
            setLogoUrl(`data:image/png;base64,${data.logoCapsalera.content}`);
        } else {
            setLogoUrl(goibLogoLight);
        }
    }, [data?.logoCapsalera]);
    return (
        <Toolbar component={Paper} square sx={{ backgroundColor: backgroundColor }}>
            {logoUrl && (
                <img
                    alt="logo"
                    src={logoUrl}
                    height={49}
                    style={{
                        paddingLeft: '8px',
                        paddingRight: '29px',
                        borderRight: '1px solid ' + theme.palette.divider,
                    }}
                />
            )}
            <img
                alt="logo2"
                src={notibLogoLight}
                height={49}
                style={{ paddingLeft: '24px', verticalAlign: 'middle' }}
            />
            <div style={{ flexGrow: 1 }} />
            {tokenParsed && <TextAvatar text={tokenParsed.name} />}
        </Toolbar>
    );
};

const EntitatFormContent: React.FC<{ setSubtitle: (subtitle: string) => void }> = (props) => {
    const { setSubtitle } = props;
    const { t } = useTranslation();
    const { data } = useFormContext();
    React.useEffect(() => {
        setSubtitle(data?.codi + ', ' + data?.nom);
    }, [data]);
    const tipusDocsTabLabel = (
        <Badge badgeContent={data.tipusDocCount} color="primary">
            {t('page.entitats.form.tabs.tipusDocs')}
        </Badge>
    );
    const aplicacionsTabLabel = (
        <Badge badgeContent={data.aplicacioCount} color="primary">
            {t('page.entitats.form.tabs.aplicacions')}
        </Badge>
    );
    const permisosTabLabel = (
        <Badge badgeContent={data.aclEntryCount} color="primary">
            {t('page.entitats.form.tabs.permisos')}
        </Badge>
    );
    const tabs = [
        t('page.entitats.form.tabs.dades'),
        t('page.entitats.form.tabs.personalitzar'),
        { label: tipusDocsTabLabel },
        { label: aplicacionsTabLabel },
        { label: permisosTabLabel },
    ];
    return (
        <MuiFormTabs tabs={tabs} tabIndexesWithGrids={[2, 3, 4]}>
            <MuiFormTabContent index={0} showOnCreate>
                <Grid container spacing={2}>
                    <GridFormField size={4} name="codi" />
                    <Grid size={8} />
                    <GridFormField size={4} name="tipus" />
                    <GridFormField size={8} name="nom" />
                    <GridFormField size={6} name="dir3Codi" />
                    <GridFormField size={6} name="dir3CodiReg" />
                    <GridFormField size={3} name="activa" />
                    <GridFormField size={3} name="ambEntregaDeh" />
                    <GridFormField size={3} name="llibreEntitat" />
                    <GridFormField size={3} name="oficinaEntitat" />
                    <GridFormField size={12} name="apiKey" />
                    <GridFormField size={12} name="descripcio" type="textarea" />
                </Grid>
            </MuiFormTabContent>
            <MuiFormTabContent index={1}>
                <Paper variant="outlined" sx={{ p: 2, pt: 1 }}>
                    <Typography variant="h6" gutterBottom>
                        {t('page.entitats.form.personalitzar.capsalera')}
                    </Typography>
                    <Grid container spacing={2} sx={{ mt: 2 }}>
                        <GridFormField size={6} name="logoCapsalera" accept="image/jpeg" />
                        <GridFormField size={3} name="colorLletra" type="color" />
                        <GridFormField size={3} name="colorFons" type="color" />
                        <Grid size={12}>
                            <CustomToolbar />
                        </Grid>
                    </Grid>
                </Paper>
            </MuiFormTabContent>
            <MuiFormTabContent index={2}>
                <EntitatFormTabTipusDocs />
            </MuiFormTabContent>
            <MuiFormTabContent index={3}>
                <EntitatFormTabAplicacions />
            </MuiFormTabContent>
            <MuiFormTabContent index={4}>
                <EntitatFormTabPermisos />
            </MuiFormTabContent>
        </MuiFormTabs>
    );
};

export const EntitatForm: React.FC = () => {
    const { t } = useTranslation();
    const { isReady, id, hiddenBackButton } = useEntitatId();
    const [subtitle, setSubtitle] = React.useState<string>();
    return (
        isReady && (
            <FormPage>
                <MuiForm
                    resourceName="entitatResource"
                    id={id}
                    title={
                        id != null
                            ? t('page.entitats.form.titleUpdate')
                            : t('page.entitats.form.titleCreate')
                    }
                    hiddenBackButton={hiddenBackButton ? true : undefined}
                    toolbarSubtitle={id != null ? subtitle : undefined}
                    createLink="./{{id}}"
                    //updateLink="../../"
                    componentProps={{ style: { height: '100%' } }}
                    commonFieldComponentProps={{ size: 'small' }}>
                    <EntitatFormContent setSubtitle={setSubtitle} />
                </MuiForm>
            </FormPage>
        )
    );
};

export default EntitatForm;
