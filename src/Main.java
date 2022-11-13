import java.util.LinkedHashMap;
import java.util.Random;
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

        FunctionThrows<Integer, Integer> f = x -> {
            if (x == 3) throw new CustomException("No!!");
            return x + 1;
        };
        try {
            System.out.println(f.apply(1));
            System.out.println(f.apply(3));
        } catch (CustomException e) {
            e.printStackTrace();
        }

        final Try<Integer> try1 = new Success<>(3);
        System.out.println(try1);

        final Try<Integer> try2 = try1.flatMap(x -> {
            if (x == 3) throw new CustomException("3!");
            else return new Success<>(x + 1);
        });
        System.out.println(try2);

        Try<Integer> try3 = Try(() -> 1);
        System.out.println(try3);

        final SupplierThrows<Integer> couldFailThunk = () -> {
            final int n = new Random().nextInt(7);
            if (n > 3) throw new CustomException(n + " is a problem!");
            else return n;
        };

        Try<Integer> try4 = Try(couldFailThunk);
        System.out.println(try4);

        System.out.println(try4.flatMap(x -> {
            final int r = x + 1;
            if (r > 3) throw new CustomException(r + " is a problem!");
            else return Try(() -> r);
        }));
    }

    static class CustomException extends Exception {
        public CustomException(String msg) {
            super(msg);
        }
    }

    @FunctionalInterface
    interface FunctionThrows<A, B> {
        B apply(A a) throws CustomException;
    }

    @FunctionalInterface
    interface SupplierThrows<T> {
        T get() throws CustomException;
    }

    interface Try<T> {
        <U> Try<U> flatMap(FunctionThrows<T, Try<U>> f);
    }

    static <T> Try<T> Try(SupplierThrows<T> thunk) {
        try {
            final T result = thunk.get();
            return new Success<>(result);
        } catch (Exception e) {
            return new Failure(e);
        }
    }

    static class Success<T> implements Try<T> {
        private final T t;

        public Success(T t) {
            this.t = t;
        }

        @Override
        public <U> Try<U> flatMap(FunctionThrows<T, Try<U>> f) {
            try {
                return f.apply(t);
            } catch (CustomException e) {
                return new Failure(e);
            }
        }

        @Override
        public String toString() {
            return "Success(" +
                    t +
                    ')';
        }
    }

    static class Failure implements Try {
        private final Exception e;

        public Failure(Exception e) {
            this.e = e;
        }

        public Try flatMap(FunctionThrows f) {
            return this;
        }

        @Override
        public String toString() {
            return "Failure(" +
                    e.getMessage() +
                    ')';
        }
    }
}
