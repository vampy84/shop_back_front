package elisa.devtest.endtoend;

import elisa.devtest.endtoend.dao.OrderDao;
import elisa.devtest.endtoend.model.Order;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.util.SystemPropertyUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

@Path("/orders")
public class OrderResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Order> getOrders() {
        return new OrderDao().findOrders();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void addOrder(String input){
    	String companyName, street, postalCode, city, country, productID, productName;
    	Map<String, String> map = new HashMap();

    	// Splitting the input string and assigning to the appropriate variables
    	
    	for(String data : input.split("&")){
    		map.put(data.split("=")[0], data.split("=")[1]);
    	}
    	for(String key : map.keySet()){
    		String value = map.get(key);
    		String newValue = value.replaceAll("\\+", " ");
    		map.put(key, newValue);
    	}

    	companyName = map.get("companyName");
    	street = map.get("street");
    	postalCode = map.get("postalCode");
    	city = map.get("city");
    	country = map.get("country");
    	productID = map.get("productID");
    	productName = map.get("productName");
   	
    	new OrderDao().addOrder(companyName, street, postalCode, city, country, productID, productName);
    }
}
