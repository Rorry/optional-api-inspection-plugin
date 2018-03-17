import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OptionalStream {
    public void testCallChainsWarning(List<String> ids) {
        List<Integer> integers = <warning descr="Stream chain of optionals can be simplified">ids.stream()
                .map(this::helperMap)
                .filter(Optional::isPresent)
                .map(Optional::get)</warning>
                .collect(Collectors.toList());
    }

    public void testCallChainsWithSpacesWarning(List<String> ids) {
        List<Integer> integers = <warning descr="Stream chain of optionals can be simplified">ids.stream()
                .map(this::helperMap)
                .filter(Optional    :: isPresent)
                .map(Optional
                        :: get)</warning>
                .collect(Collectors.toList());
    }

    public void testCallChainsWithPackageNameWarning(List<String> ids) {
        List<Integer> integers = <warning descr="Stream chain of optionals can be simplified">ids.stream()
                .map(this::helperMap)
                .filter(java.util.Optional :: isPresent)
                .map(java.util. Optional::get)</warning>
                .collect(Collectors.toList());
    }

    public void testCallChainsWithLambdaWarning(List<String> ids) {
        List<Integer> integers = <warning descr="Stream chain of optionals can be simplified">ids.stream()
                .map(this::helperMap)
                .filter(opt -> opt.isPresent())
                .map(opt -> opt.get())</warning>
                .collect(Collectors.toList());
    }

    private Optional<Integer> helperMap(String id) {
        return Optional.of(0);
    }
}
