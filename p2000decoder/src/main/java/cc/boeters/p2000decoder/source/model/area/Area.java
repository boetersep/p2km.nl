package cc.boeters.p2000decoder.source.model.area;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

public abstract class Area<A extends Area<?>> {

	private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
	private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

	public abstract String getName();

	@JsonProperty
	public String getSlug() {
		String nowhitespace = WHITESPACE.matcher(getName()).replaceAll("-");
		String normalized = Normalizer.normalize(nowhitespace, Form.NFD);
		String slug = NONLATIN.matcher(normalized).replaceAll("");
		return slug.toLowerCase(Locale.ENGLISH);
	}

	@JsonProperty
	public final String getContextualSlug() {
		StringBuilder sb = new StringBuilder();
		Area<?> coveringArea = this;
		sb.insert(0, coveringArea.getSlug());
		sb.insert(0, "/");
		while ((coveringArea = coveringArea.getCoveringArea()) != null) {
			sb.insert(0, coveringArea.getSlug());
			sb.insert(0, "/");
		}
		return sb.toString();
	}

	public abstract Integer getId();

	@JsonIgnore
	public abstract List<A> getSubAreas();

	@JsonIgnore
	public abstract Area<?> getCoveringArea();

	public A findSubAreaBySlug(String slug) {
		List<A> subAreas = getSubAreas();
		for (A a : subAreas) {
			if (slug.equals(a.getSlug())) {
				return a;
			}
		}
		return null;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getId(), getName());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Area<?> other = (Area<?>) obj;
		return Objects.equal(getId(), other.getId()) && Objects.equal(getName(), other.getName());
	}

	public static class AreaIdAdapter extends XmlAdapter<String, Integer> {

		@Override
		public String marshal(Integer id) throws Exception {
			if (id == null)
				return "";
			return id.toString();
		}

		@Override
		public Integer unmarshal(String id) throws Exception {
			return Integer.valueOf(id);
		}
	}

}
