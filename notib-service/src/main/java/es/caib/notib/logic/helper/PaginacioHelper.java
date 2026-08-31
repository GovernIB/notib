/**
 *
 */
package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto.OrdreDireccioDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto.OrdreDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Helper per a convertir les dades de paginació entre el DTO
 * i Spring-Data.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PaginacioHelper {

	@Resource
	private ConversioTipusHelper conversioTipusHelper;

	public boolean esPaginacioActivada(PaginacioParamsDto dto) {
		return dto.getPaginaTamany() > 0;
	}

	public <T> Pageable toSpringDataPageable(PaginacioParamsDto dto, Map<String, String[]> mapeigPropietatsOrdenacio) {
		return PageRequest.of(dto.getPaginaNum(), dto.getPaginaTamany(), toSpringDataSort(dto.getOrdres(), mapeigPropietatsOrdenacio));
	}

	public <T> Pageable toSpringDataPageable(PaginacioParamsDto dto) {
		return toSpringDataPageable(dto, null);
	}

	public <T> Sort toSpringDataSort(PaginacioParamsDto dto) {
		return toSpringDataSort(dto.getOrdres(), null);
	}

	public Sort toSpringDataSort(List<OrdreDto> ordres, Map<String, String[]> mapeigPropietatsOrdenacio) {

		List<Order> orders = new ArrayList<>();
		if (ordres == null) {
			return null;
		}
		for (var ordre: ordres) {
			var direccio = OrdreDireccioDto.DESCENDENT.equals(ordre.getDireccio()) ? Sort.Direction.DESC : Sort.Direction.ASC;
			if (mapeigPropietatsOrdenacio == null) {
				var order = new Order(direccio, ordre.getCamp());
				order.nullsFirst();
				orders.add(order);
				continue;
			}
			var mapeig = mapeigPropietatsOrdenacio.get(ordre.getCamp());
			if (mapeig == null) {
				var order = new Order(direccio, ordre.getCamp());
				order.nullsFirst();
				orders.add(order);
				continue;
			}
			for (var prop: mapeig) {
				var order = new Order(direccio, prop);
				order.nullsFirst();
				orders.add(order);
			}
		}
		return !orders.isEmpty() ? Sort.by(orders) : null;
	}
	public <T> PaginaDto<T> toPaginaDto(Page<T> page) {
		return toPaginaDto(page, null);
	}

	public <T, S> PaginaDto<T> toPaginaDto(List<T> list, Page<S> page) {

		var dto = new PaginaDto<T>();
		dto.setNumero(page.getNumber());
		dto.setTamany(page.getSize());
		dto.setTotal(page.getTotalPages());
		dto.setElementsTotal(page.getTotalElements());
		dto.setAnteriors(page.hasPrevious());
		dto.setPrimera(page.isFirst());
		dto.setPosteriors(page.hasNext());
		dto.setDarrera(page.isLast());
		dto.setContingut(list);
		return dto;
	}

	public <T> PaginaDto<T> toPaginaDto(Page<?> page, List<?> llista, Class<T> targetType) {

		var dto = new PaginaDto<T>();
		dto.setNumero(page.getNumber());
		dto.setTamany(page.getSize());
		dto.setTotal(page.getTotalPages());
		dto.setElementsTotal(page.getTotalElements());
		dto.setAnteriors(page.hasPrevious());
		dto.setPrimera(page.isFirst());
		dto.setPosteriors(page.hasNext());
		dto.setDarrera(page.isLast());
		if (targetType != null) {
			dto.setContingut(conversioTipusHelper.convertirList(llista, targetType));
		}
		return dto;
	}

	public <S, T> PaginaDto<T> toPaginaDto(Page<S> page, Class<T> targetType) {
		return toPaginaDto(page, targetType, null);
	}

	@SuppressWarnings("unchecked")
	public <S, T> PaginaDto<T> toPaginaDto(Page<S> page, Class<T> targetType, Converter<S, T> converter) {

		var dto = new PaginaDto<T>();
		dto.setNumero(page.getNumber());
		dto.setTamany(page.getSize());
		dto.setTotal(page.getTotalPages());
		dto.setElementsTotal(page.getTotalElements());
		dto.setAnteriors(page.hasPrevious());
		dto.setPrimera(page.isFirst());
		dto.setPosteriors(page.hasNext());
		dto.setDarrera(page.isLast());
		if (page.hasContent() && converter != null) {
			List<T> contingut = new ArrayList<>();
			for (var element : page.getContent()) {
				contingut.add(converter.convert(element));
			}
			dto.setContingut(contingut);
		} else if (page.hasContent() && targetType != null) {
			dto.setContingut(conversioTipusHelper.convertirList(page.getContent(), targetType));
		} else if (page.hasContent()){
			dto.setContingut((List<T>) page.getContent());
		}
		return dto;
	}

	public <T> PaginaDto<T> toPaginaDto(List<?> llista, Class<T> targetType) {

		var dto = new PaginaDto<T>();
		dto.setNumero(0);
		dto.setTamany(llista.size());
		dto.setTotal(1);
		dto.setElementsTotal(llista.size());
		dto.setAnteriors(false);
		dto.setPrimera(true);
		dto.setPosteriors(false);
		dto.setDarrera(true);
		var contingut = !llista.isEmpty() && targetType != null && !targetType.equals(llista.get(0).getClass()) ?
						conversioTipusHelper.convertirList(llista, targetType) : (List<T>) llista;
		dto.setContingut(contingut);
		return dto;
	}

	public <T> PaginaDto<T> getPaginaDtoBuida(Class<T> targetType) {

		var dto = new PaginaDto<T>();
		dto.setNumero(0);
		dto.setTamany(0);
		dto.setTotal(1);
		dto.setElementsTotal(0);
		dto.setAnteriors(false);
		dto.setPrimera(true);
		dto.setPosteriors(false);
		dto.setDarrera(true);
		return dto;
	}

	public <T> PaginaDto<T> toPaginaDto(List<T> elements, Pageable pageable) {

		if (elements == null) {
			elements = Collections.emptyList();
		}
		int pageNumber = pageable.getPageNumber(); // zero-based
		int pageSize = pageable.getPageSize();
		long totalElements = elements.size();
		long offset = pageable.getOffset();
		List<T> content;
		if (offset >= totalElements) {
			content = Collections.emptyList();
		} else {
			int start = (int) offset;
			int end = Math.min(start + pageSize, elements.size());
			content = new ArrayList<>(elements.subList(start, end));
		}
		int totalPages = pageSize != 0 ? (int) Math.ceil((double) totalElements / pageSize) : 0;
		PaginaDto<T> pagina = new PaginaDto<>();
		pagina.setNumero(pageNumber);
		pagina.setTamany(pageSize);
		pagina.setTotal(totalPages);
		pagina.setElementsTotal(totalElements);
		pagina.setContingut(content);
		pagina.setPrimera(pageNumber == 0);
		pagina.setDarrera(totalPages == 0 || pageNumber >= totalPages - 1);
		pagina.setAnteriors(pageNumber > 0);
		pagina.setPosteriors(pageNumber < totalPages - 1);
		return pagina;
	}

	public <T> Page<T> toPage(List<T> elements, Pageable pageable) {

		if (elements == null) {
			elements = Collections.emptyList();
		}
		long totalElements = elements.size();
		long offset = pageable.getOffset();
		int pageSize = pageable.getPageSize();
		if (offset >= totalElements) {
			return new PageImpl<>(Collections.emptyList(), pageable, totalElements);
		}
		int start = (int) offset;
		int end = Math.min(start + pageSize, elements.size());
		List<T> content = elements.subList(start, end);
		return new PageImpl<>(content, pageable, totalElements);
	}

	public Pageable getPageable(Integer pagina, Integer mida) {

		var pageable = PageRequest.of(0, 999999999);
		if (pagina != null && mida != null) {
			pageable = PageRequest.of(pagina, mida);
		}
		return pageable;
	}

	public interface Converter<S, T> {
	    T convert(S source);
	}
}
