package com.null_pointer.diarycloud;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.null_pointer.diarycloud.R;
import com.null_pointer.diarycloud.model.DiaryCloud;

import java.util.HashMap;
import java.util.Map;

public class LogIn extends AppCompatActivity {

    private EditText mETLogin;
    private EditText mETPassword;
    private Button mBOk;
    private CheckBox mKeepSignedIn;
    private ProgressBar mLoginProgress;
    private Thread loginProgressThread;

    private boolean isProgressStopped = false;

    private static Handler handler =  new Handler() {
        @Override
        public void handleMessage(Message msg) {
            LoginProgressMessage message = (LoginProgressMessage) msg.obj;
            message.runOperation();
        }
    };

    private class LoginProgressMessage{
        public void runOperation(){
            // Override this
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.log_in);
        getSupportActionBar().hide();
        mETLogin = (EditText) findViewById(R.id.etLogin);//
        mETPassword = (EditText) findViewById(R.id.etPassword);
        mBOk = (Button) findViewById(R.id.btnOk);
        mLoginProgress = (ProgressBar) findViewById(R.id.login_progress);
        mLoginProgress.setMax(160);
        mKeepSignedIn = (CheckBox) findViewById(R.id.keep_signed_in);

        if(DiaryCloud.getIdentity(getApplicationContext()) != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
            // если есть токен (юзер ранее вошел)
        }
        HashMap<String, String> data = DiaryCloud.getData(getApplicationContext());
        if(data != null){
            mETLogin.setText(data.get("username"));
            mETPassword.setText(data.get("password"));
            //если есть сохраненные данные(галочка "сохранить пароль")
        }
        mBOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mETLogin.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Введите логин!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mETPassword.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Введите пароль!", Toast.LENGTH_SHORT).show();
                    return;
                }

                createLoginProgressThread();
                loginProgressThread.start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DiaryCloud client = new DiaryCloud();

                        try{
                            String username = mETLogin.getText().toString();
                            String password = mETPassword.getText().toString();

                            if( client.signIn( username, password )){
                                setDefault();
                                client.saveIdentity(getApplicationContext());
                                if(mKeepSignedIn.isChecked()){
                                    DiaryCloud.saveData( username, password, getApplicationContext());
                                }
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                setDefault();
                                sendMessage(new LoginProgressMessage() {
                                    @Override
                                    public void runOperation() {
                                        Toast.makeText(getApplicationContext(), R.string.incorrect_auth_data, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }catch (Exception e){
                            setDefault();
                            Log.d("AppException", e.getMessage());
                            sendMessage(new LoginProgressMessage() {
                                @Override
                                public void runOperation() {
                                    Toast.makeText(getApplicationContext(), R.string.auth_error_try_later, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }

    protected void setDefault() {
        isProgressStopped = true;
        try {
            loginProgressThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        sendMessage(new LoginProgressMessage(){
            @Override
            public void runOperation() {
                mBOk.setEnabled(true);
                mLoginProgress.setProgress(0);
            }
        });
    }

    protected void createLoginProgressThread(){
        if(loginProgressThread != null && loginProgressThread.isAlive()){
            loginProgressThread.interrupt();
        }
        isProgressStopped = false;
        loginProgressThread = new Thread(new Runnable() {
            @Override
            public void run() {
                sendMessage(new LoginProgressMessage(){
                    @Override
                    public void runOperation(){
                        mBOk.setEnabled(false);
                       // mLoginProgress.setVisibility(View.VISIBLE);
                    }
                });
                for (int i = 0; i <= 160; i++) {
                    final int prg = i;
                    sendMessage(new LoginProgressMessage(){
                        @Override
                        public void runOperation(){
                            mLoginProgress.setProgress(prg);
                        }
                    });
                    if (isProgressStopped) {
                        return;
                    }
                    try {
                        Thread.sleep(35);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    protected void sendMessage(LoginProgressMessage operation){
        Message msg = new Message();
        msg.obj = operation;
        handler.sendMessage(msg);
    }

}
