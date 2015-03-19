package cc.boeters.p2000monitor.archive;

import static cc.boeters.p2000monitor.archive.PostcodeQueryBuilder.newPostcodeQuery;
import junit.framework.Assert;

import org.junit.Test;

import cc.boeters.p2000monitor.archive.PostcodeQueryBuilder.MatchType;
import cc.boeters.p2000monitor.archive.PostcodeQueryBuilder.MessageSource;
import cc.boeters.p2000monitor.archive.PostcodeQueryBuilder.PostcodeQuery;
import cc.boeters.p2000monitor.model.CapcodeInfo;
import cc.boeters.p2000monitor.model.Message;

public class PostcodeQueryBuilderTest {

	@Test
	public void testFive() {
		Message message = new Message();
		message.setMessage("Er is een hond ontsnapt uit het dierenasiel in Kalverstraat");

		PostcodeQuery query = newPostcodeQuery().mapColumn("street",
				MessageSource.MESSAGE, message, MatchType.LIKE).get();

		Assert.assertEquals(
				"SELECT * FROM postcode WHERE ( ' Er is een hond ontsnapt uit het dierenasiel in Kalverstraat ' LIKE CONCAT('% ', street ,' %') )",
				query.toString());

	}

	@Test
	public void testOne() {
		PostcodeQuery query = newPostcodeQuery()
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

		PostcodeQuery postcodeQuery = newPostcodeQuery()
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

		PostcodeQuery query = newPostcodeQuery()
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

		PostcodeQuery query = newPostcodeQuery()
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

		PostcodeQuery query = newPostcodeQuery().mapColumn("city",
				MessageSource.REGION, message, MatchType.EXACT).get();

		Assert.assertEquals(
				"SELECT * FROM postcode WHERE ( 'Amsterdam' = city OR 'Rotterdam' = city )",
				query.toString());
	}

}
