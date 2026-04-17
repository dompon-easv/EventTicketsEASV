package dk.easv.eventticketapp.app;

import javafx.fxml.FXMLLoader;

public class ViewFactory {

    private final ApplicationServices services;

    public ViewFactory(ApplicationServices services) {
        this.services = services;
    }

    public FXMLLoader createLoader(String fxmlPath) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/dk/easv/eventticketapp/" + fxmlPath));

        loader.setControllerFactory(type -> {
            try {
                Object controller = type.getDeclaredConstructor().newInstance();

                if (controller instanceof ApplicationServicesAware aware) {
                    aware.setApplicationServices(services);
                }

                return controller;
            } catch (Exception e) {
                throw new RuntimeException("Could not create controller: " + type.getName(), e);
            }
        });

        return loader;
    }
}