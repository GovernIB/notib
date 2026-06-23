import React, {useEffect, useState} from "react";
import {Box, Typography} from "@mui/material";
import {useResourceApiService} from "reactlib";
import Paper from "@mui/material/Paper";
import TextField from "@mui/material/TextField";
import {useTranslation} from "react-i18next";
import {formatDate} from "../../utils/dateUtils.ts";

const RemesesErrorRegistreDialogContent: React.FC<{ id: any }> = (props) => {

    const { t } = useTranslation();
    const { id } = props;
    const {isReady: apiIsReady, find: apiFind} = useResourceApiService('eventResource');
    const [event, setEvent] = useState<any>(null);
    useEffect(() => {
        if (!apiIsReady || !id) {
            return;
        }
        const filter= "notificacio.id: " + id + " and error: true and tipus: 'REGISTRE_ENVIAMENT'";
        apiFind({ unpaged: true, filter: filter }).then(events => setEvent(events?.rows?.[0] ?? null));
    }, [apiIsReady, id]);

    const data = event?.data ? formatDate(event.data) : "-"
    return (
        <Box sx={{ minHeight: 0, p: 2 }}>
            <Paper variant="outlined" sx={{ p: 2 }}>
                <Box sx={{ textAlign: "center", mb: 3 }}>
                    <Box sx={{ display: "inline-flex", gap: 3, alignItems: "baseline", flexDirection: "column" }}>
                        <Typography>{t('page.notificacio.detail.errorRegistre.data')} - {data}</Typography>
                        <Typography>{t('page.notificacio.detail.errorRegistre.tipus')} - {event?.tipus ?? "-"}</Typography>
                    </Box>
                </Box>
                <TextField label={t('page.notificacio.detail.errorRegistre.error')} value={event?.errorDescripcio ?? ""} multiline minRows={10} fullWidth/>
            </Paper>
        </Box>
    );
}

export default RemesesErrorRegistreDialogContent;
