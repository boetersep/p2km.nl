package cc.boeters.p2000monitor.archive;

import org.junit.Before;
import org.junit.Test;

public class AbbreviationsServiceTest {

	private AbbreviationsService service;

	@Before
	public void setUp() {
		service = new AbbreviationsService();
	}

	@Test
	public void test() {
		String value = "A1 Tweemolentjeskade DLT : Vtg: 15101 Ritnr: 39172";

		String translate = service.translate(value);

		System.out.println(translate);

	}

}
