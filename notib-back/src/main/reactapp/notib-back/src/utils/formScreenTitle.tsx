import ScreenTitle from '../components/ScreenTitle';
import type { IconName } from './icons';

/**
 * Títol de pantalla (ScreenTitle) per al `title` d'un MuiForm de la llibreria.
 *
 * MuiForm el tipa com a `string`, però només el passa a la seva barra d'eines
 * (lib/components/mui/Toolbar.tsx), que accepta qualsevol node; el component Form no en fa cap altre
 * ús. Com que la llibreria no es pot tocar, la conversió de tipus es fa aquí, en un sol lloc.
 */
export const formScreenTitle = (icon: IconName, title: string): string =>
    (<ScreenTitle icon={icon} title={title} />) as unknown as string;
