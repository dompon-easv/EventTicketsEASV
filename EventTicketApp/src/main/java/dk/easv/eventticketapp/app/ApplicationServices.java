package dk.easv.eventticketapp.app;

import dk.easv.eventticketapp.bll.*;
import dk.easv.eventticketapp.dao.*;

public class ApplicationServices {

    private final AuthenticationLogic authenticationLogic;
    private final UserManager userManager;
    private final EventLogic eventLogic;
    private final EventCoordinatorLogic eventCoordinatorLogic;
    private final TicketTypeManager ticketTypeManager;
    private final TicketManager ticketManager;
    private final CustomerLogic customerLogic;
    private final VoucherLogic voucherLogic;


    public ApplicationServices() {
        IUserDAO userDAO = new UserDAO();
        ITicketTypeDAO ticketTypeDAO = new TicketTypeDAO();
        ITicketDAO ticketDAO = new TicketDAO();
        IVoucherDAO voucherDAO = new VoucherDAO();
        ICustomerDAO customerDAO = new CustomerDAO();
        IEventCoordinatorDAO eventCoordinatorDAO = new EventCoordinatorDAO();
        IEventDAO eventDAO = new EventDAO();

        this.authenticationLogic = new AuthenticationLogic(userDAO);
        this.userManager = new UserManager(userDAO);
        this.eventLogic = new EventLogic();
        this.eventCoordinatorLogic = new EventCoordinatorLogic();
        this.ticketTypeManager = new TicketTypeManager(ticketTypeDAO);
        this.ticketManager = new TicketManager(ticketDAO, ticketTypeDAO, customerDAO, eventDAO);
        this.customerLogic = new CustomerLogic();
        this.voucherLogic = new VoucherLogic(voucherDAO, eventCoordinatorDAO);
    }

    public AuthenticationLogic getAuthenticationLogic() {
        return authenticationLogic;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public EventLogic getEventLogic() {
        return eventLogic;
    }

    public EventCoordinatorLogic getEventCoordinatorLogic() {
        return eventCoordinatorLogic;
    }

    public TicketTypeManager getTicketTypeManager() {
        return ticketTypeManager;
    }

    public TicketManager getTicketManager() {
        return ticketManager;
    }

    public CustomerLogic getCustomerLogic() {
        return customerLogic;
    }

    public VoucherLogic getVoucherLogic() {
        return voucherLogic;
    }
}