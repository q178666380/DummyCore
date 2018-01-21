package DummyCore.Utils;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;

import javax.annotation.Nullable;

import com.google.common.base.Equivalence;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * Guava's {@code Predicates}, but with Java BiPredicates.
 * @author TheLMiffy1111
 */
public class BiPredicates {
	private BiPredicates() {}

	public static <T, U> BiPredicate<T, U> alwaysTrue() {
		return ObjectBiPredicate.ALWAYS_TRUE.withNarrowedType();
	}

	public static <T, U> BiPredicate<T, U> alwaysFalse() {
		return ObjectBiPredicate.ALWAYS_FALSE.withNarrowedType();
	}

	public static <T, U> BiPredicate<T, U> not(BiPredicate<T, U> predicate) {
		return new NotBiPredicate<T, U>(predicate);
	}

	public static <T, U> BiPredicate<T, U> and(Iterable<? extends BiPredicate<? super T, ? super U>> components) {
		return new AndBiPredicate<T, U>(defensiveCopy(components));
	}

	public static <T, U> BiPredicate<T, U> and(BiPredicate<? super T, ? super U>... components) {
		return new OrBiPredicate<T, U>(defensiveCopy(components));
	}

	public static <T, U> BiPredicate<T, U> and(BiPredicate<? super T, ? super U> first, BiPredicate<? super T, ? super U> second) {
		return new OrBiPredicate<T, U>(asList(checkNotNull(first), checkNotNull(second)));
	}

	public static <T, U> BiPredicate<T, U> or(Iterable<? extends BiPredicate<? super T, ? super U>> components) {
		return new OrBiPredicate<T, U>(defensiveCopy(components));
	}

	public static <T, U> BiPredicate<T, U> or(BiPredicate<? super T, ? super U>... components) {
		return new OrBiPredicate<T, U>(defensiveCopy(components));
	}

	public static <T, U> BiPredicate<T, U> or(BiPredicate<? super T, ? super U> first, BiPredicate<? super T, ? super U> second) {
		return new OrBiPredicate<T, U>(asList(checkNotNull(first), checkNotNull(second)));
	}

	enum ObjectBiPredicate implements BiPredicate<Object, Object> {
		/** @see BiPredicates#alwaysTrue() */
		ALWAYS_TRUE {
			@Override
			public boolean test(Object o1, Object o2) {
				return true;
			}

			@Override
			public String toString() {
				return "Predicates.alwaysTrue()";
			}
		},
		/** @see BiPredicates#alwaysFalse() */
		ALWAYS_FALSE {
			@Override
			public boolean test(Object o1, Object o2) {
				return false;
			}

			@Override
			public String toString() {
				return "Predicates.alwaysFalse()";
			}
		};

		@SuppressWarnings("unchecked")
		<T, U> BiPredicate<T, U> withNarrowedType() {
			return (BiPredicate<T, U>) this;
		}
	}

	/** @see BiPredicates#not(BiPredicate) */
	private static class NotBiPredicate<T, U> implements BiPredicate<T, U> {
		final BiPredicate<T, U> predicate;

		NotBiPredicate(BiPredicate<T, U> predicate) {
			this.predicate = checkNotNull(predicate);
		}

		@Override
		public boolean test(T t, U u) {
			return !predicate.test(t, u);
		}

		@Override
		public int hashCode() {
			return ~predicate.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof NotBiPredicate) {
				NotBiPredicate<?, ?> that = (NotBiPredicate<?, ?>) obj;
				return predicate.equals(that.predicate);
			}
			return false;
		}

		@Override
		public String toString() {
			return "BiPredicates.not(" + predicate + ")";
		}
	}

	private static final Joiner COMMA_JOINER = Joiner.on(',');

	/** @see BiPredicates#and(Iterable) */
	private static class AndBiPredicate<T, U> implements BiPredicate<T, U> {
		private final List<? extends BiPredicate<? super T, ? super U>> components;

		private AndBiPredicate(List<? extends BiPredicate<? super T, ? super U>> components) {
			this.components = components;
		}

		@Override
		public boolean test(T t, U u) {
			for (int i = 0; i < components.size(); i++) {
				if (!components.get(i).test(t, u)) {
					return false;
				}
			}
			return true;
		}

		@Override
		public int hashCode() {
			// add a random number to avoid collisions with OrPredicate
			return components.hashCode() + 0x12472c2c;
		}

		@Override
		public boolean equals(@Nullable Object obj) {
			if (obj instanceof AndBiPredicate) {
				AndBiPredicate<?, ?> that = (AndBiPredicate<?, ?>) obj;
				return components.equals(that.components);
			}
			return false;
		}

		@Override
		public String toString() {
			return "Predicates.and(" + COMMA_JOINER.join(components) + ")";
		}
	}

	/** @see BiPredicates#or(Iterable) */
	private static class OrBiPredicate<T, U> implements BiPredicate<T, U> {
		private final List<? extends BiPredicate<? super T, ? super U>> components;

		private OrBiPredicate(List<? extends BiPredicate<? super T, ? super U>> components) {
			this.components = components;
		}

		@Override
		public boolean test(T t, U u) {
			for (int i = 0; i < components.size(); i++) {
				if (components.get(i).test(t, u)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public int hashCode() {
			return components.hashCode() + 0x053c91cf;
		}

		@Override
		public boolean equals(@Nullable Object obj) {
			if(obj instanceof OrBiPredicate) {
				OrBiPredicate<?, ?> that = (OrBiPredicate<?, ?>) obj;
				return components.equals(that.components);
			}
			return false;
		}

		@Override
		public String toString() {
			return "BiPredicates.or(" + COMMA_JOINER.join(components) + ")";
		}
	}

	private static <T, U> List<BiPredicate<? super T, ? super U>> asList(BiPredicate<? super T, ? super U> first, BiPredicate<? super T, ? super U> second) {
		return Arrays.<BiPredicate<? super T, ? super U>>asList(first, second);
	}

	private static <T> List<T> defensiveCopy(T... array) {
		return defensiveCopy(Arrays.asList(array));
	}

	static <T> List<T> defensiveCopy(Iterable<T> iterable) {
		ArrayList<T> list = new ArrayList<T>();
		for (T element : iterable) {
			list.add(checkNotNull(element));
		}
		return list;
	}
}
