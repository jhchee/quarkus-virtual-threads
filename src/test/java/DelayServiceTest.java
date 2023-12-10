import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;


@QuarkusTest
class DelayServiceTest {

    @Inject
    @RestClient
    DelayService delayService;
    private Logger log = Logger.getLogger(DelayServiceTest.class);

    @Test
    public void testStructuredConcurrency_ShutdownOnSuccess() throws InterruptedException, ExecutionException {
        Instant now = Instant.now();

        try (var scope = new StructuredTaskScope.ShutdownOnSuccess<Integer>()) {
            scope.fork(() -> delayService.delay(5));
            scope.fork(() -> delayService.delay(1));
            scope.fork(() -> delayService.delay(2));
            scope.join();
            Integer firstResult = scope.result();
            log.info("First result: " + firstResult);
        }
        log.info("Executed in " + Duration.between(now, Instant.now()).getSeconds() + " seconds.");
    }

    @Test
    public void testStructuredConcurrency_ShutdownOnFailure() throws InterruptedException {
        Instant now = Instant.now();

        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var subtask1 = scope.fork(() -> delayService.delay(5));
            var subtask2 = scope.fork(() -> delayService.delay(1));
            var subtask3 = scope.fork(() -> delayService.delayWithError(2));
            scope.join();

            Throwable subtask1Exception = subtask3.exception();
            log.error(subtask1Exception.getMessage(), subtask1Exception);
            log.info("Subtask 1 state: " + subtask1.state());
            log.info("Subtask 2 state: " + subtask2.state());
        }
        log.info("Executed in " + Duration.between(now, Instant.now()).getSeconds() + " seconds.");
    }

    @Test
    public void testStructuredConcurrency_ShutdownOnTimeout() throws Exception {
        Instant now = Instant.now();

        try (var scope = new SuccessResultsScope<>()) {
            scope.fork(() -> delayService.delay(5));
            scope.fork(() -> delayService.delay(3));
            scope.fork(() -> delayService.delayWithError(2));
            scope.join();

            var results = scope.resultOrElseThrow(() -> new Exception("All subtasks failed"));
            System.out.println(results);
        }
        log.info("Executed in " + Duration.between(now, Instant.now()).getSeconds() + " seconds.");
    }
}