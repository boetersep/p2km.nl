package cc.boeters.p2000monitor.archive;

import static cc.boeters.p2000monitor.archive.HectopaalQueryBuilder.newHectopaalQuery;
import static cc.boeters.p2000monitor.archive.PostcodeQueryBuilder.newPostcodeQuery;
import junit.framework.Assert;

import org.junit.Test;

import cc.boeters.p2000monitor.archive.QueryBuilder.MatchType;
import cc.boeters.p2000monitor.archive.QueryBuilder.MessageSource;
import cc.boeters.p2000monitor.archive.QueryBuilder.Query;
import cc.boeters.p2000monitor.model.CapcodeInfo;
import cc.boeters.p2000monitor.model.Message;

public class QueryBuilderTest {

	@Test
	public void testEight() {

		Message m = new Message();
		m.setMessage("PRIO 1 A15 R 140,5 Ochten HV Voertuig letsel RPN 8331 7091 INC 33538");

		Query query = newHectopaalQuery()
				.mapColumn("weg", MessageSource.MESSAGE, m, MatchType.LIKE)
				.and(newHectopaalQuery()
						.mapColumn("hectometrering_comma",
								MessageSource.MESSAGE, m, MatchType.LIKE)
						.or()
						.mapColumn("hectometrering_dot", MessageSource.MESSAGE,
								m, MatchType.LIKE))
				.and()
				.mapColumn("rpe_code", MessageSource.MESSAGE, m, MatchType.LIKE)
				.get();

		System.out.println(query);

	}

	@Test
	public void testFive() {
		Message message = new Message();
		message.setMessage("Er is een hond ontsnapt uit het dierenasiel in Kalverstraat");

		Query query = newPostcodeQuery().mapColumn("street",
				MessageSource.MESSAGE, message, MatchType.LIKE).get();

		Assert.assertEquals(
				"SELECT * FROM postcode WHERE ( ' Er is een hond ontsnapt uit het dierenasiel in Kalverstraat ' LIKE CONCAT('% ', street ,' %') )",
				query.toString());

	}

	@Test
	public void testOne() {
		Query query = newPostcodeQuery()
				.mapColumn("postcode", MatchType.EXACT, "1234AB")
				.and(newPostcodeQuery()
						.mapColumn("postcode", MatchType.EXACT, "1234CD").or()
						.mapColumn("postcode", MatchType.EXACT, "1234EF"))
				.get();

		Assert.assertEquals(
				"SELECT * FROM postcode WHERE postcode = '1234AB' AND ( postcode = '1234CD' OR postcode = '1234EF' )",
				query.toString());
	}

	@Test
	public void testSeven() {
		Message message = new Message();
		message.setMessage("P 1 WONINGBRAND Kruisweg 4 A VIA (schoorsteen) Eenh: OVD989 RV595 VIA593 HAG602");

		Query postcodeQuery = newPostcodeQuery()
				.mapColumn("street", MessageSource.MESSAGE, message,
						MatchType.LIKE)
				.and()
				.mapColumn("city", MessageSource.SECTOR, message,
						MatchType.EXACT).get();

		System.out.println(postcodeQuery);

	}

	@Test
	public void testSix() {
		Message message = new Message();
		message.setMessage("Er is een hond ontsnapt uit het dierenasiel in Kalverstraat in Amsterdam");

		Query query = newPostcodeQuery()
				.mapColumn("city", MessageSource.MESSAGE, message,
						MatchType.LIKE)
				.and()
				.mapColumn("street", MessageSource.MESSAGE, message,
						MatchType.LIKE).get();

		Assert.assertEquals(
				"SELECT * FROM postcode WHERE ( ' Er is een hond ontsnapt uit het dierenasiel in Kalverstraat in Amsterdam ' LIKE CONCAT('% ', city ,' %') ) AND ( ' Er is een hond ontsnapt uit het dierenasiel in Kalverstraat in Amsterdam ' LIKE CONCAT('% ', street ,' %') )",
				query.toString());

	}

	@Test
	public void testThree() {
		Message message = new Message();
		message.setMessage("Er is een hond ontsnapt uit het dierenasiel in Kalverstraat");
		CapcodeInfo ci1 = new CapcodeInfo();
		ci1.setRegion("Amsterdam");
		message.getGroup().add(ci1);
		CapcodeInfo ci2 = new CapcodeInfo();
		ci2.setRegion("Rotterdam");
		message.getGroup().add(ci2);

		Query query = newPostcodeQuery()
				.mapColumn("city", MessageSource.REGION, message,
						MatchType.EXACT)
				.and()
				.mapColumn("street", MessageSource.MESSAGE, message,
						MatchType.LIKE).get();

		Assert.assertEquals(
				"SELECT * FROM postcode WHERE ( 'Amsterdam' = city OR 'Rotterdam' = city ) AND ( ' Er is een hond ontsnapt uit het dierenasiel in Kalverstraat ' LIKE CONCAT('% ', street ,' %') )",
				query.toString());

	}

	@Test
	public void testTwo() {
		Message message = new Message();
		CapcodeInfo ci1 = new CapcodeInfo();
		ci1.setRegion("Amsterdam");
		message.getGroup().add(ci1);
		CapcodeInfo ci2 = new CapcodeInfo();
		ci2.setRegion("Rotterdam");
		message.getGroup().add(ci2);

		Query query = newPostcodeQuery().mapColumn("city",
				MessageSource.REGION, message, MatchType.EXACT).get();

		Assert.assertEquals(
				"SELECT * FROM postcode WHERE ( 'Amsterdam' = city OR 'Rotterdam' = city )",
				query.toString());
	}

}
