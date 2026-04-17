package dk.easv.eventticketapp.gui.adminControllers;

import dk.easv.eventticketapp.Application;
import dk.easv.eventticketapp.app.ApplicationServices;
import dk.easv.eventticketapp.app.ApplicationServicesAware;
import dk.easv.eventticketapp.be.User;
import dk.easv.eventticketapp.be.enums.UserRole;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

public class UserManagementController implements ApplicationServicesAware {

   @FXML private TableView<User> userTable;
   @FXML private TableColumn<User, String> nameColumn;
    @FXML private TableColumn<User, String> surnameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, UserRole> roleColumn;

    private ApplicationServices services;

    @Override
    public void setApplicationServices(ApplicationServices services) {
        this.services = services;
    }

    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        roleColumn.setCellFactory(column -> new javafx.scene.control.TableCell<User, UserRole>() {
            private final javafx.scene.control.Label badge = new javafx.scene.control.Label();

            @Override
            protected void updateItem(UserRole role, boolean empty) {
                super.updateItem(role, empty);

                if (empty || role == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                badge.getStyleClass().clear();
                badge.getStyleClass().add("role-badge");

                switch (role) {
                    case ADMIN -> {
                        badge.setText("Admin");
                        badge.getStyleClass().add("role-admin");
                    }
                    case COORDINATOR -> {
                        badge.setText("Coordinator");
                        badge.getStyleClass().add("role-coordinator");
                    }
                    default -> {
                        badge.setText(role.toString());
                        badge.getStyleClass().add("role-user");
                    }
                }

                setText(null);
                setGraphic(badge);
            }
        });
    }

    public void loadUsers()
    {
        ObservableList<User> userList = FXCollections.observableArrayList(services.getUserManager().getAllUsers());
        userTable.setItems(userList);
    }



    public void onAddUser(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("gui/adminViews/AddEditUser.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        AddEditUserController controller = fxmlLoader.getController();
        controller.init(services.getUserManager());
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.showAndWait();
        loadUsers();
    }


    public void handleDeleteUser(ActionEvent actionEvent) {
        User user = userTable.getSelectionModel().getSelectedItem();
        if (user != null) {
            services.getEventCoordinatorLogic().deleteUser(user.getId());
            services.getUserManager().deleteUser(user.getId());
            loadUsers();
        }
    }

    public void handleEditUser(ActionEvent actionEvent) throws IOException {

        System.out.println("clicked edit");
        User user = userTable.getSelectionModel().getSelectedItem();
        System.out.println("selected user: " + user);
        if (user == null) {
            System.out.println("no user selected");
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("gui/adminViews/AddEditUser.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        AddEditUserController controller = fxmlLoader.getController();
        controller.init(services.getUserManager());
        controller.setUser(user);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.showAndWait();
        loadUsers();


    }
}
