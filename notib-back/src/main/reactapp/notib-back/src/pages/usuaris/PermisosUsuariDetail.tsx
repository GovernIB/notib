import {DataGridPro, GridColDef} from "@mui/x-data-grid-pro";
import {useResourceApiService} from "reactlib";
import React from "react";
import Typography from "@mui/material/Typography";
import {useTranslation} from "react-i18next";
import Box from "@mui/material/Box";
import IconButton from "@mui/material/IconButton";
import Icon from "@mui/material/Icon";
import Tooltip from "@mui/material/Tooltip";
import MailOutlineIcon from "@mui/icons-material/MailOutline";
import {exportarGridToExcel, gridRegistry} from "../../utils/gridToExcel.tsx";
import Button from "@mui/material/Button";

const COLUMN_HEADER_HEIGHT = 56;
const ROW_HEIGHT = 52;

type PermisRow = {
    id: string;
    procedimentCodi: string;
    principal: string;
    organNom?: string;
    tipus?: string;
    read: boolean;
    write: boolean;
    notificacio: boolean;
    comunicacio: boolean;
    comunicacioSir: boolean;
    comuns: boolean;
    administration: boolean;
    administrador: boolean;
    processar: boolean;
    comunicacioSenseProcediment: boolean;
    fill?: string;
};

type ProcSerOrganRow = {
    id: string;
    codiValor: string;
    organGestorNom: string;
    tipus: string;
    principal: string;
    administrador: boolean;
    read: boolean;
    processar: boolean;
    administration: boolean;
    comuns: boolean;
    notificacio: boolean;
    comunicacio: boolean;
    comunicacioSir: boolean;
    comunicacioSenseProcediment: boolean;
};

const crearRowsOrgansFills = (
    fillsMap: Record<string, string[]>,     // organsFills
    permisMap: Record<string, any[] | any>  // organsMap (procedimentCodi -> permis list or obj list)
): PermisRow[] => {
    const rows: PermisRow[] = [];
    for (const [procedimentCodi, fills] of Object.entries(fillsMap ?? {})) {
        if (!Array.isArray(fills) || fills.length === 0) {
            continue;
        }
        const permisArr = permisMap?.[procedimentCodi];
        const permisList = Array.isArray(permisArr) ? permisArr : permisArr ? [permisArr] : [];
        if (permisList.length === 0) {
            continue;
        }
        // Un mateix òrgan pot tenir més d'un permís assignat (un a l'usuari i un o més
        // als seus rols/grups): cal reproduir-los TOTS per a cada òrgan fill, no només
        // el primer, o es perden els permisos que no vengui del primer principal.
        for (const [fillIndex, fill] of fills.entries()) {
            for (const [permisIndex, permis] of permisList.entries()) {
                rows.push({
                    id: `${procedimentCodi}-${fill}-${fillIndex}-${permisIndex}`,
                    procedimentCodi,
                    fill,
                    organNom: fill,
                    tipus: permis.tipus,
                    principal: permis.principal,

                    read: !!permis.read,
                    write: !!permis.write,
                    notificacio: !!permis.notificacio,
                    comunicacio: !!permis.comunicacio,
                    comunicacioSir: !!permis.comunicacioSir,
                    comuns: !!permis.comuns,
                    administration: !!permis.administration,
                    administrador: !!permis.administrador,
                    processar: !!permis.processar,
                    comunicacioSenseProcediment: !!permis.comunicacioSenseProcediment,
                });
            }
        }
    }
    return rows;
};


const crearRowsProcSerOrgan = (procSerOrgan: any[] | undefined | null): ProcSerOrganRow[] => {

    const list = Array.isArray(procSerOrgan) ? procSerOrgan : [];
    return list.map((procSer, index) => {
        const p = procSer?.permis ?? {};
        const cv = procSer?.codiValor ?? {};
        return {
            id: `${cv?.id ?? p?.id ?? "x"}-${index}`,
            codiValor: cv?.valor ?? "",
            organGestorNom: `${cv?.organGestor ?? ""} - ${cv?.organNom ?? ""}`.trim(),

            tipus: p?.tipus ?? "",
            principal: p?.principal ?? "",

            administrador: !!p?.administrador,
            read: !!p?.read,
            processar: !!p?.processar,
            administration: !!p?.administration,

            comuns: !!p?.comuns,
            notificacio: !!p?.notificacio,
            comunicacio: !!p?.comunicacio,
            comunicacioSir: !!p?.comunicacioSir,
            comunicacioSenseProcediment: !!p?.comunicacioSenseProcediment,
        };
    });
};


