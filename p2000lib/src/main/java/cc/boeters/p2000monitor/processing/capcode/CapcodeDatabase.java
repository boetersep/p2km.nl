package cc.boeters.p2000monitor.processing.capcode;

import cc.boeters.p2000monitor.model.CapcodeInfo;

public interface CapcodeDatabase {

	CapcodeInfo getCapcodeInfo(int capcode);

}
