import {DataGridPro, GridColDef} from "@mui/x-data-grid-pro";
import {useResourceApiService} from "reactlib";
import React from "react";
import Typography from "@mui/material/Typography";
import {useTranslation} from "react-i18next";
import Box from "@mui/material/Box";
import IconButton from "@mui/material/IconButton";
import Icon from "@mui/material/Icon";
import MailOutlineIcon from "@mui/icons-material/MailOutline";

const COLUMN_HEADER_HEIGHT = 56;
const ROW_HEIGHT = 52;

type PermisRow = {
    id: string;              // grid row id (unique)
    procedimentCodi: string; // the map key
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
    // add whatever PermisDto flags your columns need
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

    React.useEffect(() => {
        if (!apiIsReady) {
            return;
        }

        apiAction(undefined, { code: "GET_PERMISOS_USUARI", data: { codi: id } })
            .then(permisos => {
                let raw = permisos?.permisosOrgans;
                let map: Record<string, any[]> = typeof raw === "string" ? (raw ? JSON.parse(raw) : {}) : (raw ?? {});
                let rows: PermisRow[] = crearRows(map);
                setOrgansRows(rows);
                raw = permisos?.permisosProcediment;
                map = typeof raw === "string" ? (raw ? JSON.parse(raw) : {}) : (raw ?? {});
                rows = crearRows(map);
                setProcedimentRows(rows);
                raw = permisos?.organsFills;
                map = typeof raw === "string" ? (raw ? JSON.parse(raw) : {}) : (raw ?? {});
                rows = crearRows(map);
                setOrgansFillsRows(rows);
            })
            .catch((error) => { console.error(error); });
    }, [apiIsReady, apiAction, id]);

    const boolIcon = (params: any) =>
        params.value ? <Icon color="success">check</Icon> : null;

    const columns: GridColDef[] = [
        { field: "organNom", headerName: t('page.usuaris.permisos.grid.columnes.nom'), flex: 1 },
        { field: "tipus", headerName: t('page.usuaris.permisos.grid.columnes.tipus'), flex: 1 },
        { field: "principal", headerName: t('page.usuaris.permisos.grid.columnes.principal'), flex: 1 },
        { field: "administrador", headerName: "", width: 60, align: "center", renderHeader: () => <Icon>person_add_alt_1</Icon>, renderCell: boolIcon },
        { field: "read", headerName: "", width: 60, align: "center", renderHeader: () => <Icon>search</Icon>, renderCell: boolIcon },
        { field: "processar", headerName: "", width: 60, align: "center", renderHeader: () => <Icon>check_box</Icon>, renderCell: boolIcon },
        { field: "administration", headerName: "", width: 60, align: "center", renderHeader: () => <Icon>settings</Icon>, renderCell: boolIcon },
        { field: "comuns", headerName: "", width: 60, align: "center", renderHeader: () => <Icon>public</Icon>, renderCell: boolIcon },
        { field: "notificacio", headerName: "", width: 60, align: "center", renderHeader: () => <Icon>gavel</Icon>, renderCell: boolIcon },
        { field: "comunicacio", headerName: "", width: 60, align: "center", renderHeader: () => <MailOutlineIcon/>, renderCell: boolIcon },
        { field: "comunicacioSir", headerName: "", width: 60, align: "center", renderHeader: () => <Icon>email</Icon>, renderCell: boolIcon },
        { field: "comunicacioSenseProcediment", headerName: "", width: 60, align: "center", renderHeader: () => <Icon>send</Icon>, renderCell: boolIcon },
    ];
    //
    // select DISTINCT nac.ID,  nac.CLASS, entry.sid, entry.ACL_OBJECT_IDENTITY
    // from NOT_ACL_OBJECT_IDENTITY oi JOIN NOT_acl_entry entry ON oi.Id = entry.ACL_OBJECT_IDENTITY
    // JOIN not_acl_class nac ON nac.id = oi.object_Id_class
    // WHERE entry.sid in (1324)
    // and nac.class = 'es.caib.notib.persist.entity.OrganGestorEntity'
    // and  entry.mask in (2048)
    // and  entry.granting = 1

    const [showOrganPermisHeredat, setOrganPermisHeredat] = React.useState(false);
    const [showProcedimentPermisOrgan, setProcedimentPermisOrgan] = React.useState(false);

    return (
        <Box sx={{ mt:5, mb:5 }}>
            <Typography variant="subtitle1" sx={{ mb: 1, ml:2 }}>{t('page.usuaris.permisos.grid.organsPermisDirecte')}</Typography>
            <DataGridPro rows={organsRows}
                         columns={columns}
                         pagination={false}
                         hideFooter
                         columnHeaderHeight={COLUMN_HEADER_HEIGHT}
                         rowHeight={ROW_HEIGHT}
                         className="permisos-detail-grid"
                         sx={{
                             height: COLUMN_HEADER_HEIGHT + procedimentRows.length * ROW_HEIGHT,
                             '& .MuiDataGrid-row:not(:first-of-type)': {
                                 border: 'none',
                             }
                        }} />

            <Box sx={{mb: 5}}>
                <Typography variant="subtitle1" sx={{ mt: 10, mb: 1, ml:2 }}>{t('page.usuaris.permisos.grid.organsPermisHeredat')}
                    <IconButton onClick={() => setOrganPermisHeredat((v) => !v)}>
                        <Icon>{showOrganPermisHeredat ? 'expand_less' : 'expand_more'}</Icon>
                    </IconButton>
                </Typography>

                {showOrganPermisHeredat && (
                    <DataGridPro rows={organsFillsRows}
                                 columns={columns}
                                 pagination={false}
                                 hideFooter
                                 columnHeaderHeight={COLUMN_HEADER_HEIGHT}
                                 rowHeight={ROW_HEIGHT}
                                 className="permisos-detail-grid"
                                 sx={{
                                     height: COLUMN_HEADER_HEIGHT + procedimentRows.length * ROW_HEIGHT,
                                     '& .MuiDataGrid-row:not(:first-of-type)': {
                                         border: 'none',
                                     }
                                 }} />
                )}
            </Box>

            <Box sx={{mb: 5}}>
                <Typography variant="subtitle1" sx={{ mb: 1, ml:2 }}>{t('page.usuaris.permisos.grid.procedimentPermisDirecte')}</Typography>
                <DataGridPro rows={procedimentRows}
                             columns={columns}
                             pagination={false}
                             hideFooter
                             columnHeaderHeight={COLUMN_HEADER_HEIGHT}
                             rowHeight={ROW_HEIGHT}
                             className="permisos-detail-grid"
                             sx={{
                                 height: COLUMN_HEADER_HEIGHT + procedimentRows.length * ROW_HEIGHT,
                                 '& .MuiDataGrid-row:not(:first-of-type)': {
                                     border: 'none',
                                 }
                             }} />

                <Typography variant="subtitle1" sx={{ mt: 10, mb: 1, ml:2 }}>{t('page.usuaris.permisos.grid.procedimentPermisOrgan')}
                    <IconButton onClick={() => setProcedimentPermisOrgan((v) => !v)}>
                        <Icon>{showProcedimentPermisOrgan ? 'expand_less' : 'expand_more'}</Icon>
                    </IconButton>
                </Typography>
            </Box>

            <Box sx={{mb: 7}}>
                {showProcedimentPermisOrgan && (
                    <DataGridPro rows={procedimentRows}
                                 columns={columns}
                                 pagination={false}
                                 hideFooter
                                 columnHeaderHeight={COLUMN_HEADER_HEIGHT}
                                 rowHeight={ROW_HEIGHT}
                                 className="permisos-detail-grid"
                                 sx={{
                                     height: COLUMN_HEADER_HEIGHT + procedimentRows.length * ROW_HEIGHT,
                                     '& .MuiDataGrid-row:not(:first-of-type)': {
                                         border: 'none',
                                     }
                                 }} />
                )}
            </Box>

        </Box>)
}

export default PermisosUsuariDetail;
