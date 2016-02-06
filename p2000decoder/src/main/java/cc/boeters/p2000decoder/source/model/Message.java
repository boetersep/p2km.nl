package cc.boeters.p2000decoder.source.model;

import java.util.ArrayList;
import java.util.List;

public class Message {

	public enum MessageType {
		GROUP, ALPHA
	}

	private int capcode;

	private String message;

	private MessageType type;

	private String date;

	private String time;

	private long timestamp;

	private final List<CapcodeInfo> group;

	private CapcodeInfo capcodeInfo;

	public Message() {
		group = new ArrayList<CapcodeInfo>(10);
	}

	public int getCapcode() {
		return capcode;
	}

	public CapcodeInfo getCapcodeInfo() {
		return capcodeInfo;
	}

	public String getDate() {
		return date;
	}

	public List<CapcodeInfo> getGroup() {
		return group;
	}

	public String getMessage() {
		return message;
	}

	public String getTime() {
		return time;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public MessageType getType() {
		return type;
	}

	public boolean isAlphaMessage() {
		return type == MessageType.ALPHA;
	}

	public boolean isGroupMessage() {
		return type == MessageType.GROUP;
	}

	public void setCapcode(int capcode) {
		this.capcode = capcode;
	}

	public void setCapcodeInfo(CapcodeInfo capcodeInfo) {
		this.capcodeInfo = capcodeInfo;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Message [capcode=" + capcode + ", type=" + type + ", date=" + date + ", time=" + time + ", timestamp="
				+ timestamp + ", message=" + message + ", group=" + group + "]";
	}

}
