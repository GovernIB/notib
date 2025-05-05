package es.caib.notib.plugin.gesdoc;

import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.EstatSalutEnum;
import es.caib.comanda.ms.salut.model.IntegracioPeticions;
import es.caib.notib.logic.intf.util.FitxerUtils;
import es.caib.notib.logic.intf.util.MimeUtils;
import es.caib.notib.plugin.SistemaExternException;
import es.caib.notib.plugin.utils.NotibLoggerPlugin;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Comparator;
import java.util.Properties;
import java.util.zip.ZipFile;

/**
 * Implementació del plugin de gestió documental que emmagatzema els arxius
 * a dins una carpeta del sistema de fitxers.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class GestioDocumentalPluginFilesystem implements GestioDocumentalPlugin {

	@Getter
	private static final long MAX_FILES_IN_FOLDER = 5000;

	private final Properties properties;
	private boolean configuracioEspecifica;

	private static final String AMB_ID = " amb id: ";
	private static final String ARXIU_NO_TROBAT = "No s'ha trobat l'arxiu (id=";

	private NotibLoggerPlugin logger = new NotibLoggerPlugin(log);



	public GestioDocumentalPluginFilesystem(Properties properties) {

		this.properties = properties;
		logger.setMostrarLogs(Boolean.parseBoolean(properties.getProperty("es.caib.notib.log.tipus.plugin.GESDOC")));
	}

	public GestioDocumentalPluginFilesystem(Properties properties, boolean configuracioEspecifica) {

		this.properties = properties;
		this.configuracioEspecifica = configuracioEspecifica;
		logger.setMostrarLogs(Boolean.parseBoolean(properties.getProperty("es.caib.notib.log.tipus.plugin.GESDOC")));
	}

	@Override
	public String create(String agrupacio, InputStream contingut) throws SistemaExternException {

		try (contingut) {
			agrupacio = checkAgrupacio(agrupacio);
			var basedir = getBaseDir(agrupacio);
			var subfolderId = getValidSubfolder(agrupacio);
			var id = subfolderId + generateUniqueName(basedir);
			log.info("[GESDOC] Creant fitxer al directori: " + basedir + AMB_ID + id);
			try (var outContent = new FileOutputStream(basedir + "/" + id)) {
				IOUtils.copy(contingut, outContent);
			}
			return id;
		} catch (Exception ex) {
			throw new SistemaExternException("No s'ha pogut crear l'arxiu", ex);
		}
	}

	@Override
	public void update(String id, String agrupacio, InputStream contingut) throws SistemaExternException {

		try (contingut) {
			var fContent = getFile(agrupacio, id);
			log.info("[GESDOC] Actalitzant fitxer, directori: " + getBaseDir(agrupacio) + AMB_ID + id);
			if (fContent == null) {
				throw new SistemaExternException(ARXIU_NO_TROBAT + id + ")");
			}
			try (var outContent = new FileOutputStream(fContent, false)) {
				IOUtils.copy(contingut, outContent);
			}
		} catch (Exception ex) {
			throw new SistemaExternException("No s'ha pogut actualitzar l'arxiu (id=" + id + ")", ex);
		}
	}

	@Override
	public void delete(String id, String agrupacio) throws SistemaExternException {

		try {
			var fContent = getFile(agrupacio, id);
			log.info("[GESDOC] Eliminant fitxer, directori: " + getBaseDir(agrupacio) + AMB_ID + id);
			if (fContent == null) {
				throw new SistemaExternException(ARXIU_NO_TROBAT + id + ")");
			}
			FitxerUtils.esborrar(fContent);
		} catch (Exception ex) {
			throw new SistemaExternException("No s'ha pogut esborrar l'arxiu (id=" + id + ")", ex);
		}
	}

	@Override
	public void get(String id, String agrupacio, OutputStream contingutOut, boolean isZip) throws SistemaExternException {

		try (contingutOut) {
			var fContent = getFile(agrupacio, id);
			var isAgrupacio = true;
			if (fContent == null && "notificacions".equals(agrupacio)) {
				fContent = getFile("", id);
				isAgrupacio = false;
			}
			log.info("[GESDOC] Consultant fitxer, directori: " + (isAgrupacio ? getBaseDir(agrupacio) : "") + AMB_ID + id);
			if (fContent == null) {
				throw new SistemaExternException(ARXIU_NO_TROBAT + id + ")");
			}
			try (var contingutIn = new FileInputStream(fContent)) {
				if (isZip) {
					IOUtils.copy(contingutIn, contingutOut);
					return;
				}
				var output = new ByteArrayOutputStream();
				IOUtils.copy(contingutIn, output);
				var mime = MimeUtils.getMimeTypeFromContingut(fContent.getName(), output.toByteArray());
				output.close();
				try (var contingut = "application/zip".equals(mime) ? getOutputStreamFromDocumentComprimit(fContent) : output;
					InputStream is = new ByteArrayInputStream(contingut.toByteArray())) {
					IOUtils.copy(is, contingutOut);
				}
			}
		} catch (Exception ex) {
			throw new SistemaExternException("No s'ha pogut llegir l'arxiu (id=" + id + ")", ex);
		}
	}

	private ByteArrayOutputStream getOutputStreamFromDocumentComprimit(File fitxer) {

	try {
		var zip = new ZipFile(fitxer);
		var entry = zip.entries().nextElement();
		var output = new ByteArrayOutputStream();
		IOUtils.copy(zip.getInputStream(entry), output);
		zip.close();
		return output;
	} catch (Exception e) {
		log.debug("S'ha produït un error a l'llegir el fitxer ZIP.", e);
		return new ByteArrayOutputStream();
	}
	}

	private File getFile(String agrupacio, String id) {

		agrupacio = checkAgrupacio(agrupacio);
		var basedir = getBaseDir(agrupacio);
		var fContent = new File(basedir + "/" + id);
		fContent.getParentFile().mkdirs();
		if (!fContent.exists()) {
			return null;
		}
		return fContent;
	}
	private String getValidSubfolder(String agrupacio){

		var basedir = getBaseDir(agrupacio);
		assert basedir != null;
		var ultimDirectori = ultimDirectoryModificat(basedir);
		if (ultimDirectori != null) {
			return ultimDirectori.getFileName() + "/";
		}
		// si no n'hi ha cap de valida en cream una de nova
		var subfolder = generateUniqueName(basedir);
		(new File(basedir + "/" + subfolder)).mkdir();
		return subfolder + "/";
	}

	public Path ultimDirectoryModificat(String baseDir) {

		try {
			var dir = Paths.get(baseDir);
			try (var list = Files.list(dir)) {
				if (list.count() > MAX_FILES_IN_FOLDER) {
					return null;
				}
			}
			try (var list = Files.list(dir)) {
				var lastModifiedDir = list.filter(Files::isDirectory).max(Comparator.comparingLong(p -> p.toFile().lastModified()));
				return lastModifiedDir.orElse(null);
			}
		} catch (IOException ex) {
			log.error("Error buscant ultimDirectoryModificat", ex);
			return null;
		}
	}


	private static final Instant START_INSTANT = Instant.parse("2016-01-01T00:00:00Z");
	public boolean isValidTimestamp(String timestamp) {
		try {
			// Intenta convertir la cadena de text a long
			long ts = Long.parseLong(timestamp);

			// Convertim el timestamp a un Instant
			Instant tsInstant = Instant.ofEpochMilli(ts);

			// Obtenim l'instant actual
			Instant nowInstant = Instant.now();

			// Comprovar si el timestamp està dins dels límits
			if (!tsInstant.isBefore(START_INSTANT) && !tsInstant.isAfter(nowInstant)) {
				return true;
			} else {
				return false;
			}
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private String getBaseDir(String agrupacio) {

		String baseDir = properties.getProperty("es.caib.notib.plugin.gesdoc.filesystem.base.dir");
		if (baseDir == null) {
			return null;
		}
		return baseDir.endsWith("/") ? baseDir + agrupacio : baseDir + "/" + agrupacio;
	}

	private String generateUniqueName(String basedir) {

		var id = Long.toString(System.currentTimeMillis());
		var fContent = new File( basedir + "/" + id);
		fContent.getParentFile().mkdirs();
		while (fContent.exists()) {
			id = Long.toString(System.currentTimeMillis()-1);
			fContent = new File(basedir + "/" + id);
		}
		return id;
	}

	private String checkAgrupacio(String agrupacio) {

		if (agrupacio == null || agrupacio.isEmpty()) {
			return "altres";
		}
		return agrupacio;
	}

	@Override
	public boolean teConfiguracioEspecifica() {
		return configuracioEspecifica;
	}

	@Override
	public EstatSalut getEstatPlugin() {
		return EstatSalut.builder().estat(EstatSalutEnum.UP).latencia(1).build();
	}

	@Override
	public IntegracioPeticions getPeticionsPlugin() {
		return null;
	}
}
