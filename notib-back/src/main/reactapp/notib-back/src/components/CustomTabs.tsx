import React from 'react';
import Box from '@mui/material/Box';
import Tabs from '@mui/material/Tabs';
import Tab from '@mui/material/Tab';

interface TabPanelProps {
    children?: React.ReactNode;
    index: number;
    value: number;
}

const a11yProps = (index: number) => {
    return {
        id: `simple-tab-${index}`,
        'aria-controls': `simple-tabpanel-${index}`,
    };
};

const CustomTabPanel = (props: TabPanelProps) => {
    const { children, index, value, ...other } = props;
    return (
        <div
            role="tabpanel"
            hidden={value !== index}
            id={`simple-tabpanel-${index}`}
            aria-labelledby={`simple-tab-${index}`}
            style={{ height: '100%', minHeight: 0 }}
            {...other}
        >
            {value === index && <Box sx={{ pt: 3, height: '100%' }}>{children}</Box>}
        </div>
    );
};

const CustomTabs: React.FC<{ tabs: string[]; contents: React.ReactElement[] }> = (props) => {
    const { tabs, contents } = props;
    const [value, setValue] = React.useState<number>(0);
    const handleChange = (_event: React.SyntheticEvent, newValue: number) => {
        setValue(newValue);
    };
    return (
        <Box sx={{ width: '100%', height: '100%', display: 'flex', flexDirection: 'column' }}>
            <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
                <Tabs value={value} onChange={handleChange} aria-label="basic tabs example">
                    {tabs.map((t, i) => (
                        <Tab key={i} label={t} {...a11yProps(i)} />
                    ))}
                </Tabs>
            </Box>
            {contents.map((c, i) => (
                <CustomTabPanel key={i} index={i} value={value}>
                    {c}
                </CustomTabPanel>
            ))}
        </Box>
    );
};

export default CustomTabs;
