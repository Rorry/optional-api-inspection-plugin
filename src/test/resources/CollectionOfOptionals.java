import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Collection;

public class CollectionOfOptionals {
    public void test1() {
        <warning descr="Collection contains 'Optional'">List<Optional<String>></warning> list = Collections.emptyList();
    }

    public void test2() {
        <warning descr="Collection contains 'Optional'">Collection<Optional<String>></warning> list = Collections.emptyList();
    }
}
