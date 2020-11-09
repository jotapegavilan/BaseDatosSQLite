package com.gavilan.basedatosqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import BaseDatos.ConexionSQLiteHelper;
import BaseDatos.Usuario;
import BaseDatos.Utilidades;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView txtID, txtNombre,txtTelefono;
    Button btnAgregar, btnModificar, btnEliminar;
    ListView listView;
    ArrayList<Usuario> usuarios;
    Usuario usuario;
    ArrayAdapter<Usuario> adapter;
    ConexionSQLiteHelper conn = new ConexionSQLiteHelper(this,"bdUsuarios",null,1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtID = findViewById(R.id.txtID);
        txtNombre = findViewById(R.id.txtNombre);
        txtTelefono = findViewById(R.id.txtTelefono);
        btnAgregar = findViewById(R.id.btnAgregar);
        btnModificar = findViewById(R.id.btnModificar);
        btnEliminar = findViewById(R.id.btnEliminar);
        listView = findViewById(R.id.listView);
        btnEliminar.setOnClickListener(this);
        btnModificar.setOnClickListener(this);
        btnAgregar.setOnClickListener(this);


        usuarios = new ArrayList<>();
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,usuarios);
        selectAll();




    }

    @Override
    protected void onResume() {
        super.onResume();
        txtID.requestFocus();
    }

    public void limpiarCampos(){
        txtID.setText("");
        txtNombre.setText("");
        txtTelefono.setText("");
    }


    public void eliminarTodosUsuario(){
        SQLiteDatabase database = conn.getWritableDatabase();
        database.delete(Utilidades.TABLA_USUARIO,null,null);
        Toast.makeText(this,"Todos los usuarios eliminados",Toast.LENGTH_LONG).show();
        database.close();

    }

    public void eliminarUsuario(int id){
        if(selectOne(id) != null){
            SQLiteDatabase database = conn.getWritableDatabase();
            String[] parametros = {String.valueOf(id)};
            database.delete(Utilidades.TABLA_USUARIO,Utilidades.CAMPO_ID+"=?",parametros);
            Toast.makeText(this,"Usuario eliminado",Toast.LENGTH_LONG).show();
            database.close();
            selectAll();
            limpiarCampos();
        }else{
            Toast.makeText(this,"Usuario no existe",Toast.LENGTH_LONG).show();
        }

    }

    public void actualizarUsuario(int id,String nombre, String telefono){
        if(selectOne(id) != null){
            SQLiteDatabase database = conn.getWritableDatabase();
            String[] parametros = {String.valueOf(id)};
            ContentValues values = new ContentValues();
            values.put(Utilidades.CAMPO_NOMBRE,nombre);
            values.put(Utilidades.CAMPO_TELEFONO,telefono);
            database.update(Utilidades.TABLA_USUARIO,values,Utilidades.CAMPO_ID+"=?",parametros);
            Toast.makeText(this,"Usuario actualizado",Toast.LENGTH_LONG).show();
            database.close();
            selectAll();
            limpiarCampos();
        }else{
            Toast.makeText(this,"Usuario no existe",Toast.LENGTH_LONG).show();

        }

    }

    public Usuario selectOne(int id){
        SQLiteDatabase database = conn.getReadableDatabase();
        String[] parametros = {String.valueOf(id)};
        String[] campos = {Utilidades.CAMPO_ID, Utilidades.CAMPO_NOMBRE, Utilidades.CAMPO_TELEFONO};
        Cursor cursor = database.query(Utilidades.TABLA_USUARIO,campos,Utilidades.CAMPO_ID+"=?",parametros,null,null,null);
        Usuario usuario = null;
        while (cursor.moveToNext()){
            usuario = new Usuario(cursor.getInt(0),cursor.getString(1),cursor.getString(2));
        }
        return usuario;
    }


    public void selectAll(){
        adapter.clear();
        SQLiteDatabase database = conn.getReadableDatabase();
        String[] parametros = {"1"};
        String[] campos = {Utilidades.CAMPO_ID, Utilidades.CAMPO_NOMBRE, Utilidades.CAMPO_TELEFONO};
        //Cursor cursor = database.query(Utilidades.TABLA_USUARIO,campos,Utilidades.CAMPO_ID+"=?",parametros,null,null,null);
        Cursor cursor = database.rawQuery("SELECT * FROM "+Utilidades.TABLA_USUARIO,null);
        cursor.moveToFirst();
        while (cursor.moveToNext()){
            usuario = new Usuario(cursor.getInt(0),cursor.getString(1),cursor.getString(2));
            usuarios.add(usuario);

        }
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,usuarios);
        listView.setAdapter(adapter);
        //Toast.makeText(this,"ArrayList cargado con "+usuarios.size()+" usuarios.",Toast.LENGTH_LONG).show();
        cursor.close();
        database.close();
    }

    public void insertarUsuario(int id, String nombre, String telefono){
        SQLiteDatabase database = conn.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Utilidades.CAMPO_ID, id);
        values.put(Utilidades.CAMPO_NOMBRE,nombre);
        values.put(Utilidades.CAMPO_TELEFONO,telefono);

        long res = database.insert(Utilidades.TABLA_USUARIO,Utilidades.CAMPO_ID,values);

        Toast.makeText(this,"Respuesta :"+res,Toast.LENGTH_LONG).show();

        database.close();
        selectAll();
        limpiarCampos();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnAgregar:
                insertarUsuario(Integer.parseInt(txtID.getText().toString()),txtNombre.getText().toString(),txtTelefono.getText().toString());
                break;
            case R.id.btnModificar:
                actualizarUsuario(Integer.parseInt(txtID.getText().toString()),txtNombre.getText().toString(),txtTelefono.getText().toString());
                break;
            case R.id.btnEliminar:
                eliminarUsuario(Integer.parseInt(txtID.getText().toString()));
                break;
        }
    }
}