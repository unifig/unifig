package com.tools.plugin.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoad {

	public static void loadProperties(Properties properties, InputStream inStream) throws IOException {
		loadProperties(properties, inStream, "GBK");
	}

	public static void loadProperties(Properties properties, InputStream inStream, String encodingName) throws IOException {
		if ((properties == null) || (inStream == null)) {
			return;
		}
		properties.load(inStream);
	}

}
