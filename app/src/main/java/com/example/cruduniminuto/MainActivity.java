package com.example.cruduniminuto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.service.autofill.AutofillService;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class MainActivity extends AppCompatActivity {

    private EditText etDocument;
    private EditText etName;
    private EditText etLastName;
    private EditText etUser;
    private EditText etPassword;
    private ListView userList;
    private Button btUpdate;
    int idUser;
    String document;
    String name;
    String user;
    String lastName;
    String password;
    View view;
    private ManagementDB managementDB;

    SQLiteDatabase sqLiteDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializer();
    }

    private void initializer(){
        etDocument = findViewById(R.id.etID);
        etUser = findViewById(R.id.etUser);
        etName = findViewById(R.id.etName);
        etLastName = findViewById(R.id.etLastName);
        etPassword = findViewById(R.id.etPassword);
        userList = findViewById(R.id.lvUsers);
        btUpdate = findViewById(R.id.btUpdate);
        btUpdate.setEnabled(false); // Deshabilitar el botón
        // Ocultar la lista
        userList.setVisibility(View.INVISIBLE);
    }

    public void userList(){
        UserInterface userInterface = new UserInterface(this,findViewById(R.id.lvUsers));
        ArrayList<UserEntity> toList = userInterface.getListUsers();
        ArrayAdapter<UserEntity> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,
                toList);
        userList.setAdapter(adapter);
    }
    public void btnList(View v){
        userList.setVisibility(View.VISIBLE);
        this.userList();
    }
    public boolean setData(){
        //validaciones con regex
        Context context = this;
        this.document = etDocument.getText().toString().trim();
        this.name = etName.getText().toString().trim();
        this.lastName = etLastName.getText().toString().trim();
        this.user = etUser.getText().toString().trim();
        this.password = etPassword.getText().toString().trim();

        if (document.isEmpty() || name.isEmpty() || lastName.isEmpty() || user.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Fill all data", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            // Patrones de expresiones regulares
            Pattern documentPattern = Pattern.compile("^[0-9]{10}$"); // Por ejemplo, documento de 10 dígitos
            Pattern namePattern = Pattern.compile("^[A-Za-z]+$"); // Solo letras
            Pattern userPattern = Pattern.compile("^[A-Za-z0-9]+$"); // Letras y números
            Pattern passwordPattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{6,}$"); // Al menos 1 letra y 1 número, mínimo 6 caracteres

            // Realizar las validaciones
            boolean isDocumentValid = documentPattern.matcher(document).matches();
            boolean isNameValid = namePattern.matcher(name).matches();
            boolean isLastNameValid = namePattern.matcher(lastName).matches();
            boolean isUserValid = userPattern.matcher(user).matches();
            boolean isPasswordValid = passwordPattern.matcher(password).matches();

            // Verificar los resultados de las validaciones
            if (!isDocumentValid) {
                Toast.makeText(context, "invalid document", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (!isNameValid) {
                Toast.makeText(context, "invalid name", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (!isLastNameValid) {
                Toast.makeText(context, "invalid LastName", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (!isUserValid) {
                Toast.makeText(context, "invalid User", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (!isPasswordValid) {
                Toast.makeText(context, "invalid Password", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }
    public void userRegister(View view){
        if(setData()){
            UserInterface userInterface = new UserInterface(this,view);
            UserEntity userEntity = new UserEntity(this.idUser,this.document,this.user,this.name,this.lastName,this.password);
            boolean insertionSuccessful = userInterface.insertUser(userEntity);

            if (insertionSuccessful) {
                // Limpiar los campos de EditText
                etDocument.getText().clear();
                etName.getText().clear();
                etLastName.getText().clear();
                etUser.getText().clear();
                etPassword.getText().clear();
                userList.setVisibility(View.VISIBLE);
            }
            this.userList();
        }
    }

    public String getDocument(){
        Context context = this;
        this.document = etDocument.getText().toString().trim();
        if (document.isEmpty()){
            Toast.makeText(context, "Fill all data", Toast.LENGTH_SHORT).show();
            return "";
        }else{
            // Patrones de expresion regular document
            Pattern documentPattern = Pattern.compile("^[0-9]{10}$"); // Por ejemplo, documento de 10
            // Realizar la validacione
            boolean isDocumentValid = documentPattern.matcher(document).matches();
            if (!isDocumentValid) {
                Toast.makeText(context, "invalid document", Toast.LENGTH_SHORT).show();
                return "";
            }
        }
        return this.document = etDocument.getText().toString().trim();
    }
    public void searchUser(View view){
        String validDocument = getDocument();
        if (!validDocument.isEmpty()) {
            UserInterface userInterface = new UserInterface(this, view);
            Optional<UserEntity> userEntityOptional = userInterface.getUser(validDocument);

            if (userEntityOptional.isPresent()) {
                UserEntity userEntity = userEntityOptional.get();
                // Establecer los valores en los campos de EditText
                etDocument.setText(userEntity.getDocument());
                etName.setText(userEntity.getName());
                etLastName.setText(userEntity.getLastName());
                etUser.setText(userEntity.getUser());
                etPassword.setText(userEntity.getPassword());
                btUpdate.setEnabled(true);
                etDocument.setEnabled(false);
                userList.setVisibility(View.VISIBLE);
            } else {
                // Manejar el caso en que no se encuentre el usuario
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            }
            this.userList();
        }
    }

    public void updateUser(View view){
        if(setData()){
            UserInterface userInterface = new UserInterface(this,view);
            UserEntity userEntity = new UserEntity(this.idUser,this.document,this.user,this.name,this.lastName,this.password);
            boolean updateSuccessful = userInterface.setNewUser(userEntity);

            if (updateSuccessful) {
                // Limpiar los campos de EditText
                etDocument.getText().clear();
                etDocument.setEnabled(true);
                etName.getText().clear();
                etLastName.getText().clear();
                etUser.getText().clear();
                etPassword.getText().clear();
                btUpdate.setEnabled(false);
                userList.setVisibility(View.VISIBLE);
            }
            this.userList();
        }
    }
}