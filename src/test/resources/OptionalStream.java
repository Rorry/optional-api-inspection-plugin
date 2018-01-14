import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OptionalStream {
    public void test(List<String> ids) {
        List<Integer> integers = <warning descr="Stream of optionals can be simplified">ids.stream()
                .map(this::helperMap)
                .filter(Optional::isPresent)
                .map(Optional::get)</warning>
                .collect(Collectors.toList());
    }

    private Optional<Integer> helperMap(String id) {
        return Optional.of(0);
    }
}
