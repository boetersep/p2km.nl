package cc.boeters.p2000decoder.source;

public interface MonitorSource {

	void stop();

	void addListener(MonitorListener listener);

	void start();

}
