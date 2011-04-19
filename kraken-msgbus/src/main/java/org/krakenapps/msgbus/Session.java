package org.krakenapps.msgbus;

import java.util.Locale;

public interface Session {
	int getId();
	
	Integer getOrgId();
	
	Integer getAdminId();
	
	Locale getLocale();

	Object get(String key);

	String getString(String key);

	Integer getInt(String key);

	void setProperty(String key, Object value);

	void unsetProperty(String key);

	void send(Message msg);

	void close();
}