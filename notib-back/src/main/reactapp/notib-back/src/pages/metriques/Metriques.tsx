import {useTranslation} from "react-i18next";
import Box from "@mui/material/Box";
import React from "react";
import {MuiActionReportButton, useResourceApiService} from "reactlib";
import Paper from "@mui/material/Paper";
import Typography from "@mui/material/Typography";
import { BarChart } from '@mui/x-charts';

export function MetriquesBars({ items }) {

    const { t } = useTranslation();
    const xLabels = [ t('page.metriques.llegenda.numExecTempsMig'),
                            t('page.metriques.llegenda.tempsMigExcecuio'),
                            t('page.metriques.llegenda.tempsMaxim')];
    const uData = [1000, 2000, 300];
    let timersList = new Array();
    let maxPes = 0;
    let maxMax = 0;
    for (let timer in items?.timers) {
        let mitja = items.timers[timer].mean;
        let maxim = items.timers[timer].max;
        let num = items.timers[timer].count;
        let pes = mitja * num;
        timersList.push({name: timer, count: num, mean: mitja, max: maxim, weight: pes});
        if (pes > maxPes) {
            maxPes = pes;
        }
        if (maxim > maxMax) {
            maxMax = maxim;
        }
    }
    timersList.sort((a, b)=>  a.weight < b.weight ? 1 : a.weight > b.weight ? -1 : 0);
    const charts = [];

    let llegendaTitle = t('page.metriques.llegenda.title');
    charts.push({labels: xLabels, data: uData, title: llegendaTitle});
    for (let i in timersList) {
        let timer = timersList[i];
        let index = timer.name.lastIndexOf('.');
        let classTimer = timer.name.substring(0, index);
        let metricNameTimer = timer.name.substring(index + 1);
        let pes = Math.round(timer.weight * 100) / 100;
        let mitja = Math.round(timer.mean * 100) / 100;
        let maxim = Math.round(timer.max * 100) / 100;
        let nomSeccio = "timers-generics";
        let item = items.timers[timer.name];
        let frequencia = getFrequency(item);
        let duracio = getDuracio(item);
        let percentils = getPercentil(item);
        charts.push({
            labels: xLabels,
            data: [pes, mitja, maxim],
            title: timer.name,
            count: timer.count,
            frequencia: frequencia,
            duracio: duracio,
            percentils: percentils});
    }
    return (
        <Box sx={{ display: 'flex', flexDirection: 'column', flexWrap: 'wrap', gap: 2 }}>
            {charts.map((c, idx) => (
                <Box key={idx} sx={{ flex: '1 1 420px' }}>
                    <MetriquesBarsChart item={c} />
                </Box>
            ))}
        </Box>
    );}

function getFrequency(timerData) {
    var frequency = [];
    //El valor normalment és molt petit, millor dividir per 10000
    var m1_rate_rounded = Math.round(timerData.m1_rate * 10000) / 10000;
    var m5_rate_rounded = Math.round(timerData.m5_rate * 10000) / 10000;
    var m15_rate_rounded = Math.round(timerData.m15_rate * 10000) / 10000;
    var mean_rate_rounded = Math.round(timerData.mean_rate * 10000) / 10000;

    frequency.push(m1_rate_rounded);
    frequency.push(m5_rate_rounded);
    frequency.push(m15_rate_rounded);
    frequency.push(mean_rate_rounded);
    return frequency;
}

//Recupera la informació de la duració a mostrar
function getDuracio(timerData) {
    var duracio = [];
    var min_rounded = Math.round(timerData.min * 100) / 100;
    var mean_rounded = Math.round(timerData.mean * 100) / 100;
    var max_rounded = Math.round(timerData.max * 100) / 100;
    duracio.push(min_rounded);
    duracio.push(mean_rounded);
    duracio.push(max_rounded);
    return duracio;
}

//Recupera la informació dels percentils a mostrar
function getPercentil(timerData) {
    var percentils = [];
    var p50_rounded = Math.round(timerData.p50 * 100) / 100;
    var p75_rounded = Math.round(timerData.p75 * 100) / 100;
    var p95_rounded = Math.round(timerData.p95 * 100) / 100;
    var p98_rounded = Math.round(timerData.p98 * 100) / 100;
    var p99_rounded = Math.round(timerData.p99 * 100) / 100;
    var p999_rounded = Math.round(timerData.p999 * 100) / 100;
    percentils.push(p50_rounded);
    percentils.push(p75_rounded);
    percentils.push(p95_rounded);
    percentils.push(p98_rounded);
    percentils.push(p99_rounded);
    percentils.push(p999_rounded);
    return percentils;
}


