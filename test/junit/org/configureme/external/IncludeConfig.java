package org.configureme.external;

import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureAlso;
import org.configureme.annotations.ConfigureMe;

/**
 * @author ivanbatura
 * @since: 01.10.12
 */
@ConfigureMe(name = "include")
public class IncludeConfig {
	@ConfigureAlso
	ExternalConfig externalConfig;

	@Configure
	Integer normal;

	@Configure
	String include;

	@Configure
	String linked;

	@ConfigureAlso
	IncludeConfig circleConfig;

	public ExternalConfig getExternalConfig() {
		return externalConfig;
	}

	public void setExternalConfig(ExternalConfig externalConfig) {
		this.externalConfig = externalConfig;
	}

	public Integer getNormal() {
		return normal;
	}

	public void setNormal(Integer normal) {
		this.normal = normal;
	}

	public String getInclude() {
		return include;
	}

	public void setInclude(String include) {
		this.include = include;
	}

	public String getLinked() {
		return linked;
	}

	public void setLinked(String linked) {
		this.linked = linked;
	}

	public IncludeConfig getCircleConfig() {
		return circleConfig;
	}

	public void setCircleConfig(IncludeConfig circleConfig) {
		this.circleConfig = circleConfig;
	}
}
