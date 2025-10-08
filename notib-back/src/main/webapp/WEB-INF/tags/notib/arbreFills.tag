<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ attribute name="pare" required="true" type="java.lang.Object"%>
<%@ attribute name="fills" required="true" type="java.lang.Object"%>
<%@ attribute name="atributId" required="true"%>
<%@ attribute name="atributNom" required="true"%>
<%@ attribute name="seleccionatId"%>
<%@ attribute name="fulles" type="java.lang.Object"%>
<%@ attribute name="fullesAtributId"%>
<%@ attribute name="fullesAtributNom"%>
<%@ attribute name="fullesAtributPare"%>
<%@ attribute name="fullesIcona"%>
<%@ attribute name="fullesIconaDreta"%>
<%@ attribute name="fullesAtributDreta"%>
<%@ attribute name="fullesCondicioDreta"%>
<%@ attribute name="fullesMissatgeDreta"%>
<%@ attribute name="fullesAtributCssClassCondition"%>
<%@ attribute name="fullesAtributInfoText"%>
<%@ attribute name="isOcultarCounts" type="java.lang.Boolean"%>
<ul>
	<c:forEach var="fill" items="${fills}">
		<li id="${fill.dades[atributId]}" data-jstree='{"icon":"fa fa-university fa-lg"<c:if test="${not empty seleccionatId and fill.dades[atributId] == seleccionatId}">, "selected": true</c:if>}'>
			<small <c:if test="${fill.retornatFiltre}">style="font-weight:bold;"</c:if>>${fill.dades[atributNom]}<c:if test="${not isOcultarCounts and fill.mostrarCount}"> <span class="badge">${fill.count}</span></c:if></small>
			<not:arbreFills pare="${fill}" fills="${fill.fills}" atributId="${atributId}" atributNom="${atributNom}" seleccionatId="${seleccionatId}"  fulles="${fulles}"
							fullesIcona="${fullesIcona}" fullesAtributId="${fullesAtributId}" fullesAtributNom="${fullesAtributNom}" fullesAtributPare="${fullesAtributPare}"
							isOcultarCounts="${isOcultarCounts}" fullesAtributCssClassCondition="${fullesAtributCssClassCondition}"
							fullesAtributDreta="${fullesAtributDreta}" fullesMissatgeDreta="${fullesMissatgeDreta}"
							fullesIconaDreta="${fullesIconaDreta}" fullesCondicioDreta="${fullesCondicioDreta}"/>
			<c:if test="${fullesCondicioDreta != fill.dades[fullesAtributDreta]}">
				<span> </span><span class="${fullesIconaDreta}" <c:if test="${fill.dades[fullesAtributDreta] != null}">title="<spring:message code="${fullesMissatgeDreta}${fill.dades[fullesAtributDreta]}"/>"</c:if>></span>
			</c:if>

		</li>
	</c:forEach>
	<c:forEach var="fulla" items="${fulles}">
		<c:if test="${fulla[fullesAtributPare] == pare.dades[atributId]}">
			<li id="${fulla[fullesAtributId]}" data-jstree='{"icon":"${fullesIcona}"}'>
					${fullesAtributInfoText}
				<c:choose>
					<c:when test="${!empty fullesAtributCssClassCondition && fulla[fullesAtributCssClassCondition]}">
						<a class="fullesAtributCssClass">${fulla[fullesAtributNom]}
							<c:if test="${!empty fullesAtributInfoText && fulla[fullesAtributInfo]}">${fullesAtributInfoText}</c:if>
						</a>
					</c:when>
					<c:otherwise>
						<a >${fulla[fullesAtributNom]}
							<c:if test="${!empty fullesAtributInfoText && fulla[fullesAtributInfo]}">${fullesAtributInfoText}</c:if>
						</a>
					</c:otherwise>
				</c:choose>
				<c:if test="${!empty fullesAtributInfoText && fulla[fullesAtributInfo]}">${fullesAtributInfoText}</c:if>

			</li>
		</c:if>
	</c:forEach>
	<%--	<c:forEach var="fulla" items="${fulles}">--%>
	<%--		<c:if test="${fulla[fullesAtributPare] == pare.dades[atributId]}">--%>
	<%--			<li id="${fulla[fullesAtributId]}" data-jstree='{"icon":"${fullesIcona}"}'>--%>
	<%--					${fulla[fullesAtributNom]} ${fullesAtributInfoText}--%>
	<%--					&lt;%&ndash;					<a class="fullesAtributCssClass">${fulla[fullesAtributNom]}&ndash;%&gt;--%>
	<%--&lt;%&ndash;						<c:if test="${!empty fullesAtributInfoText && fulla[fullesAtributInfo]}">${fullesAtributInfoText}</c:if>&ndash;%&gt;--%>
	<%--&lt;%&ndash;					</a>&ndash;%&gt;--%>
	<%--			</li>--%>
	<%--		</c:if>--%>
	<%--	</c:forEach>--%>
</ul>
