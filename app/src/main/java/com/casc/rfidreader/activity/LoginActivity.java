package com.casc.rfidreader.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.EditText;

import com.casc.rfidreader.MyParams;
import com.casc.rfidreader.R;
import com.casc.rfidreader.helper.ConfigHelper;
import com.casc.rfidreader.helper.NetHelper;
import com.casc.rfidreader.helper.param.Reply;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.act_username) EditText mUsernameAct;
    @BindView(R.id.et_password) EditText mPasswordEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_sign_in)
    void onSignInButtonClicked() {
        final String username = mUsernameAct.getText().toString();
        final String password = mPasswordEt.getText().toString();

        if (TextUtils.isEmpty(username)) {
            showToast("用户名不能为空");
        } else if (TextUtils.isEmpty(password)) {
            showToast("密码不能为空");
        } else {
            NetHelper.getInstance().userLogin(username, password).enqueue(new Callback<Reply>() {
                @Override
                public void onResponse(@NonNull Call<Reply> call, @NonNull Response<Reply> response) {
                    if (!response.isSuccessful()) {
                        showToast("网络连接失败");
                    } else if (response.body() == null || response.body().getCode() != 200) {
                        showToast("用户名或密码错误");
                    } else {
                        ConfigHelper.setParam(MyParams.S_USERNAME, username);
                        MainActivity.actionStart(LoginActivity.this);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Reply> call, @NonNull Throwable t) {
                    showToast("网络连接失败");
                }
            });
        }
    }
}

