import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CollectionOfOptionals {
    public void test(List<String> ids) {
        <warning descr="Potentially confusing code constructs">List<Optional<String>></warning> list = Collections.emptyList();
    }
}
