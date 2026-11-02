import { Badge, Icon } from '@mui/material';
import { icons } from '../utils/icons';

/**
 * Nombre de permisos d'un registre a la columna «Permisos» dels llistats (entitats, òrgans,
 * procediments, serveis): icona de clau amb el nombre a la badge, com a RIPEA. Es mostra també el 0.
 * Marge a la dreta perquè la badge sobresurt de la icona i la cel·la la retallaria.
 */
const PermisosBadge = ({ count }: { count?: number }) => (
    <Badge badgeContent={count} color="primary" showZero sx={{ mr: 1 }}>
        <Icon color="action">{icons.permisUsuari}</Icon>
    </Badge>
);

export default PermisosBadge;
