package cc.boeters.p2000monitor.archive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Singleton;

import cc.boeters.p2000monitor.connector.CapcodeDatabase;

@Singleton
public class AbbreviationsService {
	// ([A-Z][A-Z0-9]{0,1}){2,}
	private static final Pattern ABBR_PATTERN = Pattern
			.compile("\\b[A-Z][A-Z0-9]{1,}\\b");

	private static final Map<String, String> DB;

	private static final String SOURCE = "/data/abbr.csv";

	static {
		DB = new HashMap<String, String>(200);

		InputStream stream = CapcodeDatabase.class.getResourceAsStream(SOURCE);

		BufferedReader reader = new BufferedReader(
				new InputStreamReader(stream));

		String line = null;

		try {
			while ((line = reader.readLine()) != null) {
				String[] tokens = line.split(";");
				String value = tokens[0];
				for (int i = 1; i < tokens.length; i++) {
					DB.put(tokens[i], value);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String translate(String value) {
		Matcher matcher = ABBR_PATTERN.matcher(value);
		while (matcher.find()) {
			String abbreviation = matcher.group().trim();
			if (DB.get(abbreviation) == null) {
				continue;
			}
			value = value.replaceFirst(abbreviation, DB.get(abbreviation));
		}
		return value;
	}

}
