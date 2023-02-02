/**
 * 
 */
package es.caib.notib.back.helper;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.validation.BindingResult;

import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto.OrdreDireccioDto;
import es.caib.notib.back.helper.AjaxHelper.AjaxFormResponse;

/**
 * Mètodes d'ajuda per a gestionar datatables.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class DatatablesHelper {

	private static final String ATRIBUT_ID = "DT_Id";
	private static final String ATRIBUT_ROW_ID = "DT_RowId";
	//private static final String ATRIBUT_ROW_DATA = "DT_RowData";
	private static final String ATRIBUT_ROW_SELECTED = "DT_RowSelected";


	public static PaginacioParamsDto getPaginacioDtoFromRequest(HttpServletRequest request) {
		return getPaginacioDtoFromRequest(request, null, null);
	}

	private static PaginacioParamsDto getPaginacioDtoFromRequest(HttpServletRequest request, Map<String, String[]> mapeigFiltres, Map<String, String[]> mapeigOrdenacions) {

		var params = new DatatablesParams(request);
		log.debug("Informació de la pàgina obtingudes de datatables (draw=" + params.getDraw() + ", start=" + params.getStart() + ", length=" + params.getLength() + ")");
		var paginacio = new PaginacioParamsDto();
		var paginaNum = params.getStart() != null && params.getLength() != null ? params.getStart() / params.getLength() : 0;
		paginacio.setPaginaNum(paginaNum);
		paginacio.setPaginaTamany(params.getLength() == null || params.getLength().intValue() == -1 ? Integer.MAX_VALUE : params.getLength());
		paginacio.setFiltre(params.getSearchValue());
		String columna;
		String [] columnes;
		int size = params.getColumnsSearchValue().size();
		for (var i = 0; i < size; i++) {
			columna = params.getColumnsData().get(i);
			columnes = new String[] {columna};
			if (mapeigFiltres != null && mapeigFiltres.get(columna) != null) {
				columnes = mapeigFiltres.get(columna);
			}
			for (var col: columnes) {
				if (!"<null>".equals(col)) {
					paginacio.afegirFiltre(col, params.getColumnsSearchValue().get(i));
					log.debug("Afegit filtre a la paginació (columna=" + col + ", valor=" + params.getColumnsSearchValue().get(i) + ")");
				}
			}
		}
		int columnIndex;
		OrdreDireccioDto direccio;
		size = params.getOrderColumn().size();
		for (var i = 0; i < size; i++) {
			columnIndex = params.getOrderColumn().get(i);
			columna = params.getColumnsData().get(columnIndex);
			direccio = "asc".equals(params.getOrderDir().get(i)) ? OrdreDireccioDto.ASCENDENT : OrdreDireccioDto.DESCENDENT;
			columnes = new String[] {columna};
			if (mapeigOrdenacions != null && mapeigOrdenacions.get(columna) != null) {
				columnes = mapeigOrdenacions.get(columna);
			}
			for (var col: columnes) {
				paginacio.afegirOrdre(col, direccio);
				log.debug("Afegida ordenació a la paginació (columna=" + columna + ", direccio=" + direccio + ")");
			}
		}
		log.debug("Informació de la pàgina sol·licitada (paginaNum=" + paginacio.getPaginaNum() + ", paginaTamany=" + paginacio.getPaginaTamany() + ")");
		return paginacio;
	}

	public static <T> DatatablesResponse getDatatableResponse(HttpServletRequest request, PaginaDto<T> pagina) {
		return getDatatableResponse(request, null, pagina, null, null);
	}

	public static <T> DatatablesResponse getDatatableResponse(HttpServletRequest request, PaginaDto<T> pagina, String atributId) {
		return getDatatableResponse(request, null, pagina, atributId, null);
	}

	public static <T> DatatablesResponse getDatatableResponse(HttpServletRequest request, PaginaDto<T> pagina, String atributId, String atributSeleccio) {
		return getDatatableResponse(request, null, pagina, atributId, atributSeleccio);
	}

	public static <T> DatatablesResponse getDatatableResponse(HttpServletRequest request, BindingResult bindingResult, PaginaDto<T> pagina) {
		return getDatatableResponse(request, bindingResult, pagina, null, null);
	}

	@SuppressWarnings("unchecked")
	public static <T> DatatablesResponse getDatatableResponse(HttpServletRequest request, BindingResult bindingResult, PaginaDto<T> pagina, String atributId, String atributSeleccio) {

		log.debug("Generant informació de resposta per datatable (numero=" + pagina.getNumero() + ", tamany=" + pagina.getTamany() + ", total=" + pagina.getTotal() + ", elementsTotal=" + pagina.getElementsTotal() + ")");
		if (bindingResult != null && bindingResult.hasErrors()) {
			var emptyResponse = getDatatableResponse(request, null, (List<T>)null, null);
			emptyResponse.setFiltreFormResponse(AjaxHelper.generarAjaxFormErrors(null, bindingResult));return emptyResponse;
		}
		var params = new DatatablesParams(request);
		var response = new DatatablesResponse();
		response.setDraw((params.getDraw() != null) ? params.getDraw().intValue() : 0);
		response.setRecordsFiltered(pagina.getElementsTotal());
		response.setRecordsTotal(pagina.getElementsTotal());
		List<Map<String, Object>> dataMap = new ArrayList<>();
		Collection<? extends Object> seleccio = null;
		if (atributSeleccio != null) {
			seleccio = (Collection<? extends Object>)request.getSession().getAttribute(atributSeleccio);
		}
		if (pagina.getContingut() != null) {
			var index = 0;
			List<PropertyDescriptor> descriptors;
			Object[] dadesRegistre;
			Map<String, Object> mapRegistre;
			String propietatNom;
			Object valor, valorId;
			int size;
			for (var registre: pagina.getContingut()) {
				descriptors = getBeanPropertyDescriptors(registre);
				dadesRegistre = new Object[params.getColumnsData().size()];
				mapRegistre = new HashMap<>();
				size = params.getColumnsData().size();
				for (var i = 0; i < size; i++) {
					propietatNom = params.getColumnsData().get(i);
					try {
						if (propietatNom.contains(".")) {
							propietatNom = propietatNom.substring(0, propietatNom.indexOf("."));
						}
						valor = getPropietatValor(registre, propietatNom, descriptors);
						mapRegistre.put(propietatNom, valor);
						dadesRegistre[i] = valor;
					} catch (Exception ex) {
						dadesRegistre[i] = "(!)";
						log.error("No s'ha pogut llegir la propietat de l'objecte (propietatNom=" + propietatNom + ")", ex);
					}
				}
				valorId = null;
				if (atributId != null) {
					try {
						valorId = getPropietatValor(registre, atributId, descriptors);
					} catch (Exception ex) {
						log.error("No s'ha pogut llegir la propietat de l'objecte (propietatNom=" + atributId + ")", ex);
					}
				} else {
					valorId = new Long(index);
				}
				mapRegistre.put(ATRIBUT_ROW_ID, "row_" + valorId);
				mapRegistre.put(ATRIBUT_ID, valorId);
				mapRegistre.put(ATRIBUT_ROW_SELECTED, (seleccio != null && seleccio.contains(valorId)));
				dataMap.add(mapRegistre);
				index++;
			}
		}
		response.setData(dataMap);
		log.debug("Informació per a datatables (draw=" + response.getDraw() + ",recordsFiltered=" + response.getRecordsFiltered() + ",recordsTotal=" + response.getRecordsTotal() + ")");
		return response;
	}

	public static <T> DatatablesResponse getDatatableResponse(HttpServletRequest request, List<T> llista) {
		return getDatatableResponse(request, null, llista, null);
	}

	public static <T> DatatablesResponse getDatatableResponse(HttpServletRequest request, List<T> llista, String atributId) {
		return getDatatableResponse(request, null, llista, atributId);
	}

	public static <T> DatatablesResponse getDatatableResponse(HttpServletRequest request, BindingResult bindingResult, List<T> llista) {
		return getDatatableResponse(request, bindingResult, llista, null);
	}

	public static <T> DatatablesResponse getDatatableResponse(HttpServletRequest request, BindingResult bindingResult, List<T> llista, String atributId) {

		log.debug(llista != null ? "Informació de la llista (tamany=" + llista.size() + ")" : "Informació de la llista (null)");
		var dto = new PaginaDto<T>();
		dto.setNumero(0);
		dto.setTamany((llista != null) ? llista.size() : 0);
		dto.setTotal(1);
		dto.setElementsTotal((llista != null) ? llista.size() : 0);
		dto.setAnteriors(false);
		dto.setPrimera(true);
		dto.setPosteriors(false);
		dto.setDarrera(true);
		dto.setContingut(llista);
		return getDatatableResponse(request, bindingResult, dto, atributId, null);
	}

	/*public static <T> DatatablesResponse getDatatableResponse(
			HttpServletRequest request,
			T element) {
		return getDatatableResponse(request, null, element, null);
	}
	public static <T> DatatablesResponse getDatatableResponse(
			HttpServletRequest request,
			T element,
			String atributId) {
		return getDatatableResponse(request, null, element, atributId);
	}
	public static <T> DatatablesResponse getDatatableResponse(
			HttpServletRequest request,
			BindingResult bindingResult,
			T element) {
		return getDatatableResponse(request, bindingResult, element, null);
	}
	public static <T> DatatablesResponse getDatatableResponse(
			HttpServletRequest request,
			BindingResult bindingResult,
			T element,
			String atributId) {
		List<T> list = new ArrayList<T>();
		list.add(element);
		return getDatatableResponse(request, bindingResult, list, atributId);
	}*/

	public static <T> DatatablesResponse getEmptyDatatableResponse(HttpServletRequest request) {
		return getDatatableResponse(request, null, (List<T>)null, null);
	}
	public static <T> DatatablesResponse getEmptyDatatableResponse(HttpServletRequest request, BindingResult bindingResult) {
		return getDatatableResponse(request, bindingResult, (List<T>)null, null);
	}


	@NoArgsConstructor
	@Getter @Setter
	public static class DatatablesResponse {
		private int draw;
		private long recordsTotal;
		private long recordsFiltered;
		private List<Map<String, Object>> data;
		private String error;
		private AjaxFormResponse filtreFormResponse;
	}

	@Getter @Setter
	public static class DatatablesParams {
		private Integer draw;
		private Integer start;
		private Integer length;
		private String searchValue;
		private Boolean searchRegex;
		private List<Integer> orderColumn = new ArrayList<>();
		private List<String> orderDir = new ArrayList<>();
		private List<String> columnsData = new ArrayList<>();
		private List<String> columnsName = new ArrayList<>();
		private List<Boolean> columnsSearchable = new ArrayList<>();
		private List<Boolean> columnsOrderable = new ArrayList<>();
		private List<String> columnsSearchValue = new ArrayList<>();
		private List<Boolean> columnsSearchRegex = new ArrayList<>();

		public DatatablesParams(HttpServletRequest request) {

			if (request.getParameter("draw") != null) {
				draw = Integer.parseInt(request.getParameter("draw"));
			}
			if (request.getParameter("start") != null) {
				start = Integer.parseInt(request.getParameter("start"));
			}
			if (request.getParameter("length") != null) {
				length = Integer.parseInt(request.getParameter("length"));
			}
			if (request.getParameter("search[value]") != null) {
				searchValue = request.getParameter("search[value]");
			}
			if (request.getParameter("search[regex]") != null) {
				searchRegex = Boolean.parseBoolean(request.getParameter("search[regex]"));
			}
			String paramPrefix;
			for (int i = 0;; i++) {
				paramPrefix = "order[" + i + "]";
				if (request.getParameter(paramPrefix + "[column]") == null) {
					break;
				}
				orderColumn.add(Integer.parseInt(request.getParameter(paramPrefix + "[column]")));
				orderDir.add(request.getParameter(paramPrefix + "[dir]"));
			}
			for (int i = 0;; i++) {
				paramPrefix = "columns[" + i + "]";
				if (request.getParameter(paramPrefix + "[data]") == null) {
					break;
				}
				columnsData.add(request.getParameter(paramPrefix + "[data]"));
				columnsName.add(request.getParameter(paramPrefix + "[name]"));
				columnsSearchable.add(Boolean.parseBoolean(request.getParameter(paramPrefix + "[searchable]")));
				columnsOrderable.add(Boolean.parseBoolean(request.getParameter(paramPrefix + "[orderable]")));
				columnsSearchValue.add(request.getParameter(paramPrefix + "[search][value]"));
				columnsSearchRegex.add(Boolean.parseBoolean(request.getParameter(paramPrefix + "[search][regex]")));
			}
		}
	}

	private static List<PropertyDescriptor> getBeanPropertyDescriptors(Object bean) {

		List<PropertyDescriptor> descriptors = new ArrayList<>(Arrays.asList(PropertyUtils.getPropertyDescriptors(bean)));
		var it = descriptors.iterator();
		while (it.hasNext()) {
			PropertyDescriptor pd = it.next();
			if ("class".equals(pd.getName())) {
				it.remove();
				break;
			}
		}
		return descriptors;
	}

	/*private static Map<String, Object> getRowData(
			Object bean,
			String atributId) {
		Map<String, Object> rowData = new HashMap<String, Object>();
		try {
			rowData.put(
					"id",
					PropertyUtils.getProperty(bean, atributId));
		} catch (Exception ex) {
			log.error(
					"No s'ha pogut llegir la propietat de l'objecte (" +
							"propietatNom=" + atributId + ")",
					ex);
		}
		return rowData;
	}*/

	private static Object getPropietatValor(Object registre, String propietatNom, List<PropertyDescriptor> descriptors) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

		try {
			int index = Integer.parseInt(propietatNom);
			return PropertyUtils.getProperty(registre, descriptors.get(index).getName());
		} catch (NumberFormatException ex) {
			if (propietatNom != null && !propietatNom.isEmpty() && !"<null>".equals(propietatNom)) {
				return PropertyUtils.getProperty(registre, propietatNom);
			}
			return null;
		}
	}
	
}
