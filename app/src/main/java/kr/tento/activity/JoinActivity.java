package kr.tento.activity;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import kr.tento.R;

public class JoinActivity extends Activity implements View.OnClickListener {

    Button btnCancle;
    Button btnOkay;
    EditText textJoinUserId;
    EditText textJoinUserPassword;
    EditText textJoinUserConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        btnOkay = (Button) this.findViewById(R.id.btnOkay);
        btnCancle = (Button) this.findViewById(R.id.btnCancle);
        textJoinUserId = (EditText) this.findViewById(R.id.JoinEmail);
        textJoinUserPassword = (EditText) this.findViewById(R.id.JoinPassword);
        textJoinUserConfirmPassword = (EditText) this.findViewById(R.id.JoinConfirmPassword);

        btnOkay.setOnClickListener(this);
        btnCancle.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnCancle:
                //Cancle 버튼 눌렀을 때
                finish();
                break;

            case R.id.btnOkay:
                //Okay 버튼 눌렀을 때
                break;
        }
    }
}
