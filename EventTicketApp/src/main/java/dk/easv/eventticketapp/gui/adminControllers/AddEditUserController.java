package dk.easv.eventticketapp.gui.adminControllers;

import dk.easv.eventticketapp.be.UserRole;
import dk.easv.eventticketapp.bll.UserManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddEditUserController {
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField surnameField;
    @FXML
    private ComboBox<UserRole> roleComboBox;

    private UserManager userManager;

    public void init (UserManager userManager)
    {this.userManager = userManager;}

    public void initialize() {
        roleComboBox.getItems().addAll(UserRole.values());
    }

    public void onClickClose(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    public void onAddUser(ActionEvent actionEvent) {
        String userName = usernameField.getText();
        String password = passwordField.getText();
        String email = emailField.getText();
        String name = nameField.getText();
        String surname = surnameField.getText();
        UserRole userRole = roleComboBox.getSelectionModel().getSelectedItem();

        if (userName.isEmpty() || password.isEmpty() || email.isEmpty() || name.isEmpty() || surname.isEmpty() || roleComboBox.getSelectionModel().isEmpty())
        {
            System.out.println("Please fill all the fields");
        }else {

            try {
               userManager.addUser(email, userRole, name, surname, password, userName);
               Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
               stage.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }



    }
}
