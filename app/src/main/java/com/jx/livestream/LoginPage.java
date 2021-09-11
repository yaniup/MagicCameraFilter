package com.jx.livestream;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.Snackbar;
import com.jx.livestream.databinding.LoginPageBinding;

public class LoginPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LoginPageBinding loginPageBinding = LoginPageBinding.inflate(getLayoutInflater());
        setContentView(loginPageBinding.getRoot());

        loginPageBinding.sendVerifiedCodeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(loginPageBinding.loginContainer,"验证码已发送，请查收！",Snackbar.LENGTH_SHORT).show();
            }
        });

        loginPageBinding.loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loginPageBinding.phoneEdit.getText().toString().trim().equals(""))
                {
                    Snackbar.make(loginPageBinding.loginContainer,"手机号码不能为空，请重新输入！",Snackbar.LENGTH_SHORT).show();
                    loginPageBinding.phoneEdit.findFocus();
                    return;
                }

                if (loginPageBinding.phoneEdit.getText().toString().trim().length()!=11)
                {
                    Snackbar.make(loginPageBinding.loginContainer,"手机号码不正确，请重新输入！",Snackbar.LENGTH_SHORT).show();
                    loginPageBinding.phoneEdit.findFocus();
                    return;
                }

                if (loginPageBinding.verifiedCodeEdit.getText().toString().trim().equals(""))
                {
                    Snackbar.make(loginPageBinding.loginContainer,"验证码不能为空，请重新输入！",Snackbar.LENGTH_SHORT).show();
                    loginPageBinding.verifiedCodeEdit.findFocus();
                    return;
                }

                if (loginPageBinding.verifiedCodeEdit.getText().toString().trim().length()!=6)
                {
                    Snackbar.make(loginPageBinding.loginContainer,"验证码不正确，请重新输入！",Snackbar.LENGTH_SHORT).show();
                    loginPageBinding.verifiedCodeEdit.findFocus();
                    return;
                }

                SharedPreferences sharedPreferences = getSharedPreferences("user_info",MODE_PRIVATE);
                sharedPreferences.edit().putString("user_phone",loginPageBinding.phoneEdit.getText().toString().trim()).apply();

                Intent jumpLivePage = new Intent(LoginPage.this,LivePage.class);
                startActivity(jumpLivePage);
                finish();
            }
        });
    }
}