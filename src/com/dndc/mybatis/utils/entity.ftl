package ${packageName};

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * <p>${entityName} for ${tableName}</p><br />
 * ${tableComment}
 * @author ${author}
 * @Date ${createDate}
 */
 @Entity
 @Table(name="${tableName}")
public class ${entityName} implements Serializable {

	private static final long serialVersionUID = 1L;
	
	<#list tableColumns as column>
	/**${column.comment}*/
	<#if column.primaryKey>
	@Id
	</#if>
	@Column(name = "${column.name}")
	private ${column.type} ${column.lowerName};
	
	</#list>
	
	<#list tableColumns as method>
	public void set${method.upperName}(${method.type} ${method.lowerName}) {
		this.${method.lowerName} = ${method.lowerName};
	}
	
	public ${method.type} get${method.upperName}() {
		return this.${method.lowerName};
	}
	
	</#list>
	

}
