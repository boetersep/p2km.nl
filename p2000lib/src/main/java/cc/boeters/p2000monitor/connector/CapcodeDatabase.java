package cc.boeters.p2000monitor.connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import cc.boeters.p2000monitor.model.CapcodeInfo;

@Singleton
public class CapcodeDatabase {

	private static final Map<Integer, CapcodeInfo> DB;

	private static final String SOURCE = "/data/capcodes.csv";

	static {
		DB = new HashMap<Integer, CapcodeInfo>(10000);

		InputStream stream = CapcodeDatabase.class.getResourceAsStream(SOURCE);

		BufferedReader reader = new BufferedReader(
				new InputStreamReader(stream));

		String line = null;

		try {
			while ((line = reader.readLine()) != null) {
				CapcodeInfo info = new CapcodeInfo();
				String[] tokens = line.split(";");
				Integer capcode = Integer.valueOf(tokens[0]
						.replaceAll("\"", ""));
				info.setCapcode(capcode);
				info.setDiscipline(tokens[1].replaceAll("\"", ""));
				info.setRegion(tokens[2].replaceAll("\"", ""));
				info.setSector(tokens[3].replaceAll("\"", ""));
				info.setDescription(tokens[4].replaceAll("\"", ""));
				info.setShortdesc(tokens[5].replaceAll("\"", ""));
				DB.put(capcode, info);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public CapcodeInfo getCapcodeInfo(int capcode) {
		return DB.get(capcode);
	}

}
