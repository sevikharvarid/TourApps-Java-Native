package com.example.exploreasy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, usernameEditText, fullNameEditText,
            birthDateEditText, genderEditText, phoneNumberEditText, addressEditText;
    private Button registerButton, alreadyHaveAcc;

    private DatabaseReference usersRef;
    private FirebaseAuth mAuth;
    private Calendar calendar;

    private String email, password, username, fullName, birthDate, gender, phoneNumber, address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inisialisasi referensi database
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Inisialisasi Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Inisialisasi UI
        emailEditText = findViewById(R.id.et_email);
        passwordEditText = findViewById(R.id.et_password);
        usernameEditText = findViewById(R.id.et_username);
        fullNameEditText = findViewById(R.id.et_fullname);
        birthDateEditText = findViewById(R.id.et_birthdate);
        birthDateEditText.setFocusable(false);
        birthDateEditText.setCursorVisible(false);
        calendar = Calendar.getInstance();
        phoneNumberEditText = findViewById(R.id.et_phone_number);
        addressEditText = findViewById(R.id.et_address);
        registerButton = findViewById(R.id.btn_register);
        alreadyHaveAcc = findViewById(R.id.btn_already_have_account);
        alreadyHaveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        birthDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        calendar.set(year, monthOfYear, dayOfMonth);
                        String selectedDate = sdf.format(calendar.getTime());
                        birthDateEditText.setText(selectedDate);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
        RadioGroup radioGroupGender = findViewById(R.id.radioGroupGender);
        radioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButtonMale:
                        gender = "Male";
                        break;
                    case R.id.radioButtonFemale:
                        gender = "Female";
                        break;
                }
            }
        });

    }

    private void registerUser() {
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();
        username = usernameEditText.getText().toString();
        fullName = fullNameEditText.getText().toString();
        birthDate = birthDateEditText.getText().toString();
        phoneNumber = phoneNumberEditText.getText().toString();
        address = addressEditText.getText().toString();

        User user = new User(username, email, password, fullName, birthDate, gender, phoneNumber, address);
        if (email.isEmpty() && password.isEmpty() && username.isEmpty()
                && fullName.isEmpty() && birthDate.isEmpty()
                && phoneNumber.isEmpty() && address.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Harap lengkapi data diatas !", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser userData = mAuth.getCurrentUser();
                                String userId = userData.getUid();
                                Toast.makeText(RegisterActivity.this, userId, Toast.LENGTH_SHORT).show();
                                //Create other field
                                usersRef.child(userId).setValue(user);
                                showRegistrationSuccessDialog("Registration Successfully !");
                            } else {
                                showRegistrationFailedDialog("Registration Unsuccessfully !");
                            }
                        }
                    });
        }
    }

    private void showRegistrationSuccessDialog(String title) {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(title)
                .setConfirmText("OK")
                .setConfirmButtonBackgroundColor(Color.BLACK)
                .setConfirmButtonTextColor(Color.WHITE)
                .setNeutralButtonBackgroundColor(Color.BLACK)
                .setNeutralButtonTextColor(Color.WHITE)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);

                    }
                });
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
    }

    private void showRegistrationFailedDialog(String title) {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(title)
                .setConfirmText("OK")
                .setConfirmButtonBackgroundColor(Color.BLACK)
                .setConfirmButtonTextColor(Color.WHITE)
                .setNeutralButtonBackgroundColor(Color.BLACK)
                .setNeutralButtonTextColor(Color.WHITE)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                });
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
    }
}


