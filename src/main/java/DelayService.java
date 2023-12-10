import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(baseUri = "http://localhost:8080/")
public interface DelayService {

    @GET
    @Path("delayOnVirtualThread/{delay}")
    Integer delay(@PathParam("delay") int delay);


    @GET
    @Path("delayWithError/{delay}")
    Integer delayWithError(@PathParam("delay") int delay);
}
