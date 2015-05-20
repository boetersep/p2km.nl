package cc.boeters.p2000monitor.processing.capcode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.boeters.p2000monitor.model.CapcodeInfo;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Singleton
public class MysqlCapcodeDatabase extends CacheLoader<Integer, CapcodeInfo>
		implements CapcodeDatabase {

	static final Logger LOG = LoggerFactory
			.getLogger(MysqlCapcodeDatabase.class);

	private static final String STATEMENT_FIND_BY_CAPCODE = "SELECT `discipline`, `region`, `sector`, `description`, `shortdesc` FROM `capcodes` WHERE `capcode` = ?";

	private final LoadingCache<Integer, CapcodeInfo> cache;

	@Resource(name = "jdbc/p2000metadata")
	private DataSource p2000metadata;

	public MysqlCapcodeDatabase() {
		cache = CacheBuilder.newBuilder().maximumSize(10000)
				.expireAfterWrite(1, TimeUnit.DAYS).build(this);
	}

	private CapcodeInfo createEmptyCapcodeInfo(int capcode) {
		CapcodeInfo capcodeInfo = new CapcodeInfo();
		capcodeInfo.setCapcode(capcode);
		capcodeInfo.setDescription(String.valueOf(capcode));
		return capcodeInfo;
	}

	@Override
	public CapcodeInfo getCapcodeInfo(int capcode) {
		try {
			return cache.get(capcode);
		} catch (ExecutionException e) {
			LOG.error("Unable to load capcode information from cache.", e);
			return createEmptyCapcodeInfo(capcode);
		}
	}

	@Override
	public CapcodeInfo load(Integer capcode) throws Exception {
		CapcodeInfo capcodeInfo = new CapcodeInfo();
		capcodeInfo.setCapcode(capcode);

		Connection connection = p2000metadata.getConnection();
		PreparedStatement prepareStatement = connection
				.prepareStatement(STATEMENT_FIND_BY_CAPCODE);
		prepareStatement.setInt(1, capcode);

		if (prepareStatement.execute()) {
			ResultSet resultSet = prepareStatement.getResultSet();
			if (resultSet.next()) {
				capcodeInfo.setDescription(resultSet.getString("description"));
				capcodeInfo.setDiscipline(resultSet.getString("discipline"));
				capcodeInfo.setRegion(resultSet.getString("region"));
				capcodeInfo.setSector(resultSet.getString("sector"));
				capcodeInfo.setShortdesc(resultSet.getString("shortdesc"));
			}
		}
		return capcodeInfo;
	}
}
