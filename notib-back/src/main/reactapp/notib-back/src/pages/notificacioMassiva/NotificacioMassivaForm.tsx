import {FormPage, MuiActionReportButton, MuiForm, useFormApiRef} from "reactlib";
import {useTranslation} from "react-i18next";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import Grid from "@mui/material/Grid";
import GridFormField from "../../components/GridFormField.tsx";
import React from "react";
import {useParams} from "react-router-dom";

function NumberCircle({ number, size = 40, bg = '#31708f', color = '#fff' }: { number: string; size?: number; bg?: string; color?: string }) {
    const style = {
        width: size,
        height: size,
        borderRadius: '50%',
        background: bg,
        color,
        display: 'inline-flex',
        alignItems: 'center',
        justifyContent: 'center',
        // fontWeight: 600,
        userSelect: 'none',
    };
    return (<Box sx={style}>{number}</Box>);
}

const NotificacioMassivaForm: React.FC = () => {

    const { t } = useTranslation();
    const formApiRef = useFormApiRef();
    const { id } = useParams();
    return (
        <FormPage>
            <MuiForm
                id={id != null ? parseInt(id) : id}
                resourceName="notificacioMassivaResource"
                title= {t('page.notificacioMassiva.form.title')}
                apiRef={formApiRef}
                createLink="../"
                toolbarElementsWithPositions={[
                    {
                        position: 2,
                        element:
                            <Box>
                                <MuiActionReportButton
                                    resourceName={"notificacioMassivaResource"}
                                    report="DESCARREGAR_CODIS_ENTREGA_POSTAL"
                                    reportFileType="CUSTOM"
                                    title={t('page.notificacioMassiva.form.codisEntregaPostal')}
                                    buttonComponentProps={{ variant: 'outlined', sx: { mr: 1, backgroundColor: '#c9302c', borderColor: '#c9302c', textTransform: 'none', color: '#ffff'  } }}
                                    buttonIcon="download"/>
                                <MuiActionReportButton
                                    resourceName={"notificacioMassivaResource"}
                                    report="DESCARREGAR_MODEL_DADES_NOTIFICACIO_MASSIVA"
                                    reportFileType="CUSTOM"
                                    title={t('page.notificacioMassiva.form.modelCsv')}
                                    buttonComponentProps={{ variant: 'outlined', sx: { mr: 1, backgroundColor: '#5bc0de', borderColor: '#5bc0de', textTransform: 'none', color: '#ffff' } }}
                                    buttonIcon="download"/>
                                </Box>
                        ,
                    },
                ]}
                >
                <Box sx={{ padding: '15px', bgcolor:"#d9edf7", color:"#31708f", borderColor:"#bce8f1"}}>
                    <Box sx={{ fontWeight: 'bold' }}>{t('page.notificacioMassiva.form.indicacions.title')}</Box>
                    <Box sx={{ paddingLeft: '30px', marginTop:"15px"}}>
                        <Box sx={{display: 'flex'}}>
                            <NumberCircle number="1" size={35} bg="#37a2d6"/>
                            <Typography sx={{ paddingLeft: '10px'}}>{t('page.notificacioMassiva.form.indicacions.indicacio1')}</Typography>
                        </Box>
                        <Box sx={{ paddingTop: '10px', display: 'flex'}}>
                            <NumberCircle number="2" size={35} bg="#37a2d6"/>
                            <Typography sx={{ paddingLeft: '10px'}}>{t('page.notificacioMassiva.form.indicacions.indicacio2')}</Typography>
                        </Box>
                    </Box>
                </Box>
                <Grid container spacing={2} sx={{marginTop:'50px'}}>
                    {/*<Grid size={12}>*/}
                    {/*    <FormField*/}
                    {/*        type="file"*/}
                    {/*        name="csv"*/}
                    {/*        label={t('page.notificacioMassiva.form.csvFieldLabel')}*/}
                    {/*        componentProps={{ helperText: t('page.notificacioMassiva.form.csvFieldText')}}*/}
                    {/*        accept=".csv"*/}
                    {/*        required*/}
                    {/*    />*/}
                    {/*</Grid>*/}
                    {/*<Grid size={12}>*/}
                    {/*    <FormField*/}
                    {/*        type="file"*/}
                    {/*        name="zip"*/}
                    {/*        label={t('page.notificacioMassiva.form.zipFieldLabel')}*/}
                    {/*        componentProps={{ helperText: t('page.notificacioMassiva.form.zipFieldText')}}*/}
                    {/*        accept=".zip"*/}
                    {/*        required*/}
                    {/*    />*/}
                    {/*</Grid>*/}
                    <GridFormField size={12} name="caducitat" label={t('page.notificacioMassiva.form.caducitat')} type="date" required />
                    <GridFormField size={12} name="email" label={t('page.notificacioMassiva.form.email')} />
                </Grid>
            </MuiForm>
        </FormPage>
    );
};

export default NotificacioMassivaForm;
