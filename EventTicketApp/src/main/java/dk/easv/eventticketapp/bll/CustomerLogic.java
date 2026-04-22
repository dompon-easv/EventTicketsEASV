package dk.easv.eventticketapp.bll;

import dk.easv.eventticketapp.be.Customer;
import dk.easv.eventticketapp.dao.CustomerDAO;
import dk.easv.eventticketapp.dao.ICustomerDAO;

public class CustomerLogic {

    private final ICustomerDAO customerDAO;

    public CustomerLogic(ICustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    public Customer createCustomer(String name, String email) throws Exception {

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer email cannot be empty");
        }

        Customer existing = customerDAO.getByEmail(email.trim());
        if (existing != null) {
            return existing;
        }

        Customer customer = new Customer(name.trim(), email.trim());
        return customerDAO.createCustomer(customer);
    }
}