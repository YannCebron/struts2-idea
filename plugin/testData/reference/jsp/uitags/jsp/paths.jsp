<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- relative paths ============== --%>

<s:include value="paths.jsp"/>
<s:include value="<error>INVALID_VALUE</error>"/>

<s:url value="paths.jsp"/>
<s:url value="<error>INVALID_VALUE</error>"/>

<s:submit src="paths.jsp"/>
<s:submit src="<error>INVALID_VALUE</error>"/>