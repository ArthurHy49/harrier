
package cn.spdb.harrier.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyUtils {
	private static final Logger logger = LoggerFactory.getLogger(PropertyUtils.class);

	private static final Properties properties = new Properties();

	private PropertyUtils() {
		throw new UnsupportedOperationException("Construct PropertyUtils");
	}

	static {
		loadPropertyFile("/application.properties");
	}

	public static synchronized void loadPropertyFile(String... propertyFiles) {
		for (String fileName : propertyFiles) {
			try (InputStream fis = PropertyUtils.class.getResourceAsStream(fileName);) {
				properties.load(fis);

			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				System.exit(1);
			}
		}

		// Override from system properties
		System.getProperties().forEach((k, v) -> {
			final String key = String.valueOf(k);
			logger.info("Overriding property from system property: {}", key);
			PropertyUtils.setValue(key, String.valueOf(v));
		});
	}

	/**
	 * get property value
	 *
	 * @param key property name
	 * @return property value
	 */
	public static String getString(String key) {
		return properties.getProperty(key.trim());
	}

	/**
	 * get property value with upper case
	 *
	 * @param key property name
	 * @return property value with upper case
	 */
	public static String getUpperCaseString(String key) {
		return properties.getProperty(key.trim()).toUpperCase();
	}

	/**
	 * get property value
	 *
	 * @param key        property name
	 * @param defaultVal default value
	 * @return property value
	 */
	public static String getString(String key, String defaultVal) {
		String val = properties.getProperty(key.trim());
		return val == null ? defaultVal : val;
	}

	/**
	 * get property value
	 *
	 * @param key property name
	 * @return get property int value , if key == null, then return -1
	 */
	public static int getInt(String key) {
		return getInt(key, -1);
	}

	/**
	 * @param key          key
	 * @param defaultValue default value
	 * @return property value
	 */
	public static int getInt(String key, int defaultValue) {
		String value = getString(key);
		if (value == null) {
			return defaultValue;
		}

		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			logger.info(e.getMessage(), e);
		}
		return defaultValue;
	}

	/**
	 * get property value
	 *
	 * @param key property name
	 * @return property value
	 */
	public static boolean getBoolean(String key) {
		String value = properties.getProperty(key.trim());
		if (null != value) {
			return Boolean.parseBoolean(value);
		}

		return false;
	}

	/**
	 * get property value
	 *
	 * @param key          property name
	 * @param defaultValue default value
	 * @return property value
	 */
	public static Boolean getBoolean(String key, boolean defaultValue) {
		String value = properties.getProperty(key.trim());
		if (null != value) {
			return Boolean.parseBoolean(value);
		}

		return defaultValue;
	}

	/**
	 * get property long value
	 *
	 * @param key        key
	 * @param defaultVal default value
	 * @return property value
	 */
	public static long getLong(String key, long defaultVal) {
		String val = getString(key);
		return val == null ? defaultVal : Long.parseLong(val);
	}

	/**
	 * @param key key
	 * @return property value
	 */
	public static long getLong(String key) {
		return getLong(key, -1);
	}

	/**
	 * @param key        key
	 * @param defaultVal default value
	 * @return property value
	 */
	public static double getDouble(String key, double defaultVal) {
		String val = getString(key);
		return val == null ? defaultVal : Double.parseDouble(val);
	}

	/**
	 * get array
	 *
	 * @param key      property name
	 * @param splitStr separator
	 * @return property value through array
	 */
	public static String[] getArray(String key, String splitStr) {
		String value = getString(key);
		if (value == null) {
			return new String[0];
		}
		try {
			String[] propertyArray = value.split(splitStr);
			return propertyArray;
		} catch (NumberFormatException e) {
			logger.info(e.getMessage(), e);
		}
		return new String[0];
	}

	/**
	 * @param key          key
	 * @param type         type
	 * @param defaultValue default value
	 * @param <T>          T
	 * @return get enum value
	 */
	public static <T extends Enum<T>> T getEnum(String key, Class<T> type, T defaultValue) {
		String val = getString(key);
		return val == null ? defaultValue : Enum.valueOf(type, val);
	}

	/**
	 * get all properties with specified prefix, like: fs.
	 *
	 * @param prefix prefix to search
	 * @return all properties with specified prefix
	 */
	public static Map<String, String> getPrefixedProperties(String prefix) {
		Map<String, String> matchedProperties = new HashMap<>();
		for (String propName : properties.stringPropertyNames()) {
			if (propName.startsWith(prefix)) {
				matchedProperties.put(propName, properties.getProperty(propName));
			}
		}
		return matchedProperties;
	}

	/**
	 *
	 */
	public static void setValue(String key, String value) {
		properties.setProperty(key, value);
	}

	public static Map<String, String> getPropertiesByPrefix(String prefix) {
		if (StringUtils.isEmpty(prefix)) {
			return null;
		}
		Set<Object> keys = properties.keySet();
		if (keys.isEmpty()) {
			return null;
		}
		Map<String, String> propertiesMap = new HashMap<>();
		keys.forEach(k -> {
			if (k.toString().contains(prefix)) {
				propertiesMap.put(k.toString().replaceFirst(prefix + ".", ""), properties.getProperty((String) k));
			}
		});
		return propertiesMap;
	}

}
