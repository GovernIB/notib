package es.caib.notib.plugin.gesdoc;

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
import java.io.InputStream;
import java.io.OutputStream;
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

	private static final String AMB_ID = " amb id: ";
	private static final String ARXIU_NO_TROBAT = "No s'ha trobat l'arxiu (id=";

	private NotibLoggerPlugin logger = new NotibLoggerPlugin(log);

	public GestioDocumentalPluginFilesystem(Properties properties) {

		this.properties = properties;
		logger.setMostrarLogs(Boolean.parseBoolean(properties.getProperty("es.caib.notib.log.tipus.plugin.GESDOC")));
	}

	@Override
	public String create(String agrupacio, InputStream contingut) throws SistemaExternException {

		try {
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

		try {
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
			logger.info("[GESDOC] Eliminant fitxer, directori: " + getBaseDir(agrupacio) + AMB_ID + id);
			if (fContent == null) {
				throw new SistemaExternException(ARXIU_NO_TROBAT + id + ")");
			}
			FitxerUtils.esborrar(fContent);
		} catch (Exception ex) {
			throw new SistemaExternException("No s'ha pogut esborrar l'arxiu (id=" + id + ")", ex);
		}
	}

	@Override
	public void get(String id, String agrupacio, OutputStream contingutOut) throws SistemaExternException {

		try {
			var fContent = getFile(agrupacio, id);
			var isAgrupacio = true;
			if (fContent == null && "notificacions".equals(agrupacio)) {
				fContent = getFile("", id);
				isAgrupacio = false;
			}
			logger.info("[GESDOC] Consultant fitxer, directori: " + (isAgrupacio ? getBaseDir(agrupacio) : "") + AMB_ID + id);
			if (fContent == null) {
				throw new SistemaExternException(ARXIU_NO_TROBAT + id + ")");
			}
			try (var contingutIn = new FileInputStream(fContent)) {
				var output = new ByteArrayOutputStream();
				IOUtils.copy(contingutIn, output);
				var mime = MimeUtils.getMimeTypeFromContingut(fContent.getName(), output.toByteArray());
				output.close();
				var contingut = "application/zip".equals(mime) ? getOutputStreamFromDocumentComprimit(fContent) : output;
				InputStream is = new ByteArrayInputStream(contingut.toByteArray());
				IOUtils.copy(is, contingutOut);
				is.close();
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
		var file = new File(basedir);
		File[] directories = file.listFiles((current, name) -> {
			var f = new File(current, name);
			if (!f.isDirectory()) {
				return false;
			}
			var files = f.list();
			return files == null || files.length < MAX_FILES_IN_FOLDER;
		});
		if (directories != null && directories.length > 0){
			return directories[0].getName() + "/";
		}

		// si no n'hi ha cap de valida en cream una de nova
		var subfolder = generateUniqueName(basedir);
		(new File(basedir + "/" + subfolder)).mkdir();
		return subfolder + "/";
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

}
