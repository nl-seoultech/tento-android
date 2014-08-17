package tento.kr.tento.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import tento.kr.tento.R;


public class LoginActivity extends Activity implements View.OnClickListener {

    Button btnSignIn;
    Button btnJoin;
    EditText textUserEmail;
    EditText txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textUserEmail = (EditText) this.findViewById(R.id.UserEmail);
        txtPassword = (EditText) this.findViewById(R.id.UserPassword);
        btnJoin = (Button) this.findViewById(R.id.btnJoin);
        btnSignIn = (Button) this.findViewById(R.id.btnSignIn);

        btnSignIn.setOnClickListener(this);
        btnJoin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnJoin:
                //Join 버튼 눌렀을 때
                Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
                startActivity(intent);
                break;

            case R.id.btnSignIn:
                //Sign In 버튼 눌렀을 때
                break;
        }
    }
}