export function MetriquesBarsChart({ item }) {

    const { t } = useTranslation();
    const safeLabels = Array.isArray(item.labels) ? item.labels : null;
    const safeData = Array.isArray(item.data) ? item.data : [];
    const yLabels = safeLabels && safeLabels.length > 0 ? safeLabels : safeData.map((_, i) => `#${i + 1}`);
    const execucionsText = t('page.metriques.excecucions');
    let titol = item.title + (item.count ? " (" + item.count + " " + execucionsText + ")" : "");
    const [open, setOpen] = React.useState(false);
    const mostrarDetallMetrica = () => setOpen(prev => !prev);
    const x1 = ["1m", "5m", "15m"];
    const x2 = ["min", "mean", "max"];
    const x3 = ["50%", "75%", "95%", "98%", "99%", "99.9%"];
    const y1 = item.frequencia;
    const y2 = item.duracio;
    const y3 = item.percentils;
    const lablel1 = t('page.metriques.frequencia');
    const lablel2 = t('page.metriques.duracio');
    const lablel3 = t('page.metriques.percentils');
    return (
        <Box sx={{ width: "100%" }}>
            <Paper sx={{ p: 2 }}>
                <Typography variant="h6" sx={{ mb: 1 }}>{titol} </Typography>
                <Box sx={{ width: '100%', height: 200, overflow: 'visible', cursor: 'pointer' }} onClick={mostrarDetallMetrica}>
                    <BarChart
                        sx={{ '& .MuiBarChart-label': { fill: '#767b83' } }}
                        layout="horizontal"
                        height={200}
                        margin={{ left: 300 }}
                        series={[{ data: safeData, label: '', barLabel: ({ value }) => `${value}` }]}
                        hideLegend
                        xAxis={[{scaleType: 'linear', disableLine: true, disableTicks: true, tickLabelStyle: { display: 'none' }}]}
                        yAxis={[{
                            scaleType: 'band',
                            data: yLabels,
                            disableLine: true,
                            disableTicks: true,
                            tickLabelStyle: { display: 'auto' },
                            colorMap: { type: 'ordinal', values: yLabels, colors: ['#aabee0', '#b5d69d', '#edc194'] },
                        }]}
                    />
                </Box>
            </Paper>
            {open && (
                <Paper sx={{ p: 2, display:'flex' }}>
                    <Box sx={{ width: '100%', height: 250, overflow: 'visible', cursor: 'pointer' }}>
                        <BarChart
                            series={[{ data: y1, label: lablel1, id: item.title + lablel1, stack: 'total' },]}
                            xAxis={[{ data: x1, height: 28 }]}
                            yAxis={[{ width: 50 }]}
                        />
                    </Box>
                    <Box sx={{ width: '100%', height: 250, overflow: 'visible', cursor: 'pointer' }}>
                        <BarChart
                            series={[{ data: y2, label: lablel2, id: item.title + lablel2, stack: 'total', color: "#99afd6" },]}
                            xAxis={[{ data: x2, height: 28 }]}
                            yAxis={[{ width: 50 }]}
                        />
                    </Box>
                    <Box sx={{ width: '100%', height: 250, overflow: 'visible', cursor: 'pointer' }}>
                        <BarChart
                            series={[{ data: y3, label: lablel3, id: item.title + lablel3, stack: 'total', color: "#ff6f61" },]}
                            xAxis={[{ data: x3, height: 28 }]}
                            yAxis={[{ width: 50 }]}
                        />
                    </Box>
                </Paper>
            )}
        </Box>
    );
}

export const Metriques = () => {

    const { t } = useTranslation();
    const {isReady: apiIsReady, getOne: apiGetOne} = useResourceApiService("metriquesResource");
    const [metriques, setMetriques] = React.useState(null);

    React.useEffect(() => {
        if (!apiIsReady) {
            return;
        }
        apiGetOne("metriques").then((resposta) => setMetriques(JSON.parse(resposta?.metriques)));
    }, [apiIsReady, apiGetOne]);
    return (
        <Box>
            {t("page.metriques.title")}
            <Box sx={{ textAlign: 'right' }}>
                <MuiActionReportButton
                    resourceName={"metriquesResource"}
                    report="DESCARREGAR_METRIQUES_JSON"
                    reportFileType="CUSTOM"
                    title={t('page.metriques.exporta')}
                    buttonComponentProps={{ variant: 'outlined', sx: { mr: 1 } }}
                    buttonIcon="file_download"/>
            </Box>
            <MetriquesBars items={metriques} />
        </Box>
    );
};

export default Metriques;
