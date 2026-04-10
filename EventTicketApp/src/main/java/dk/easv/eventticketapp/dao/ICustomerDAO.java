package dk.easv.eventticketapp.dao;

import dk.easv.eventticketapp.be.Customer;

public interface ICustomerDAO {
    Customer createCustomer(Customer customer) throws Exception;
    Customer getByEmail(String email) throws Exception;
    void delete(int customerId) throws Exception;
}