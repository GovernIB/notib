package es.caib.notib.logic.helper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests de CacheHelper.evictCachesPermisosOrgansProcediments.
 */
class CacheHelperPermisosEvictTest {

	private static final List<String> CACHES_PERMISOS = List.of(
			"organsAmbPermis", "organsAmbPermisPerConsulta", "codisPermisProcessar",
			"procserAmbPermis", "procedimentsAmbPermis", "serveisAmbPermis",
			"procsersPermisNotificacioMenu", "procsersPermisComunicacioMenu", "procsersPermisComunicacioSirMenu",
			"organsPermisPerProcedimentComu", "procserOrgansCodisAmbPermis");

	private CacheHelper cacheHelper;
	private ConcurrentMapCacheManager cacheManager;

	@BeforeEach
	void setUp() {
		cacheManager = new ConcurrentMapCacheManager();
		cacheHelper = new CacheHelper();
		ReflectionTestUtils.setField(cacheHelper, "cacheManager", cacheManager);
	}

	@AfterEach
	void tearDown() {
		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronizationManager.clearSynchronization();
		}
	}

	@Test
	void shouldClearAllPermissionCachesAndKeepOthers() {
		omplir();
		cacheManager.getCache("findEntitatByCodi").put("k", "v");

		cacheHelper.evictCachesPermisosOrgansProcediments();

		CACHES_PERMISOS.forEach(nom -> assertNull(cacheManager.getCache(nom).get("k"), nom));
		assertNotNull(cacheManager.getCache("findEntitatByCodi").get("k"));
	}

	@Test
	void shouldClearAgainAfterCommit() {
		TransactionSynchronizationManager.initSynchronization();
		cacheHelper.evictCachesPermisosOrgansProcediments();
		// Una consulta concurrent torna a omplir la cache abans del commit
		omplir();

		TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);

		CACHES_PERMISOS.forEach(nom -> assertNull(cacheManager.getCache(nom).get("k"), nom));
	}

	private void omplir() {
		CACHES_PERMISOS.forEach(nom -> cacheManager.getCache(nom).put("k", "v"));
	}

}
