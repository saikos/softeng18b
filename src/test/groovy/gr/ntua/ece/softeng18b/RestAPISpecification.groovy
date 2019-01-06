package gr.ntua.ece.softeng18b

import gr.ntua.ece.softeng18b.client.RestAPI
import gr.ntua.ece.softeng18b.client.model.Product
import gr.ntua.ece.softeng18b.client.rest.RestCallFormat

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

@Stepwise class RestAPISpecification extends Specification {
    
    private static final String HOST = System.getProperty("gretty.host")
    private static final String PORT = System.getProperty("gretty.port")
	
    @Shared RestAPI api = new RestAPI(HOST, PORT as Integer, false)
    
    def "User logins"() {
        when:
        api.login("user", "pass", RestCallFormat.JSON)
        
        then:
        api.isLoggedIn()
    }
    
    def "User adds product" () {
        when:
        Product sent = new Product(
            name       : "Product",
            description: "Description",
            category   : "Category",
            tags       : ["x", "y", "z"],
            withdrawn  : false
        )
        Product returned = api.postProduct(sent, RestCallFormat.JSON)
        
        then:
        returned.name == sent.name &&
        returned.description == sent.description &&
        returned.category == sent.category &&
        returned.tags == sent.tags &&
        returned.withdrawn == sent.withdrawn
    }
}

