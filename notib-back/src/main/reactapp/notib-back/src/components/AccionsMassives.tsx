import {Box, Button, ButtonGroup, Chip, Icon, Menu, MenuItem, Tooltip} from '@mui/material';
import React, {RefObject} from 'react';
import {useTranslation} from 'react-i18next';
import {useBaseAppContext, useMuiActionReportLogic, useResourceApiService} from "reactlib";
import {ExportFileType} from "../../lib/components/ResourceApiContext.tsx";
import {TemporalMessageSeverity} from "../../lib/components/BaseAppContext.tsx";
import {GridApiPro} from "@mui/x-data-grid-pro";
import Divider from "@mui/material/Divider";
import Grid from "@mui/material/Grid";
import GridFormField from "./GridFormField.tsx";

export interface MenuOption {
    label: string;
    tooltip: string;
    onClick: () => void;
    icon?: string;
    disabled?: boolean;
}

interface AccionsMassivesProps {
    options: MenuOption[],
    buttonLabel?: string,
    sizeSelection?: number,
    apiRef: any,
    resource?: string
}

const AccionsMassives: React.FC<AccionsMassivesProps> = (props) => {
    const {t} = useTranslation();
    const {
        options,
        buttonLabel = t('page.accioMassiva.accions.labelBoto'),
        sizeSelection,
        apiRef
    } = props;

    const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
    const open = Boolean(anchorEl);

    const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
        setAnchorEl(event.currentTarget);
    };
    const handleClose = () => {
        setAnchorEl(null);
    };

    const handleOptionClick = (optionOnClick: () => void) => {
        handleClose();
        optionOnClick();
    };

    const {isReady: apiIsReady, find: apiFind} = useResourceApiService(props.resource);

    // Handle selection actions
    const handleSelectAllGlobal = () => {
        if (apiIsReady) {
            // apiFind({unpaged: true, filter: "id: 32031097"})
            apiFind({unpaged: true})
                .then((app) => {
                    const allIds = app?.rows.map(row => row.id);
                    apiRef.current.selectRows(allIds, true, true);
                });
        }
    };

    const handleDeselectAllGlobal = () => {

        if (!apiRef) {
            return;
        }
        apiRef.current.selectRows([], false, true);
    };


    return (
        <>
            <ButtonGroup variant="outlined" aria-label="Basic button group">
                <Tooltip title={t('page.accioMassiva.accions.selectAll')} arrow>
                    <Button color="primary" onClick={handleSelectAllGlobal}>
                        <Icon fontSize='small'>check_box</Icon>
                    </Button>
                </Tooltip>
                <Tooltip title={t('page.accioMassiva.accions.deselectAll')} arrow>
                    <Button color="primary" onClick={handleDeselectAllGlobal}>
                        <Icon fontSize='small'>check_box_outline_blank</Icon>
                    </Button>
                </Tooltip>
                <Button
                    id="basic-button"
                    aria-controls={open ? 'basic-menu' : undefined}
                    aria-haspopup="true"
                    aria-expanded={open ? 'true' : undefined}
                    onClick={handleClick}
                    endIcon={<Icon>{open ? 'arrow_drop_up' : 'arrow_drop_down'}</Icon>}
                    variant="outlined"
                    sx={{mr: 1, textTransform: 'none'}}
                >
                    <Box sx={{display: 'flex', gap: 1, justifyContent: 'space-between'}}>
                        <Chip label={sizeSelection} size="small" color="default"/>
                        {buttonLabel}
                    </Box>
                </Button>
            </ButtonGroup>

            <Menu
                id="basic-menu"
                anchorEl={anchorEl}
                open={open}
                keepMounted
                onClose={handleClose}
                slotProps={{
                    list: {
                        'aria-labelledby': 'basic-button',
                    },
                }}
            >
                {options.map((option, index) => (
                    ('type' in option && (option as any).type === 'divider')
                        ? (<Divider key={index}/>)
                        : (<MenuItem
                            key={index}
                            onClick={() => handleOptionClick(option.onClick)}
                            disabled={option.disabled}
                            title={option.tooltip}
                        >
                            {option.icon && <Icon sx={{mr: 1}}>{option.icon}</Icon>}
                            {option.label}
                        </MenuItem>)
                ))}
            </Menu>
        </>
    );
};


