package cc.boeters.p2000monitor.processing.classifier;

import cc.boeters.p2000monitor.model.Message;

public class SearchHit {
	private Message _source;

	public SearchHit() {
	}

	public Message get_source() {
		return _source;
	}

	public void set_source(Message _source) {
		this._source = _source;
	}

}
