package config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigParser {

	public static String readFile(Path path) throws Exception {
		return Files.readString(path);
	}

	public static double readDouble(String json, String key, double defVal) {
		try {
			Pattern p = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*([0-9.\\-]+)");
			Matcher m = p.matcher(json);
			if (m.find()) {
				return Double.parseDouble(m.group(1));
			}
		} catch (Exception ignored) {
		}
		return defVal;
	}

	public static long readLong(String json, String key, long defVal) {
		try {
			Pattern p = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*([0-9\\-]+)");
			Matcher m = p.matcher(json);
			if (m.find()) {
				return Long.parseLong(m.group(1));
			}
		} catch (Exception ignored) {
		}
		return defVal;
	}

	public static String readString(String json, String key, String defVal) {
		try {
			Pattern p = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*\"([^\"]+)\"");
			Matcher m = p.matcher(json);
			if (m.find()) {
				return m.group(1);
			}
		} catch (Exception ignored) {
		}
		return defVal;
	}
}
