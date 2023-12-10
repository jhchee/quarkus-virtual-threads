import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.StructuredTaskScope;
import java.util.function.Supplier;

public class SuccessResultsScope<T> extends StructuredTaskScope<T> {
    private final List<T> results = new ArrayList<>();
    private final List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());

    @Override
    protected void handleComplete(Subtask<? extends T> subtask) {
        switch (subtask.state()) {
            case UNAVAILABLE -> {
                // Ignore
            }
            case SUCCESS -> {
                T result = subtask.get();
                synchronized (this) {
                    results.add(result);
                }
            }
            case FAILED -> exceptions.add(subtask.exception());
        }
    }

    public <X extends Throwable> List<T> resultOrElseThrow(Supplier<? extends X> exceptionSupplier)
            throws X {
        ensureOwnerAndJoined();
        if (!results.isEmpty()) {
            return results;
        } else {
            X exception = exceptionSupplier.get();
            exceptions.forEach(exception::addSuppressed);
            throw exception;
        }
    }
}