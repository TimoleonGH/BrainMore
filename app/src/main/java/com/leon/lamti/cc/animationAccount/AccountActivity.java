package com.leon.lamti.cc.animationAccount;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.leon.lamti.cc.R;

public class AccountActivity extends AppCompatActivity {

    // Views
    private Button login, signin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_account);

        login = (Button) findViewById(R.id.loginCB);
        signin = (Button) findViewById(R.id.signInCB);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                goTo(LoginActivity.class, login, "loginTraName");
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                goTo(CreateAccountActivity.class, signin, "signInTraName");
            }
        });
    }

    private void goTo (Class goClass, View view, String s) {

        Intent intent = new Intent( AccountActivity.this, goClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        ActivityOptions activityOptions = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            activityOptions = ActivityOptions.makeSceneTransitionAnimation(
                    this,
                    view,
                    s);

            startActivity(intent, activityOptions.toBundle());
        } else {

            startActivity(intent);
        }
    }
}
