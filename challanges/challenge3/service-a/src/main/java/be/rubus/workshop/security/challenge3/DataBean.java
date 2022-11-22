package be.rubus.workshop.security.challenge3;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

@RequestScoped
@Named
public class DataBean {

    @Inject
    private TokenCreator tokenCreator;

        private WebTarget remoteTarget;

    @PostConstruct
    public void init() {
        // FIXME Within production systems, this URL needs to be read from External configuration like MicroProfile Config.
        remoteTarget = ClientBuilder.newClient().target("http://localhost:8180/service-b/data/protected");
    }

    public String getRemoteValue() {
        String jwt = tokenCreator.createToken();

        Response response = remoteTarget.request().header("Authorization", "Bearer " + jwt).buildGet().invoke();
        return String.format("Response from remote service : %s", response.readEntity(String.class));

    }
}
