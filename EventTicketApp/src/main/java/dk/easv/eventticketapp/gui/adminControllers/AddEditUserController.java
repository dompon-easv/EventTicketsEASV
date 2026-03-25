package dk.easv.eventticketapp.gui.adminControllers;

import dk.easv.eventticketapp.be.User;
import dk.easv.eventticketapp.be.UserRole;
import dk.easv.eventticketapp.bll.UserManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class AddEditUserController {
    @FXML Label lblAddEditInfo;
    @FXML Label lblAddEdit;

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
    private User editingUser;

    public void init (UserManager userManager)
    {this.userManager = userManager;}

    public void initialize() { roleComboBox.getItems().addAll(UserRole.values());
    }

    public void onClickClose(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    public void onSave(ActionEvent actionEvent) {
        String userName = usernameField.getText();
        String password = passwordField.getText();
        String email = emailField.getText();
        String name = nameField.getText();
        String surname = surnameField.getText();
        UserRole userRole = roleComboBox.getSelectionModel().getSelectedItem();

        if (userName.isEmpty() || password.isEmpty() || email.isEmpty() || name.isEmpty() || surname.isEmpty() || roleComboBox.getSelectionModel().isEmpty())
        {
            System.out.println("Please fill all the fields");
            return;
        }

        try{
            if(editingUser == null) {
                userManager.addUser(email, userRole, name, surname, password, userName);
            } else {
                User updatedUser = new User(editingUser.getId(), email, userRole, name, surname, password, userName);
                userManager.editUser(updatedUser);
            }
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                stage.close();
            } catch (Exception e){
                e.printStackTrace();
            }

        }



    public void setUser(User user) {
        this.editingUser = user;

        if (user != null) {
            usernameField.setText(user.getUsername());
            passwordField.setText(user.getPassword());
            emailField.setText(user.getEmail());
            nameField.setText(user.getName());
            surnameField.setText(user.getSurname());

            lblAddEditInfo.setText("Update the user " + user.getUsername() +"'s information below");
            lblAddEdit.setText("Edit User Information");
           roleComboBox.setValue(user.getRole());
        }
    }
}