const iniciaDescarga = (url: string, fileName: string) => {
    const link = document.createElement('a');
    link.href = url;
    link.download = fileName;
    document.body.appendChild(link);
    link.click();
    link.remove(); // Limpieza
    URL.revokeObjectURL(url);
}
const iniciaDescargaBlob = (result: any) => {
    const url = URL.createObjectURL(result.blob);
    iniciaDescarga(url, result.fileName)
}

export const useAccionsMassives = (refresh?: () => void) => {

    const {t} = useTranslation();
    const {artifactAction: apiAction, artifactReport: apiReport} = useResourceApiService('notificacioResource');
    const {temporalMessageShow} = useBaseAppContext();


    const massiveReport = (ids: Set<any> | undefined, code: string, msg: string, seleccioTipus: string, fileType: ExportFileType) => {
        apiReport(undefined, {code: code, fileType: fileType, data: {ids: [...ids], seleccioTipus: seleccioTipus}})
            .then(response => {
                refresh?.()
                iniciaDescargaBlob(response)
                temporalMessageShow(null, msg, 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            })
    }

    const massiveAction = (ids: Set<any> | undefined, code: string, msg: string, seleccioTipus: string, ...apiRef: any[]) => {
        apiAction(undefined, {code: code, data: {ids: [...ids], seleccioTipus: seleccioTipus}})
            .then(resposta => {
                refresh?.()
                if (!resposta || resposta.ok || resposta.errors?.length === 0 && resposta.noExecutables?.length === 0) {
                    temporalMessageShow(null, msg, 'success');
                    return;
                }
                let severity: TemporalMessageSeverity = "success";
                msg = "";
                if (resposta.errors?.length > 0) {
                    msg += t('page.accioMassiva.accions.respostesError') + "\n";
                    resposta.errors.forEach(r => msg += r.id + " -  Error: " + r.errorDesc + "\n");
                    if (msg.length > 0) {
                        severity = "error";
                    }
                }
                if (resposta.noExecutables?.length > 0) {
                    severity = severity === "success" ? "warning" : severity;
                    msg = msg.length > 0 ? "\n" + msg : msg;
                    msg += t('page.accioMassiva.accions.noExecutades');
                    resposta.noExecutables.forEach(r => msg += (r.referencia ? r.referencia : r.id) + ", ");
                    msg = msg.substring(0, msg.length - 2);
                }
                temporalMessageShow(null, msg, severity);

            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            }).finally(() => {
            if (code === 'ESBORRAR_MASSIU') {
                apiRef[0].current.refresh();
            }
        });
    }

    const descarregarExcel = (ids: Set<any> | undefined, seleccioTipus: string): void => {

        temporalMessageShow(null, t('page.accioMassiva.accions.executant'), 'info');
        massiveReport(ids, 'EXPORTAR_EXCEL', t('page.accioMassiva.accions.exportarFullCalcul.ok'), seleccioTipus, 'ODS');
    }

    const descarregarJustificants = (ids: Set<any> | undefined, seleccioTipus: string): void => {

        temporalMessageShow(null, t('page.accioMassiva.accions.executant'), 'info');
        massiveReport(ids, 'DESCARREGAR_JUSTIFICANT_MASSIU', t('page.accioMassiva.accions.justificantEnviament.ok'), seleccioTipus, 'CUSTOM');
    }

    const descarregarCertificacions = (ids: Set<any> | undefined, seleccioTipus: string): void => {

        temporalMessageShow(null, t('page.accioMassiva.accions.executant'), 'info');
        massiveReport(ids, 'DESCARREGAR_CERTIFICACIO_MASSIU', t('page.accioMassiva.accions.certificacioRecepcio.ok'), seleccioTipus, 'CUSTOM');
    }

    const actualitzarEstat = (ids: Set<any> | undefined, seleccioTipus: string): void => {
        temporalMessageShow(null, t('page.accioMassiva.accions.executant'), 'info');
        massiveAction(ids, 'ACTUALITZAR_ESTAT_MASSIU', t('page.accioMassiva.accions.actualitzarEstat.ok'), seleccioTipus);
    }

    const reenviarAmbError = (ids: Set<any> | undefined, seleccioTipus: string): void => {
        temporalMessageShow(null, t('page.accioMassiva.accions.executant'), 'info');
        massiveAction(ids, 'REENVIAR_AMB_ERROR_MASSIU', t('page.accioMassiva.accions.reenviarAmbError.ok'), seleccioTipus);
    }

    const esborrarMassiu = (ids: Set<any> | undefined, seleccioTipus: string, apiRef: RefObject<GridApiPro> | null): void => {
        temporalMessageShow(null, t('page.accioMassiva.accions.executant'), 'info');
        massiveAction(ids, 'ESBORRAR_MASSIU', t('page.accioMassiva.accions.esborrar.ok'), seleccioTipus, apiRef);
    }

    const reactivarConsulesCanviEstatMassiu = (ids: Set<any> | undefined, seleccioTipus: string): void => {
        temporalMessageShow(null, t('page.accioMassiva.accions.executant'), 'info');
        massiveAction(ids, 'REACTIVAR_CONSULTES_CANVI_ESTAT_MASSIU', t('page.accioMassiva.accions.reactivarCanviEstat.ok'), seleccioTipus);
    }

    const reactivarCallbacksMassiu = (ids: Set<any> | undefined, seleccioTipus: string): void => {
        temporalMessageShow(null, t('page.accioMassiva.accions.executant'), 'info');
        massiveAction(ids, 'REACTIVAR_CALLBACKS_MASSIU', t('page.accioMassiva.accions.reactivarCallbacks.ok'), seleccioTipus);
    }

    const enviarNotificacionsMovilMassiu = (ids: Set<any> | undefined, seleccioTipus: string): void => {
        temporalMessageShow(null, t('page.accioMassiva.accions.executant'), 'info');
        massiveAction(ids, 'ENVIAR_NOTIFICACIONS_MOVIL_MASSIU', t('page.accioMassiva.accions.notificacionsMovil.ok'), seleccioTipus);
    }

    const botons = [{value: true, text: t('comu.guardar'), icon: 'save', componentProps: {variant: 'contained'}},
        {value: false, text: t('comu.cancelar'), componentProps: {variant: 'outlined'}}];


    const {exec: marcarProcessatMassiu, formDialogComponent: marcarProcessatMassiuDialog} = useMuiActionReportLogic(
        'notificacioResource',
        'MARCAR_PROCESSAT_MASSIU',
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        (<Grid container spacing={2}>
            <GridFormField size={12} name="motiu" type="textarea" required/>
        </Grid>),
        undefined,
        undefined,
        botons,
        undefined,
        undefined,
        resposta => {
            if (!resposta) {
                temporalMessageShow(null, t('page.notificacio.grid.accions.marcarProcessades.noReposta'), "error");
                return;
            }
            temporalMessageShow(null, t('page.accioMassiva.accions.marcarProcessades.ok'), "success");
        },
        undefined,
        undefined,
        true,
    );

    const {exec: anularRemesaMassiu, formDialogComponent: anularRemesaMassiuDialog} = useMuiActionReportLogic(
        'notificacioResource',
        'ANULAR_MASSIU',
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        (<Grid container><GridFormField size={12} name="motiu" type="textarea" required/></Grid>),
        undefined,
        undefined,
        botons,
        undefined,
        undefined,
        resposta => {
            if (!resposta) {
                temporalMessageShow(null, t('page.notificacio.grid.accions.anular.noReposta'), "error");
                return;
            }
            temporalMessageShow(null, t('page.accioMassiva.accions.anular.ok'), "success");
        },
        undefined,
        undefined,
        true,
    );

    const {exec: ampliarTerminiMassiu, formDialogComponent: ampliarTerminiMassiuDialog} = useMuiActionReportLogic(
        'notificacioResource',
        'AMPLIAR_TERMINI_MASSIU',
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        (<Grid container spacing={2}>
            <GridFormField size={12} name="dies" required/>
            <GridFormField size={12} name="motiu" type="textarea" required/>
        </Grid>),
        undefined,
        undefined,
        botons,
        undefined,
        undefined,
        resposta => {
            if (!resposta) {
                temporalMessageShow(null, t('page.notificacio.grid.accions.ampliarTermini.noReposta'), "error");
                return;
            }
            temporalMessageShow(null, t('page.accioMassiva.accions.ampliarTermini.ok'), "success");
        },
        undefined,
        undefined,
        true,
    );


    return {
        descarregarExcel,
        descarregarJustificants,
        descarregarCertificacions,
        actualitzarEstat,
        reenviarAmbError,
        esborrarMassiu,
        reactivarConsulesCanviEstatMassiu,
        reactivarCallbacksMassiu,
        enviarNotificacionsMovilMassiu,
        marcarProcessatMassiu, marcarProcessatMassiuDialog,
        anularRemesaMassiu, anularRemesaMassiuDialog,
        ampliarTerminiMassiu, ampliarTerminiMassiuDialog
    }
}

export default AccionsMassives;
