package pr.connectionData;

public class ConnectionData {
	private String connectionName;
	private String userName;
	private char[] password;
	private String serverName;
	private String serverPort;
	private String serviceName;
	private String[] logs;

	public String[] getLogs() {
		return logs;
	}

	public void setLogs(String[] logs) {
		this.logs = logs;
	}

	private int dictionary;

	public String getConnectionName() {
		return connectionName;
	}

	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public char[] getPassword() {
		return password;
	}

	public void setPassword(char[] password) {
		this.password = password;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getServerPort() {
		return serverPort;
	}

	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public int getDictionary() {
		return dictionary;
	}

	public void setDictionary(int dictionary) {
		this.dictionary = dictionary;
	}
}
