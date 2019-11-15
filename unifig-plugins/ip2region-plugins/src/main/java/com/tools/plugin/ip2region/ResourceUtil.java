package com.tools.plugin.ip2region;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

public class ResourceUtil {

	/**
	 * 获取资源，依据通配符路径
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static Resource[] getResourcePaths(String path){
		ResourcePatternResolver resourceLoader = new PathMatchingResourcePatternResolver();
		Resource[] sources = null;
		try {
			sources = resourceLoader.getResources(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sources;
	}
}
