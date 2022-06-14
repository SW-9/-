package osp.smgonggu.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;

public class JoinActivity extends AppCompatActivity {

    // 로그 찍을 때 사용하는 TAG 변수
    final private String TAG = getClass().getSimpleName();

    // 사용할 컴포넌트 선언
    EditText userid_et, passwd_et,test_et;
    EditText nick_et;
    Button join_button;

    //firebase에 연결할 함수
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

// 컴포넌트 초기화
        userid_et = findViewById(R.id.userid_et);
        passwd_et = findViewById(R.id.passwd_et);
        nick_et = findViewById(R.id.nick_et);
        join_button = findViewById(R.id.join_button);

        //firebase의 인스턴스 초기화
        mAuth = FirebaseAuth.getInstance();

// 버튼 이벤트 추가
        join_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
// 회원가입 함수 호출
                String getUserId = userid_et.getText().toString();
                String getnick= nick_et.getText().toString();
                String getUserPassword = passwd_et.getText().toString();

                if(getUserId.equals("")) {
                    Toast.makeText(JoinActivity.this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                    else if(getnick.equals("")) {
                    Toast.makeText(JoinActivity.this, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(getUserPassword.equals("")) {
                    Toast.makeText(JoinActivity.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(passwordcheck()==false){
                    Toast.makeText(JoinActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();

                }
                else{
                    mAuth = FirebaseAuth.getInstance();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(getnick)
                            .build();

                    mAuth.getCurrentUser().updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "User profile updated.");
                                    }
                                }
                            });


                    createAccount(getUserId, getUserPassword);

                }

            }
            });

        }


    private boolean passwordcheck(){
        if(passwd_et.getText().toString().equals(test_et.getText().toString())){
            return true;
        }
        else return false;
    }

    private void createAccount(String email, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String email = user.getEmail();
                            String uid = nick_et.getText().toString();



                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(uid)
                                    .build();
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                            }
                                        }
                                    });

                            //가입이 이루어져을시 가입 화면을 빠져나감.
                            Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(JoinActivity.this, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(JoinActivity.this, "회원가입에 실패하셨습니다.", Toast.LENGTH_SHORT).show();


                        }
                    }
                });

    }
    }


