package cc.boeters.p2000decoder.source;

import cc.boeters.p2000decoder.source.model.CapcodeInfo;

public interface CapcodeDatabase {

	CapcodeInfo getCapcodeInfo(int capcode);

}