const crearRows = (map : Record<string, any[]>) => {
    return Object.entries(map).flatMap(([procedimentCodi, permisList]) =>
        (permisList ?? []).map((p, index) => ({
            id: `${procedimentCodi}-${p.id ?? index}`, // composite → guaranteed unique
            procedimentCodi,
            principal: p.principal,
            organNom: p.organNom,
            tipus: p.tipus,
            read: p.read,
            write: p.write,
            notificacio: p.notificacio,
            comunicacio: p.comunicacio,
            comunicacioSir: p.comunicacioSir,
            comuns: p.comuns,
            administration: p.administration,
            administrador: p.administrador,
            processar: p.processar,
            comunicacioSenseProcediment: p.comunicacioSenseProcediment,
        }))
    );
}

export const PermisosUsuariDetail: React.FC<{id: any}> = (props) => {

    const { id } = props;
    const { t } = useTranslation();
    const { isReady: apiIsReady, artifactAction: apiAction } = useResourceApiService('usuariPermisResource');
    const [organsRows, setOrgansRows] = React.useState<PermisRow[]>([]);
    const [organsFillsRows, setOrgansFillsRows] = React.useState<PermisRow[]>([]);
    const [procedimentRows, setProcedimentRows] = React.useState<PermisRow[]>([]);
    const [procSerOrganRows, setProcSerOrganRows] = React.useState<ProcSerOrganRow[]>([]);

    React.useEffect(() => {
        if (!apiIsReady) {
            return;
        }
        const parseMap = (raw: any): Record<string, any[]> =>
            typeof raw === "string" ? (raw ? JSON.parse(raw) : {}) : (raw ?? {});
        apiAction(undefined, { code: "GET_PERMISOS_USUARI", data: { codi: id } })
            .then(permisos => {
                const permisosOrgansMap = parseMap(permisos?.permisosOrgans);
                setOrgansRows(crearRows(permisosOrgansMap));
                const permisosProcedimentMap = parseMap(permisos?.permisosProcediment);
                setProcedimentRows(crearRows(permisosProcedimentMap));
                const organsFillsMap = parseMap(permisos?.organsFills);
                // crearRowsOrgansFills necessita el mapa d'òrgan -> fills (organsFillsMap) i, per a
                // cada fill, els permisos de l'òrgan del qual hereta (permisosOrgansMap) -no el mateix
                // mapa d'òrgan -> fills ni, molt menys, sense parsejar (un JSON encara en format text).
                setOrgansFillsRows(crearRowsOrgansFills(organsFillsMap, permisosOrgansMap));
                setProcSerOrganRows(crearRowsProcSerOrgan(permisos?.procSerOrgan));
            })
            .catch((error) => { console.error(error); });
    }, [apiIsReady, apiAction, id]);

    const boolIcon = (params: any) => params.value ? <Icon color="success">check</Icon> : null;

    const permisHeader = (icon: React.ReactElement, tooltipKey: string) => () => (
        <Tooltip title={t(`page.organs.form.permisos.${tooltipKey}Tooltip`)} arrow placement="top">
            {icon}
        </Tooltip>
    );

    const permisColumnBase = { width: 76, align: "center" as const, sortable: false, disableColumnMenu: true, disableReorder: true };

    const permisColumns: GridColDef[] = [
        { ...permisColumnBase, field: "administrador", headerName: "", renderHeader: permisHeader(<Icon color="action">person_add_alt_1</Icon>, 'administrador'), renderCell: boolIcon },
        { ...permisColumnBase, field: "read", headerName: "", renderHeader: permisHeader(<Icon color="action">search</Icon>, 'consulta'), renderCell: boolIcon },
        { ...permisColumnBase, field: "processar", headerName: "", renderHeader: permisHeader(<Icon color="action">check_box</Icon>, 'processar'), renderCell: boolIcon },
        { ...permisColumnBase, field: "administration", headerName: "", renderHeader: permisHeader(<Icon color="action">settings</Icon>, 'gestio'), renderCell: boolIcon },
        { ...permisColumnBase, field: "comuns", headerName: "", renderHeader: permisHeader(<Icon color="action">public</Icon>, 'comuns'), renderCell: boolIcon },
        { ...permisColumnBase, field: "notificacio", headerName: "", renderHeader: permisHeader(<Icon color="action">gavel</Icon>, 'notificacions'), renderCell: boolIcon },
        { ...permisColumnBase, field: "comunicacio", headerName: "", renderHeader: permisHeader(<MailOutlineIcon color="action" />, 'comunicacions'), renderCell: boolIcon },
        { ...permisColumnBase, field: "comunicacioSir", headerName: "", renderHeader: permisHeader(<Icon color="action">email</Icon>, 'sir'), renderCell: boolIcon },
        { ...permisColumnBase, field: "comunicacioSenseProcediment", headerName: "", renderHeader: permisHeader(<Icon color="action">send</Icon>, 'comSenseProc'), renderCell: boolIcon },
    ];

    const columns: GridColDef[] = [
        { field: "organNom", headerName: t('page.usuaris.permisos.grid.columnes.nom'), flex: 1 },
        { field: "organGestor", headerName: t('page.usuaris.permisos.grid.columnes.organGestor'), flex: 1 },
        { field: "tipus", headerName: t('page.usuaris.permisos.grid.columnes.tipus'), flex: 1 },
        { field: "principal", headerName: t('page.usuaris.permisos.grid.columnes.principal'), flex: 1 },
        ...permisColumns,
    ];

    const columnsProcSerOrgan: GridColDef[] = [
        { field: "codiValor", headerName: t("page.usuaris.permisos.grid.columnes.nom"), flex: 1 },
        { field: "organGestorNom", headerName: t("page.usuaris.permisos.grid.columnes.organGestor"), flex: 1 },
        { field: "tipus", headerName: t("page.usuaris.permisos.grid.columnes.tipus"), flex: 1 },
        { field: "principal", headerName: t("page.usuaris.permisos.grid.columnes.principal"), flex: 1 },
        ...permisColumns,
    ];

    React.useEffect(() => {
        gridRegistry.set(`${id}-organs`, { columns, rows: organsRows });
        return () => { gridRegistry.delete(`${id}-organs`); };
    }, [id, organsRows, columns])

    React.useEffect(() => {
        gridRegistry.set(`${id}-organsFills`, { columns, rows: organsFillsRows });
        return () => { gridRegistry.delete(`${id}-organsFills`);}
    }, [id, organsFillsRows, columns]);

    React.useEffect(() => {
        gridRegistry.set(`${id}-procediment`, { columns, rows: procedimentRows });
        return () => { gridRegistry.delete(`${id}-procediment`);}
    }, [id, procedimentRows, columns]);

    React.useEffect(() => {
        gridRegistry.set(`${id}-procSerOrgan`, { columns: columnsProcSerOrgan, rows: procSerOrganRows });
        return () => { gridRegistry.delete(`${id}-procSerOrgan`);}
    }, [id, procSerOrganRows, columnsProcSerOrgan]);

    const [showOrganPermisHeredat, setOrganPermisHeredat] = React.useState(false);
    const [showProcedimentPermisOrgan, setProcedimentPermisOrgan] = React.useState(false);

    const detailBoxSx = {
        ml: 3,
        mt: 1,
        mb: 2,
        pl: 2,
        py: 0.5,
        borderLeft: '3px solid',
        borderColor: 'primary.light',
        borderRadius: 1,
    };

    return (
        <Box sx={{
            m: 2,
            p: 3,
            border: '1px solid',
            borderColor: 'divider',
            borderRadius: 2,
            bgcolor: 'background.paper',
            boxShadow: 1,
        }}>
            <Box sx={{ display: "flex", justifyContent: "flex-end", width: "100%" }}>
                <Button variant="contained"
                    onClick={() => {
                        exportarGridToExcel([`${String(id)}-organs`, `${String(id)}-organsFills`, `${String(id)}-procediment`, `${String(id)}-procSerOrgan`],
                                    "permisos_usuari_" + id + ".xlsx");
                    }}>
                    Exportar
                </Button>
            </Box>
            <Typography variant="subtitle1" sx={{ mb: 1, ml:2 }}>{t('page.usuaris.permisos.grid.organsPermisDirecte')}</Typography>
            <DataGridPro rows={organsRows}
                         columns={columns}
                         pagination={false}
                         hideFooter
                         columnHeaderHeight={COLUMN_HEADER_HEIGHT}
                         rowHeight={ROW_HEIGHT}
                         className="permisos-detail-grid"
                         columnVisibilityModel={{organGestor: false}}
                         sx={{
                             height: COLUMN_HEADER_HEIGHT + organsRows.length * ROW_HEIGHT,
                             '& .MuiDataGrid-row:not(:first-of-type)': {
                                 border: 'none',
                             }
                        }} />

            <Box sx={{ mt: 2, mb: 2 }}>
                <Typography variant="subtitle2" sx={{ ml:2, display: 'flex', alignItems: 'center', color: 'text.secondary' }}>
                    {t('page.usuaris.permisos.grid.organsPermisHeredat')}
                    <IconButton size="small" onClick={() => setOrganPermisHeredat((v) => !v)}>
                        <Icon fontSize="small">{showOrganPermisHeredat ? 'expand_less' : 'expand_more'}</Icon>
                    </IconButton>
                </Typography>

                {showOrganPermisHeredat && (
                    <Box sx={detailBoxSx}>
                        <DataGridPro rows={organsFillsRows}
                                     columns={columns}
                                     pagination={false}
                                     hideFooter
                                     columnHeaderHeight={COLUMN_HEADER_HEIGHT}
                                     rowHeight={ROW_HEIGHT}
                                     className="permisos-detail-grid"
                                     sx={{
                                         height: COLUMN_HEADER_HEIGHT + organsFillsRows.length * ROW_HEIGHT,
                                         bgcolor: 'background.paper',
                                         '& .MuiDataGrid-row:not(:first-of-type)': {
                                             border: 'none',
                                         }
                                     }} />
                    </Box>
                )}
            </Box>

            <Box sx={{ mt: 4, mb: 2 }}>
                <Typography variant="subtitle1" sx={{ mb: 1, ml:2 }}>{t('page.usuaris.permisos.grid.procedimentPermisDirecte')}</Typography>
                <DataGridPro rows={procedimentRows}
                             columns={columns}
                             pagination={false}
                             hideFooter
                             columnHeaderHeight={COLUMN_HEADER_HEIGHT}
                             rowHeight={ROW_HEIGHT}
                             className="permisos-detail-grid"
                             columnVisibilityModel={{organGestor: false}}
                             sx={{
                                 height: COLUMN_HEADER_HEIGHT + procedimentRows.length * ROW_HEIGHT,
                                 '& .MuiDataGrid-row:not(:first-of-type)': {
                                     border: 'none',
                                 }
                             }} />
            </Box>

            <Box sx={{ mt: 2, mb: 2 }}>
                <Typography variant="subtitle2" sx={{ ml:2, display: 'flex', alignItems: 'center', color: 'text.secondary' }}>
                    {t('page.usuaris.permisos.grid.procedimentPermisOrgan')}
                    <IconButton size="small" onClick={() => setProcedimentPermisOrgan((v) => !v)}>
                        <Icon fontSize="small">{showProcedimentPermisOrgan ? 'expand_less' : 'expand_more'}</Icon>
                    </IconButton>
                </Typography>

                {showProcedimentPermisOrgan && (
                    <Box sx={detailBoxSx}>
                        <DataGridPro
                            rows={procSerOrganRows}
                            columns={columnsProcSerOrgan}
                            pagination={false}
                            hideFooter
                            columnHeaderHeight={COLUMN_HEADER_HEIGHT}
                            rowHeight={ROW_HEIGHT}
                            className="permisos-detail-grid"
                            sx={{
                                height: COLUMN_HEADER_HEIGHT + procSerOrganRows.length * ROW_HEIGHT,
                                bgcolor: 'background.paper',
                                '& .MuiDataGrid-row:not(:first-of-type)': { border: 'none' },
                            }}
                        />
                    </Box>
                )}
            </Box>

        </Box>)
}

export default PermisosUsuariDetail;
