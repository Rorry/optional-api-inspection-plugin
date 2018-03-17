import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;

public class CollectionOfOptionals {
    public void testListOfOptionalWarning() {
        <warning descr="Collection contains 'Optional'">List<Optional<String>></warning> list = Collections.emptyList();
    }

    public void testCollectionOfOptionalWarning() {
        <warning descr="Collection contains 'Optional'">Collection<Optional<String>></warning> list = Collections.emptyList();
    }

    public void testCustomListNoWarning() {
        class MyList<T> extends ArrayList<String> {
        }
        MyList<OptionalDouble> list = new MyList<>();
    }

    public void testMapOfOptionalNoWarning() {
        Map<Optional<String>, String> list = Collections.emptyMap();
    }
}
