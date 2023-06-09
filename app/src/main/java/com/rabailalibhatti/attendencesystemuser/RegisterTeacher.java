package com.rabailalibhatti.attendencesystemuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterTeacher extends AppCompatActivity {
    TextInputEditText name, contact, username, password;
    Button add, update;
    MaterialToolbar materialToolbar;
    ProgressDialog progressDialog;
    private String teacherKey;
    private teacherModel model;
    TextView appbartitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_teacher);

        materialToolbar = findViewById(R.id.toolBar);
        materialToolbar.setNavigationOnClickListener(view -> {
            finish();
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Adding Teacher..");
        progressDialog.setCancelable(false);

        name = findViewById(R.id.teachername);
        username = findViewById(R.id.teacherusername);
        password = findViewById(R.id.teacherpassword);
        contact = findViewById(R.id.contactnumber);
        add = findViewById(R.id.add);
        update = findViewById(R.id.update);
        appbartitle = findViewById(R.id.appbartitle);

        if (getIntent().hasExtra("KEY")){
            teacherKey = getIntent().getStringExtra("KEY");
            update.setVisibility(View.VISIBLE);
            fillData(teacherKey);
            appbartitle.setText("Update Teacher");
        }
        else{
            add.setVisibility(View.VISIBLE);
            appbartitle.setText("Teacher Registration");
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add.setEnabled(false);
                String namee = name.getText().toString();
                String conta = contact.getText().toString();
                String usern = username.getText().toString();
                String passw = password.getText().toString();
                if (TextUtils.isEmpty(namee)){
                    name.setError("Please Enter Name");
                    progressDialog.dismiss();
                    add.setEnabled(true);
                } else if (TextUtils.isEmpty(conta)){
                    contact.setError("Please Enter Contact Number");
                    progressDialog.dismiss();
                    add.setEnabled(true);
                } else if (TextUtils.isEmpty(usern)){
                    username.setError("Please Enter Username");
                    progressDialog.dismiss();
                    add.setEnabled(true);
                } else if (TextUtils.isEmpty(passw)){
                    password.setError("Please Enter Password");
                    progressDialog.dismiss();
                    add.setEnabled(true);
                } else{
                    addTeacher(namee, conta, usern, passw);
                }
            }
        });
    }
    private void addTeacher(String name, String contact, String username, String password){
        String key = FirebaseDatabase.getInstance().getReference("Teachers").push().getKey();
        FirebaseDatabase.getInstance().getReference("Teachers").child(key).setValue(new teacherModel(key, name, contact, username, password))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(RegisterTeacher.this, "Teacher Successfully Added", Toast.LENGTH_SHORT).show();
                            finish();
                            progressDialog.dismiss();
                            add.setEnabled(true);
                        }
                        else{
                            Toast.makeText(RegisterTeacher.this, ""+task.getException().toString(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            add.setEnabled(true);
                        }
                    }
                });

    }
    private void fillData(String key){
        FirebaseDatabase.getInstance().getReference("Teachers").child(key).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                model = task.getResult().getValue(teacherModel.class);
                name.setText(model.getName());
                contact.setText(model.getContact());
                username.setText(model.getUsername());
                password.setText(model.getPassword());
            }
        });
    }

    public void update(View view) {
        FirebaseDatabase.getInstance().getReference("Teachers")
                .child(model.getKey())
                .setValue(new teacherModel(model.getKey(), name.getText().toString(), contact.getText().toString(), username.getText().toString(), password.getText().toString()))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(RegisterTeacher.this, "Teacher Successfully Updated", Toast.LENGTH_SHORT).show();
                            finish();
                            progressDialog.dismiss();
                        }
                        else{
                            Toast.makeText(RegisterTeacher.this, ""+task.getException().toString(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }
}
