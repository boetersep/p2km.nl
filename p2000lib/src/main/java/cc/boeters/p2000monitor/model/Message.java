package cc.boeters.p2000monitor.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

	private final Map<String, Object> metadata;

	public Message() {
		group = new ArrayList<CapcodeInfo>(10);
		metadata = new HashMap<String, Object>();
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

	public Map<String, Object> getMetadata() {
		return metadata;
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

	@JsonIgnore
	public boolean isAlphaMessage() {
		return type == MessageType.ALPHA;
	}

	@JsonIgnore
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
		return "Message [capcode=" + capcode + ", type=" + type + ", date="
				+ date + ", time=" + time + ", timestamp=" + timestamp
				+ ", message=" + message + ", group=" + group + "]";
	}

}
