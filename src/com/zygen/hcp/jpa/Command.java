package com.zygen.hcp.jpa;

import java.io.Serializable;
import javax.persistence.*;

import org.eclipse.persistence.annotations.Multitenant;
import org.eclipse.persistence.annotations.TenantDiscriminatorColumn;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "Command")
@Multitenant
@TenantDiscriminatorColumn(name = "tenant_id", contextProperty = "me-tenant.id", length = 36)
@NamedQuery(name = "AllCommand", query = "select p from Command p")
public class Command implements Serializable {

	private static final long serialVersionUID = 1L;

	public Command() {
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private long id;
	private String command;
	private String sourceSystem;
	private String langu;
	private String description;
	private String pattern;
	private String tag;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String param) {
		this.command = param;
	}

	public String getSourceSystem() {
		return sourceSystem;
	}

	public void setSourceSystem(String param) {
		this.sourceSystem = param;
	}

	public String getLangu() {
		return langu;
	}

	public void setLangu(String param) {
		this.langu = param;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String param) {
		this.description = param;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String param) {
		this.pattern = param;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String param) {
		this.tag = param;
	}

}