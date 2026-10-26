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

/**
 * `title` d'un MuiForm sense títol a la barra d'eines (el posa el ScreenFrame de la pantalla, <h1>).
 * Amb `title=""` la barra d'eines de la llibreria pinta igualment un <h6> buit, que l'OAW compta com a
 * encapçalament buit i com a salt de nivell (h1 -> h6). Un node buit no pinta cap element.
 */
export const FORM_NO_TITLE = (<></>) as unknown as string;
