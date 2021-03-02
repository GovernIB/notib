/**
 * 
 */
package es.caib.notib.plugin.gesdoc;

import es.caib.notib.plugin.SistemaExternException;
import es.caib.notib.plugin.utils.PropertiesHelper;
import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * Implementació del plugin de gestió documental que emmagatzema els arxius
 * a dins una carpeta del sistema de fitxers.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class GestioDocumentalPluginFilesystem implements GestioDocumentalPlugin {

	private static final long MAX_FILES_IN_FOLDER = 5000;

	@Override
	public String create(
			String agrupacio,
			InputStream contingut) throws SistemaExternException {
		try {
			String basedir = getBaseDir(agrupacio);
			String subfolderId = getValidSubfolder(agrupacio);
			String id = subfolderId + generateUniqueName(basedir);
			FileOutputStream outContent = new FileOutputStream(basedir + "/" + id);
			IOUtils.copy(contingut, outContent);
			outContent.close();
			return id;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut crear l'arxiu",
					ex);
		}
	}

	@Override
	public void update(
			String id,
			String agrupacio,
			InputStream contingut) throws SistemaExternException {
		try {
			File fContent = new File(getBaseDir(agrupacio) + "/" + id);
			fContent.getParentFile().mkdirs();
			if (fContent.exists()) {
				FileOutputStream outContent = new FileOutputStream(fContent, false);
				IOUtils.copy(contingut, outContent);
				outContent.close();
			} else {
				throw new SistemaExternException(
						"No s'ha trobat l'arxiu (id=" + id + ")");
			}
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut actualitzar l'arxiu (id=" + id + ")",
					ex);
		}
	}

	@Override
	public void delete(
			String id,
			String agrupacio) throws SistemaExternException {
		try {
			File fContent = new File(getBaseDir(agrupacio) + "/" + id);
			fContent.getParentFile().mkdirs();
			if (fContent.exists()) {
				fContent.delete();
			} else {
				throw new SistemaExternException(
						"No s'ha trobat l'arxiu (id=" + id + ")");
			}
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut esborrar l'arxiu (id=" + id + ")",
					ex);
		}
	}

	@Override
	public void get(
			String id,
			String agrupacio,
			OutputStream contingutOut) throws SistemaExternException {
		try {
			File fContent = new File(getBaseDir(agrupacio) + "/" + id);
			fContent.getParentFile().mkdirs();
			if (fContent.exists()) {
				FileInputStream contingutIn = new FileInputStream(fContent);
				IOUtils.copy(contingutIn, contingutOut);
			} else {
				throw new SistemaExternException(
						"No s'ha trobat l'arxiu (id=" + id + ")");
			}
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut llegir l'arxiu (id=" + id + ")",
					ex);
		}
	}

	private String getValidSubfolder(String agrupacio){
		String basedir = getBaseDir(agrupacio);
		File file = new File(basedir);
		File[] directories = file.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				File f = new File(current, name);
				if (!f.isDirectory()) {
					return false;
				}
				String[] files = f.list();
				if (files != null && files.length > MAX_FILES_IN_FOLDER) {
					return false;
				}
				return true;
			}
		});
		if (directories != null && directories.length > 0){
			return directories[0].getName() + "/";
		}

		// si no n'hi ha cap de valida en cream una de nova
		String subfolder = generateUniqueName(basedir);
		return subfolder + "/";
	}

	private String getBaseDir(String agrupacio) {
		String baseDir = PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.plugin.gesdoc.filesystem.base.dir");
		if (baseDir != null) {
			if (baseDir.endsWith("/")) {
				return baseDir + agrupacio;
			} else {
				return baseDir + "/" + agrupacio;
			}
		}
		return baseDir;
	}

	private String generateUniqueName(String basedir) {
		String id = Long.toString(System.currentTimeMillis());
		File fContent = new File( basedir + "/" + id);
		fContent.getParentFile().mkdirs();
		while (fContent.exists()) {
			id = Long.toString(System.currentTimeMillis()-1);
			fContent = new File(basedir + "/" + id);
		}
		return id;
	}

}
