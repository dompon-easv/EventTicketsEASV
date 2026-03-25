package dk.easv.eventticketapp.gui.adminControllers;

import dk.easv.eventticketapp.Application;
import dk.easv.eventticketapp.be.User;
import dk.easv.eventticketapp.be.UserRole;
import dk.easv.eventticketapp.bll.UserManager;
import dk.easv.eventticketapp.gui.coordinatorControllers.CoordinatorMainController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class UserManagementController {

   @FXML private TableView<User> userTable;
   @FXML private TableColumn<User, String> nameColumn;
    @FXML private TableColumn<User, String> surnameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, UserRole> roleColumn;
  //  @FXML private TableColumn<User, String> passwordColumn;

    private UserManager userManager;
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
        loadUsers();
    }
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));


    }

    public void loadUsers()
    {
        ObservableList<User> userList = FXCollections.observableArrayList(userManager.getAllUsers());
        userTable.setItems(userList);
    }



    public void onAddUser(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("gui/adminViews/AddEditUser.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        AddEditUserController controller = fxmlLoader.getController();
        controller.init(userManager);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.showAndWait();
        loadUsers();
    }


    public void handleDeleteUser(ActionEvent actionEvent) {
        User user = userTable.getSelectionModel().getSelectedItem();
        if (user != null) {
            userManager.deleteUser(user.getUsername());
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
        controller.init(userManager);
        controller.setUser(user);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.showAndWait();
        loadUsers();


    }
}
