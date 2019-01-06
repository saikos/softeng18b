package gr.ntua.ece.softeng18b.api;

import java.util.HashMap;
import java.util.Map;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

public class LoginResource extends ServerResource {
    
    @Override
    protected Representation post(Representation entity) throws ResourceException {
        //All login attempts succeed with the same token
        Map<String, Object> map = new HashMap<>();
        map.put("token", "321CBA");
        return new JsonMapRepresentation(map);
    }
}
