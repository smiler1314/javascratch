import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        IntStream.of(1, 1, 1, 2, 2, 3, 4, 5, 5, 6, 7, 9)
                .boxed()
                .collect(Collectors.groupingBy(i -> i, Collectors.toList()))
                .entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + e.getValue().size())
                .forEach(System.out::println)
        ;

        System.out.println("-------------------");

        IntStream.of(1, 1, 1, 2, 2, 3, 4, 5, 5, 6, 7, 9)
                .boxed()
                .collect(Collectors.toMap(k -> k, v -> 1, (item, dup) -> item + 1))
                .entrySet()
                .forEach(System.out::println)
        ;

        System.out.println("-------------------");

        IntStream.of(1, 1, 1, 2, 2, 3, 4, 5, 5, 6, 7, 9)
                .boxed()
                .reduce(new LinkedHashMap<Integer, Integer>(), (acc, el) -> {
                    acc.merge(el, 1, Integer::sum);
                    return acc;
                }, (res1, res2) -> {
                    res1.putAll(res2);
                    return res1;
                })
                .entrySet()
                .forEach(System.out::println)
        ;

        System.out.println("-------------------");

    }
}
