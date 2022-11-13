import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Immutable {

    public static void main(String[] args) {
        final Container<String> data = new Container<>(Arrays.asList("h", "2", "2"));
        System.out.println(data);
        final Container<String> data2 = data.addThing("thing1");
        System.out.println(data2);
        data2.getStreamOfThings()
                .forEach(System.out::println);
        data2.getStreamOfThings()
                .map(t -> "ttt..." + t + "...")
                .forEach(System.out::println);

        System.out.println("-----------");

        new Container<String>()
                .addThing("1")
                .addThing("r2")
                .addThing("3")
                .addThing("3")
                .addThings(Arrays.asList("5", "7", "9"))
                .removeThing("9")
                .getStreamOfThings()
//                .peek(System.out::print)
                .mapToInt(thing -> safeParseInt(thing).orElse(0))
                .flatMap(i -> IntStream.of(i, i + 1))
                .map(i -> 2 * i)
                .sorted()
                .forEach(System.out::println);

        System.out.println("-----------");

        final Container<Integer> bigBoy = new Container<>(
                IntStream.iterate(0, x -> x + 1)
                        .boxed()
                        .limit(1000000L)
                        .collect(Collectors.toList())
        );

        bigBoy.removeThing(1);
//        bigBoy.getStreamOfThings().forEach(System.out::println);
    }

    public static Optional<Integer> safeParseInt(Object in) {
        try {
            return Optional.of(Integer.parseInt(in.toString()));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }

    public static class Container<T> {
        private final Set<T> things;

        Container(Collection<T> things) {
            this.things = new LinkedHashSet<>(things);
        }

        Container() {
            this(Collections.emptySet());
        }

        public Stream<T> getStreamOfThings() {
            return things.stream();
        }

        public Container<T> addThing(T thing) {
            return new Container<>(Stream.concat(
                    things.stream(),
                    Stream.of(thing)
            ).collect(Collectors.toSet())
            );
        }

        public Container<T> removeThing(T toRemove) {
            return new Container<>(things.stream()
                    .filter(t -> !t.equals(toRemove))
                    .collect(Collectors.toSet())
            );
        }

        public Container<T> addThings(Collection<T> otherThings) {
            return new Container<>(Stream.concat(
                    things.stream(),
                    otherThings.stream()
            ).collect(Collectors.toSet())
            );
        }

        @Override
        public String toString() {
            return things.toString();
        }
    }
}
