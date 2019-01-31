package com.casc.rfidreader.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import com.casc.rfidreader.MyParams;
import com.casc.rfidreader.R;
import com.casc.rfidreader.helper.NetHelper;
import com.casc.rfidreader.helper.SpHelper;
import com.casc.rfidreader.helper.param.Reply;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.tb_login) Toolbar mToolbar;
    @BindView(R.id.met_username) MaterialEditText mUsernameMet;
    @BindView(R.id.met_password) MaterialEditText mPasswordMet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setTitle("登录");
        setSupportActionBar(mToolbar);
        mUsernameMet.setText(SpHelper.getString(MyParams.S_USERNAME));
    }

    @OnClick(R.id.btn_sign_in)
    void onSignInButtonClicked() {
        final String username = mUsernameMet.getText().toString();
        final String password = mPasswordMet.getText().toString();

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
                        SpHelper.setParam(MyParams.S_USERNAME, username);
                        MainActivity.actionStart(LoginActivity.this);
                        finish();
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

