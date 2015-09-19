# ${apiName}

<#if overview??>${overview}</#if>

<#list services as service>
## ${service.name}

<#list service.methods as method>
### ${method.verb} ${method.path!"/"} <#if method.queryParameters?size &gt; 0>(${method.queryParameterNames?join(", ")})</#if>

${method.title!"(no name)"}

<#if method.description??>
${method.description}
</#if>
</#list>

</#list>
