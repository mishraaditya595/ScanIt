package com.vob.scratch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.vob.scanner.R;

public class SignUpActivity extends AppCompatActivity {

    private String email;
    private String password;
    private String confirmPassword;
    private EditText emailET, passwordET, passwordConfirmET;
    private CardView registerCardView;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initialiseFields();

        ActionCodeSettings actionCodeSettings =
                ActionCodeSettings.newBuilder()
                        .setUrl("https://www.vob.page.link/finishSignUp?cartId=1234")
                        .setHandleCodeInApp(true)
                        .setIOSBundleId("")
                        .setAndroidPackageName(
                                "com.vob.scanner",
                                true,
                                "21")
                        .build();


        sendSignUpLink(actionCodeSettings);
    }

    private void sendSignUpLink(final ActionCodeSettings actionCodeSettings) {
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        registerCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.sendSignInLinkToEmail(email, actionCodeSettings)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignUpActivity.this,"Verification link has been sent to your email.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }

    private void updateUI(Object o) {
    }

    private void initialiseFields() {
        emailET = findViewById(R.id.new_email_et);
        email = emailET.getText().toString();
        //passwordET = findViewById(R.id.new_password_et);
        //password = passwordET.getText().toString();
        //passwordConfirmET = findViewById(R.id.password_confirm_et);
        //confirmPassword = passwordConfirmET.getText().toString();
        registerCardView = findViewById(R.id.registerCardView);
        mAuth = FirebaseAuth.getInstance();
    }
}