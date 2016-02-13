package cc.boeters.p2000decoder.source.model.area;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import com.google.common.base.Objects;

public abstract class Area<A extends Area<?>> {

	private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
	private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

	public abstract String getName();

	public String getSlug() {
		String nowhitespace = WHITESPACE.matcher(getName()).replaceAll("-");
		String normalized = Normalizer.normalize(nowhitespace, Form.NFD);
		String slug = NONLATIN.matcher(normalized).replaceAll("");
		return slug.toLowerCase(Locale.ENGLISH);
	}

	public abstract Integer getId();

	public abstract List<A> getSubAreas();

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

}
