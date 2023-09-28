package com.example.cruduniminuto;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Optional;

public class UserInterface {

    private ManagementDB managementDB;
    Context context;
    View view;
    UserEntity user;

    public UserInterface(Context context, View view) {
        this.context = context;
        this.view = view;
        managementDB = new ManagementDB(context);
    }

    //Insert into Table USERS
    public boolean insertUser(UserEntity user){
        SQLiteDatabase db = managementDB.getWritableDatabase();
        try{
            if(db != null){
                ContentValues values = new ContentValues();
                values.put("USU_DOCUMENT",user.getDocument());
                values.put("USU_USER",user.getUser());
                values.put("USU_NAME",user.getName());
                values.put("USU_LASTNAME",user.getLastName());
                values.put("USU_PASSWORD",user.getPassword());
                long response = db.insert("users",null,values);
                Snackbar.make(this.view,"registered user"+response,Snackbar.LENGTH_LONG).show();
                db.close();
                return true;
            }else{
                Snackbar.make(this.view,"user not registered",Snackbar.LENGTH_LONG).show();
                return false;
            }
        }catch (SQLException sqlException){
            Log.i("DB","error:"+sqlException);
            return false;
        }
    }
    public boolean setNewUser(UserEntity newUser){
        SQLiteDatabase db = managementDB.getWritableDatabase();
        try {
            if (db != null) {
                ContentValues values = new ContentValues();
                values.put("USU_USER", newUser.getUser());
                values.put("USU_NAME", newUser.getName());
                values.put("USU_LASTNAME", newUser.getLastName());
                values.put("USU_PASSWORD", newUser.getPassword());

                // Define la cláusula WHERE para especificar qué registro actualizar
                String whereClause = "USU_DOCUMENT = ?";

                // Define los valores para reemplazar los marcadores de posición en la cláusula WHERE
                String[] whereArgs = {String.valueOf(newUser.getDocument())};

                long response = db.update("users", values, whereClause, whereArgs);

                if (response > 0) {
                    Snackbar.make(this.view, "User updated: " + response, Snackbar.LENGTH_LONG).show();
                    db.close();
                    return true;
                } else {
                    Snackbar.make(this.view, "User not found or no changes made", Snackbar.LENGTH_LONG).show();
                    db.close();
                    return false;
                }
            } else {
                Snackbar.make(this.view, "Database not available", Snackbar.LENGTH_LONG).show();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }
    public Optional<UserEntity> getUser(String document){

        SQLiteDatabase db = managementDB.getReadableDatabase();
        String searchQuery = "SELECT * FROM USERS WHERE USU_DOCUMENT = " + "'"+ document + "'";
        Cursor cursor = db.rawQuery(searchQuery,null);
        cursor.moveToFirst();
        UserEntity userEntity = new UserEntity(cursor.getInt(0),cursor.getString(1), cursor.getString(2)
                ,cursor.getString(3),cursor.getString(4), cursor.getString(5));
        cursor.close();
        db.close();
        return Optional.of(userEntity);
    }
    public ArrayList<UserEntity> getListUsers(){
        SQLiteDatabase db = managementDB.getWritableDatabase();
        String selectQuery = "SELECT * FROM USERS";
        ArrayList<UserEntity> usersList = new ArrayList<>();
        Cursor cursor = db.rawQuery(selectQuery,null);
        if (cursor.moveToFirst()){
            do {
                user = new UserEntity(cursor.getInt(0),cursor.getString(1), cursor.getString(2)
                        ,cursor.getString(3),cursor.getString(4), cursor.getString(5));
                usersList.add(user);
            }  while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return usersList;
    }

}
