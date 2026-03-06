import React from 'react';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import { useTheme } from '@mui/material/styles';

const DataCard: React.FC<{ title: string; data: Record<string, string>[] } & any> = (props) => {
    const { title, data, sx, ...otherProps } = props;
    const theme = useTheme();
    const bgColor =
        theme.palette.mode === 'light' ? theme.palette.grey[200] : theme.palette.grey[900];
    return (
        <Card {...otherProps} sx={{ '& .MuiCardContent-root ': { padding: 0 }, ...sx }}>
            <CardContent>
                <Table aria-label="simple table">
                    <TableHead>
                        <TableRow>
                            <TableCell
                                colSpan={2}
                                sx={{ fontSize: 18, backgroundColor: bgColor, py: 1.5 }}
                            >
                                {title}
                            </TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {Object.entries(data).map(([key, value]) => (
                            <TableRow key={key}>
                                <TableCell
                                    component="th"
                                    scope="row"
                                    sx={{
                                        borderRight: 1,
                                        borderColor: 'divider',
                                        fontWeight: 500,
                                        py: 1.5,
                                    }}
                                >
                                    {Object.keys(value as any)[0]}
                                </TableCell>
                                <TableCell
                                    component="th"
                                    scope="row"
                                    sx={{
                                        py: 1.5,
                                    }}
                                >
                                    {Object.values(value as any)[0] as string}
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </CardContent>
        </Card>
    );
};

export default DataCard;
