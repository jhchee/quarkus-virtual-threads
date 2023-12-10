import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.jboss.logging.Logger;


@Path("/")
public class DelayController {
    private static final Logger log = Logger.getLogger(DelayController.class);

    @GET
    @Path("delayOnVirtualThread/{delay}")
    @RunOnVirtualThread
    public int delayOnVirtualThread(@PathParam("delay") int delay) throws InterruptedException {
        log.info("Delaying for " + delay + " seconds");
        Thread.sleep(delay * 1000L);
        return delay;
    }

    @GET
    @Path("delayOnNormalThread/{delay}")
    public int delayOnThread(@PathParam("delay") int delay) throws InterruptedException {
        log.info("Delaying for " + delay + " seconds");
        Thread.sleep(delay * 1000L);
        return delay;
    }

    @GET
    @Path("delayWithError/{delay}")
    public int delayWithError(@PathParam("delay") int delay) throws InterruptedException {
        log.info("Delaying for " + delay + " seconds");
        Thread.sleep(delay * 1000L);
        throw new RuntimeException("Error");
    }
}
