import React, {useState} from 'react';
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
import { useNotibContext } from '../../components/NotibContext';
import GridFormField from '../../components/GridFormField';
import { useTabParam } from '../../hooks/useSearchParams';
import Button from "@mui/material/Button";
import {Icon, Tooltip} from "@mui/material";

const useEntitatId = () => {

    const { id: paramId } = useParams();
    const location = useLocation();
    const { isReady, currentEntitatId } = useNotibContext();
    const isCurrentPathname = location.pathname.endsWith('current');
    const id = isCurrentPathname ? currentEntitatId : paramId != null ? parseInt(paramId) : paramId;
    return {isReady: isCurrentPathname ? isReady : true, id, hiddenBackButton: isCurrentPathname,
    };
};

const CustomToolbar: React.FC = () => {

    const theme = useTheme();
    const { id, data } = useFormContext();
    const { isReady: apiIsReady, fieldDownload: apiFieldDownload } = useResourceApiService('entitatResource');
    const { getTokenParsed } = useAuthContext();
    const [logoUrl, setLogoUrl] = React.useState<string>();
    const [tokenParsed, setTokenParsed] = React.useState<any>();
    const dataIsReady = data != null;
    const backgroundColor = data?.colorFons ?? '#fff';
    React.useEffect(() => setTokenParsed(getTokenParsed()), []);
    React.useEffect(() => {

        if (!apiIsReady || !dataIsReady) {
            return;
        }
        if (!data.logoCapsalera) {
            setLogoUrl(goibLogoLight);
            return;
        }
        const args = { fieldName: 'logoCapsalera' };
        apiFieldDownload(id, args).then((blobFileName) => setLogoUrl(URL.createObjectURL(blobFileName.blob)));

    }, [apiIsReady, dataIsReady]);

    React.useEffect(() => {
            setLogoUrl(data.logoCapsalera ? `data:image/png;base64,${data.logoCapsalera.content}` : goibLogoLight);
    }, [data?.logoCapsalera]);

    return (
        <Toolbar component={Paper} square sx={{ backgroundColor: backgroundColor }}>
            {logoUrl && (<img alt="logo" src={logoUrl} height={49} style={{paddingLeft: '8px', paddingRight: '29px', borderRight: '1px solid ' + theme.palette.divider}}/>)}
            <img alt="logo2" src={notibLogoLight} height={49} style={{ paddingLeft: '24px', verticalAlign: 'middle' }}/>
            <div style={{ flexGrow: 1 }} />
            {tokenParsed && <TextAvatar text={tokenParsed.name} />}
        </Toolbar>
    );
};

const EntitatFormTabPersonalitzar: React.FC = () => {
    const { t } = useTranslation();
    return (
        <Paper variant="outlined" sx={{ p: 2, pt: 1 }}>
            <Typography variant="h6" gutterBottom>{t('page.entitats.form.personalitzar.capsalera')}</Typography>
            <Grid container spacing={2} sx={{ mt: 2 }}>
                <GridFormField size={6} name="logoCapsalera" accept="image/jpeg" />
                <GridFormField size={3} name="colorLletra" type="color" />
                <GridFormField size={3} name="colorFons" type="color" />
                <Grid size={12}>
                    <CustomToolbar />
                </Grid>
            </Grid>
        </Paper>
    );
};

