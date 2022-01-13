package ${apiPackagePath}.model;

<#if entity.hasCompoundPK()>
	import ${apiPackagePath}.service.persistence.${entity.name}PK;
</#if>

import java.io.Serializable;

import java.util.Date;
import java.util.List;

/**
 * This class is used by SOAP remote services<#if entity.hasRemoteService()>, specifically {@link ${packagePath}.service.http.${entity.name}ServiceSoap}</#if>.
 *
 * @author ${author}
<#if serviceBuilder.isVersionGTE_7_3_0()>
 * @deprecated As of Athanasius (7.3.x), with no direct replacement
<#elseif classDeprecated>
 * @deprecated ${classDeprecatedComment}
</#if>
 * @generated
 */

<#if serviceBuilder.isVersionGTE_7_3_0() || classDeprecated>
	@Deprecated
</#if>
public class ${entity.name}Soap implements Serializable {

	public static ${entity.name}Soap toSoapModel(${entity.name} model) {
		throw new UnsupportedOperationException();
	}

	public static ${entity.name}Soap[] toSoapModels(${entity.name}[] models) {
		throw new UnsupportedOperationException();
	}

	public static ${entity.name}Soap[][] toSoapModels(${entity.name}[][] models) {
		throw new UnsupportedOperationException();
	}

	public static ${entity.name}Soap[] toSoapModels(List<${entity.name}> models) {
		throw new UnsupportedOperationException();
	}

	public ${entity.name}Soap() {
	}

	public ${entity.PKClassName} getPrimaryKey() {
		throw new UnsupportedOperationException();
	}

	public void setPrimaryKey(${entity.PKClassName} pk) {
		throw new UnsupportedOperationException();
	}

	<#list entity.regularEntityColumns as entityColumn>
		public ${entityColumn.genericizedType} get${entityColumn.methodName}() {
			throw new UnsupportedOperationException();
		}

		<#if entityColumn.type== "boolean">
			public ${entityColumn.type} is${entityColumn.methodName}() {
				throw new UnsupportedOperationException();
			}
		</#if>

		public void set${entityColumn.methodName}(${entityColumn.genericizedType} ${entityColumn.name}) {
			throw new UnsupportedOperationException();
		}
	</#list>

}