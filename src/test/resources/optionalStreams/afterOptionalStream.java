// "Stream chain of optionals can be simplified" "true"
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class MyClass {
    public void test(List<String> ids) {
        List<Integer> integers = ids.stream()
                .map(this::helperMap).flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    private Optional<Integer> helperMap(String id) {
        return Optional.of(0);
    }
}