const EntitatFormTabDades: React.FC = () => {

    const { data, apiRef } = useFormContext();
    const { t } = useTranslation();
    const { artifactAction: apiAction } = useResourceApiService('entitatResource');

    const dir3Changed = (codiDir3: string) => {

        if (!data.llibreEntitat) {
            return;
        }
        if (!codiDir3) {
            apiRef.current?.setFieldValue('llibreNom', '');
        }
        getLlibreEntitat(codiDir3);
    }

    const actualitzarLlibre = (codiDir3: string) => {

       if (data.llibreEntitat || !data.dir3Codi) {
            apiRef.current?.setFieldValue('llibreNom', '');
            return;
        }
        getLlibreEntitat(codiDir3);
    }

    const getLlibreEntitat = (codiDir3: string) => {

        apiAction(undefined, { code: 'LLIBRE_ENTITAT', data: {codiDir3: codiDir3} }).then(llibre => {
        if (!llibre) {
            return;
        }
        apiRef.current?.setFieldValue('llibre', llibre.codi);
        apiRef.current?.setFieldValue('llibreNom', llibre.nomCurt);

        }).catch(error => console.error(error))
    };
    //
    // const getOficinesEntitat = (oficinaEntitat: boolean, codiDir3: string) => {
    //
    //     if (!oficinaEntitat || ! codiDir3) {
    //         return;
    //     }
    //     apiAction(undefined, { code: 'OFICINES_ENTITAT', data: {codiDir3: codiDir3} }).then(oficines => {
    //         console.log(oficines);
    //         if (!oficines) {
    //             return;
    //         }
    //         console.log(oficines)
    //         apiRef.current?.setFieldValue('oficina', oficines);
    //         // apiRef.current?.setFieldValue('nomOficinaVirtual', oficina.nomCurt);
    //
    //     }).catch(error => console.error(error))
    // };

    const [oficines, setOficines] = useState<any[]>([]);

    const oficinaOptions = oficines.map((oficina) => ({
        value: oficina.codi,
        description: oficina.nom,
    }));

    const getOficinesEntitat = async (oficinaEntitat: boolean, codiDir3: string) => {

        if (!oficinaEntitat || !codiDir3) {
            setOficines([]);
            apiRef.current?.setFieldValue("oficina", undefined);
            return;
        }

        try {
            const result = await apiAction(undefined, {code: "OFICINES_ENTITAT", data: {codiDir3,},});
            const oficinaList = result.oficines ?? [];
            setOficines(oficinaList);
            // Clear the selected value when the list changes
            apiRef.current?.setFieldValue("oficina", undefined);
        } catch (error) {
            console.error(error);
            setOficines([]);
        }
    };

    return (
        <Grid container spacing={2}>
            <GridFormField size={4} name="codi" />
            <GridFormField size={4} name="tipus" />
            <Grid size={8} />
            <GridFormField size={8} name="nom" />
            <Grid size={4} />
            <GridFormField size={3} name="dir3Codi" onChange={dir3Codi => dir3Changed(dir3Codi)} />
            <GridFormField size={3} name="dir3CodiReg" />
            <GridFormField size={12} name="apiKey" />
            <GridFormField size={3} name="activa" />
            <GridFormField size={12} name="ambEntregaDeh" />
            <GridFormField size={3} name="entregaCieActiva" />
            {data?.entregaCieActiva && (
                <>
                    <GridFormField size={3} name="entregaCiePagadorPostal" />
                    <GridFormField size={3} name="entregaCiePagadorCie" />
                </>
            )}
            <Grid size={12} />
            <GridFormField size={3} name="llibreEntitat" onChange={() => actualitzarLlibre(data.dir3Codi)} />
            {data?.llibreEntitat && (
                <>
                    <Grid size={3}>
                        <GridFormField size={12} name="llibreNom" />
                        <Typography variant="caption" color="text.secondary" display="block" >
                            {t("page.entitats.form.llibre.error")}
                        </Typography>
                    </Grid>
                    <Tooltip title={t("page.entitats.form.llibre.refrescar")} placement="top">
                        <Button onClick={() => getLlibreEntitat(data.dir3Codi)}>
                            <Icon fontSize='small'>refresh</Icon>
                        </Button>
                    </Tooltip>
                </>
            )}
            <Grid size={12} />
            <GridFormField size={3} name="oficinaEntitat" onChange={oficinaEntitat => getOficinesEntitat(oficinaEntitat, data.dir3Codi)} />
            {data?.oficinaEntitat && (
                <GridFormField
                    size={3}
                    label=""
                    name="oficina"
                    type="enum"
                    options={oficinaOptions}
                />
            )}
            {/*{data?.oficinaEntitat && (*/}
            {/*    <select*/}
            {/*        value={data?.oficina ?? ""}*/}
            {/*        onChange={(event) =>*/}
            {/*            apiRef.current?.setFieldValue(*/}
            {/*                "oficina",*/}
            {/*                event.target.value*/}
            {/*            )*/}
            {/*        }*/}
            {/*    >*/}
            {/*        <option value="">Select an office</option>*/}

            {/*        {oficines.map((oficina) => (*/}
            {/*            <option key={oficina.codi} value={oficina.codi}>*/}
            {/*                {oficina.nom}*/}
            {/*            </option>*/}
            {/*        ))}*/}
            {/*    </select>*/}
            {/*)}*/}
            <GridFormField size={12} name="descripcio" type="textarea" />
        </Grid>
    );
};

const EntitatFormContent: React.FC<{ setSubtitle: (subtitle: string) => void }> = (props) => {

    const { setSubtitle } = props;
    const { t } = useTranslation();
    const { data } = useFormContext();
    const initialTab = useTabParam();
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
        <MuiFormTabs tabs={tabs} tabIndexesWithGrids={[2, 3, 4]} initialIndex={initialTab}>
            <MuiFormTabContent index={0} showOnCreate>
                <EntitatFormTabDades />
            </MuiFormTabContent>
            <MuiFormTabContent index={1}>
                <EntitatFormTabPersonalitzar />
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
                    title={id != null ? t('page.entitats.form.titleUpdate') : t('page.entitats.form.titleCreate')}
                    hiddenBackButton={hiddenBackButton ? true : undefined}
                    toolbarSubtitle={id != null ? subtitle : undefined}
                    createLink="./{{id}}"
                    //updateLink="../../"
                    componentProps={{ style: { height: '100%' } }}
                    commonFieldComponentProps={{ size: 'small' }}
                >
                    <EntitatFormContent setSubtitle={setSubtitle} />
                </MuiForm>
            </FormPage>
        )
    );
};

export default EntitatForm;
