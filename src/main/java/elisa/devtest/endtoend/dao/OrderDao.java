package elisa.devtest.endtoend.dao;

import elisa.devtest.endtoend.model.Customer;
import elisa.devtest.endtoend.model.Order;
import elisa.devtest.endtoend.model.OrderLine;
import elisa.devtest.endtoend.model.ProductDto;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;


import java.util.Collections;
import java.util.List;

public class OrderDao {


    public List<Order> findOrders() {
        try {
            return createJdbcTemplate().query("select * from orders", (resultSet, rowNumber) -> new Order(resultSet.getLong("order_id"), findCustomer(resultSet.getLong("customer_id")), findOrderLines(resultSet.getLong("order_id"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }

    private List<OrderLine> findOrderLines(long orderId) {
        try {
            return createJdbcTemplate().query("select * from order_line where order_id = ?", new Object[]{orderId}, (resultSet, rowNumber) -> new OrderLine(resultSet.getLong("order_line_id"), resultSet.getString("product_id"), resultSet.getString("product_name"), resultSet.getInt("quantity")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }

    public Customer findCustomer(final long customerId) {
        try {
            return createJdbcTemplate().queryForObject("select * from customer where customer_id = ?", new Object[]{customerId}, (resultSet, rowNumber) -> new Customer(resultSet.getLong("customer_id"), resultSet.getString("company_name"), resultSet.getString("street"), resultSet.getString("postal_code"), resultSet.getString("city"), resultSet.getString("country")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Customer();
    }

    private JdbcTemplate createJdbcTemplate() {
        return new JdbcTemplate(DBConnection.getDataSource());
    }
    
    public void addOrder(String companyName, String street, String postalCode, String city, String country, String productID, String productName){
    	//Check if companyName already exists, use pre-existing customer_id and contact details if so
    	Long returningCustomerID = null;
    	try {
    		int count = createJdbcTemplate().queryForObject("select count(customer_id) from customer where company_name='" + companyName + "'", Integer.class);
    		if(count != 0){
    			returningCustomerID = createJdbcTemplate().queryForObject("select customer_id from customer where company_name='" + companyName + "'", Long.class);
    		}
    	} catch (EmptyResultDataAccessException e) {
    		e.printStackTrace();
    	}
    	if(returningCustomerID != null){
        	String newOrder = "INSERT INTO ORDERS (CUSTOMER_ID) VALUES (" + returningCustomerID +")";
        	createJdbcTemplate().update(newOrder);
    	} else {
        	String newCustomer = "INSERT INTO CUSTOMER (COMPANY_NAME, STREET, POSTAL_CODE, CITY, COUNTRY) VALUES ('" + companyName + "', '" + street + "', '" + postalCode + "', '" + city + "', '" + country + "')";
        	createJdbcTemplate().update(newCustomer);
        	long newCustomerID = createJdbcTemplate().queryForObject("select max(customer_id) from customer", Long.class);
        	String newOrder = "INSERT INTO ORDERS (CUSTOMER_ID) VALUES (" + newCustomerID +")";
        	createJdbcTemplate().update(newOrder);
    	}
    	long newOrderID = createJdbcTemplate().queryForObject("select max(order_id) from orders", Long.class);
    	
    		
    	String newOrderLine = "INSERT INTO ORDER_LINE (ORDER_ID, PRODUCT_ID, PRODUCT_NAME, QUANTITY) VALUES ('"+ newOrderID + "', '" + productID + "', '" + productName + "', '1')";
    	createJdbcTemplate().update(newOrderLine);
    }
}
