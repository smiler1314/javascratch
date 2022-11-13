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

        System.out.println("Try to mutate List used to construct a Container...");
        final List<Integer> toMutate = new ArrayList<>(Arrays.asList(1,2,3));
        final Container<Integer> cantMutate = new Container<>(toMutate);
        System.out.println(cantMutate);
        toMutate.add(4);
        System.out.println(cantMutate);

        System.out.println("-----------");

        final Dictionary<Integer, String> dict1 = new Dictionary<>();
        final Dictionary<Integer, String> dict2 = dict1.addItem(1, "test1");
        System.out.println(dict2);
        System.out.println(dict2.removeItem(2));
        System.out.println(dict2.removeItem(1));
        System.out.println(dict2.lookupValue(5));
        System.out.println(dict2.lookupValue(1));
        final Dictionary<Integer, String> dict3 = dict2
                .addItem(2, "test2")
                .addItem(4, "test4")
                ;
        dict3.getAllEntriesStream()
                .map(e -> e.getKey() + " -> " + e.getValue())
                .forEach(System.out::println);

        System.out.println("-----------");
        System.out.println("Try to mutate Map used to construct a Dictionary...");

        final Map<Integer, String> mutaMap = new HashMap<>();
        mutaMap.put(1, "mut1");
        mutaMap.put(2, "mut2");

        final Dictionary<Integer, String> dict4 = new Dictionary<>(mutaMap);
        System.out.println(dict4);
        mutaMap.put(3, "mut3");
        System.out.println(dict4);

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

    static class Dictionary<K, V> {
        private final Map<K, V> dict;

        Dictionary(Map<K, V> dict) {
            this.dict = new HashMap<>(dict);
        }

        Dictionary() {
            this.dict = new HashMap<>();
        }

        public Dictionary<K, V> addItem(K key, V value) {
            Map<K, V> updated = new HashMap<>(dict);
            updated.put(key, value);
            return new Dictionary<>(updated);
        }

        public Dictionary<K, V> removeItem(K key) {
            Map<K, V> updated = new HashMap<>(dict);
            updated.remove(key);
            return new Dictionary<>(updated);
        }

        public Optional<V> lookupValue(K key) {
            return Optional.ofNullable(dict.get(key));
        }

        public Stream<Map.Entry<K, V>> getAllEntriesStream() {
            return dict.entrySet().stream();
        }

        @Override
        public String toString() {
            return dict.toString();
        }
    }
}
