package dk.easv.eventticketapp.dao;

import dk.easv.eventticketapp.be.Customer;

public interface ICustomerDAO {
    Customer createCustomer(Customer customer) throws Exception;
}