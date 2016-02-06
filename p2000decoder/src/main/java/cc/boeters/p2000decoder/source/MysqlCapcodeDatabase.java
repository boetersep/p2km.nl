package cc.boeters.p2000decoder.source;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.boeters.p2000decoder.source.model.CapcodeInfo;
import jersey.repackaged.com.google.common.cache.CacheBuilder;
import jersey.repackaged.com.google.common.cache.CacheLoader;
import jersey.repackaged.com.google.common.cache.LoadingCache;

public class MysqlCapcodeDatabase extends CacheLoader<Integer, CapcodeInfo> implements CapcodeDatabase {

	static final Logger LOG = LoggerFactory.getLogger(MysqlCapcodeDatabase.class);

	private static final String STATEMENT_FIND_BY_CAPCODE = "SELECT `discipline`, `region`, `sector`, `description`, `shortdesc` FROM `capcodes` WHERE `capcode` = ?";

	private final LoadingCache<Integer, CapcodeInfo> cache;

	private DataSource dataSource;

	public MysqlCapcodeDatabase(DataSource dataSource) {
		this.dataSource = dataSource;
		cache = CacheBuilder.newBuilder().maximumSize(10000).expireAfterWrite(1, TimeUnit.DAYS).build(this);
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

		Connection connection = dataSource.getConnection();
		PreparedStatement prepareStatement = connection.prepareStatement(STATEMENT_FIND_BY_CAPCODE);
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
		connection.close();
		return capcodeInfo;
	}
}
