package org.configureme.githubissues.issue16;

import org.configureme.annotations.ConfigureMe;
import org.configureme.sources.ConfigurationSourceKey;

/**
 * TODO comment this class
 *
 * @author lrosenberg
 * @since 2019-05-10 09:53
 */
@ConfigureMe(allfields = true, type = ConfigurationSourceKey.Type.FILE, name="issue16")
public class ConfigObject {
	private String apiKey;
	private String secret;
	private String server;

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	@Override
	public String toString() {
		return "ConfigObject{" +
				"apiKey='" + apiKey + '\'' +
				", secret='" + secret + '\'' +
				", server='" + server + '\'' +
				'}';
	}
}
