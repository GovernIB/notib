import i18next from 'i18next';
import { initReactI18next } from 'react-i18next';
import LanguageDetector from 'i18next-browser-languagedetector';
import translationCa from './translationCa';
import translationEs from './translationEs';

const resources = {
    ca: { translation: translationCa },
    es: { translation: translationEs },
};

i18next.use(LanguageDetector).use(initReactI18next).init({
    resources,
    fallbackLng: 'ca',
    supportedLngs: ['ca', 'es'],
    nonExplicitSupportedLngs: true,
    load: 'languageOnly',
    detection: {
        order: ['querystring', 'cookie', 'localStorage', 'sessionStorage', 'htmlTag'],
        lookupQuerystring: 'lang',
        caches: ['cookie', 'localStorage'],
    },
    interpolation: { escapeValue: false },
});

const syncUtilityNav = (language?: string) => {
    const base = (import.meta.env.BASE_URL || '/').replace(/\/$/, '');
    const lng = (language ?? '').split('-')[0].toLowerCase();
    const resolved = SUPPORTED.has(lng) ? lng : 'ca';
    document.querySelectorAll<HTMLElement>('[data-i18n]').forEach((el) => {
        const value = i18next.t(el.dataset.i18n!, { lng: resolved });
        if (value !== el.dataset.i18n) el.textContent = value;
    });
    document.querySelectorAll<HTMLElement>('[data-i18n-aria-label]').forEach((el) => {
        el.setAttribute('aria-label', i18next.t(el.dataset.i18nAriaLabel!, { lng: resolved }));
    });
    document.querySelectorAll<HTMLAnchorElement>('#oawUtilityNav a[data-route]').forEach((a) => {
        a.href = `${base}/${a.dataset.route}`;
    });
};

const SUPPORTED = new Set(['ca', 'es']);
const syncDocumentLanguage = (language?: string) => {
    const base = (language ?? '').split('-')[0].toLowerCase();
    // Never publish an unsupported value: a wrong `lang` is worse than `ca`.
    document.documentElement.setAttribute('lang', SUPPORTED.has(base) ? base : 'ca');
};

i18next.on('languageChanged', (l) => { syncDocumentLanguage(l); syncUtilityNav(l); });
i18next.on('initialized', () => { syncDocumentLanguage(i18next.resolvedLanguage); syncUtilityNav(i18next.resolvedLanguage); });
syncDocumentLanguage(i18next.resolvedLanguage);

export default i18next;
