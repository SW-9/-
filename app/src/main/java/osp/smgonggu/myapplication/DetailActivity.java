package osp.smgonggu.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class DetailActivity extends AppCompatActivity {

    // 로그에 사용할 TAG
    final private String TAG = getClass().getSimpleName();

    // 사용할 컴포넌트 선언
    TextView title_tv, content_tv, user_tv, getidx;
    LinearLayout comment_layout;
    EditText comment_et;
    Button reg_button;
    View customView;


    //댓글 리스트
    Comment[] comment;

    // 선택한 게시물의 번호
    String board_seq = "";

    // 유저아이디 변수
    String userid= "";

    //글 제목
    String title = "";


    FirebaseAuth mAuth;

    private FirebaseFirestore db;
FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

// ListActivity 에서 넘긴 변수들을 받아줌
        board_seq = getIntent().getStringExtra("board_seq");
        title = getIntent().getStringExtra("title");

// 컴포넌트 초기화
        title_tv = findViewById(R.id.title_tv);
        title_tv.setText(title);
        content_tv = findViewById(R.id.content_tv);
        user_tv = findViewById(R.id.user_tv);

        comment_layout = findViewById(R.id.comment_layout);
        comment_et = findViewById(R.id.comment_et);
        reg_button = findViewById(R.id.reg_button);

        getidx = findViewById(R.id.getidx);
        getidx.setVisibility(View.GONE);

        db=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userid= user.getDisplayName();

        // custom_comment 를 불러오기 위한 객체
        LayoutInflater layoutInflater = LayoutInflater.from(DetailActivity.this);
        customView = layoutInflater.inflate(R.layout.custom_comment, null);


// 등록하기 버튼을 눌렀을 때 댓글 등록 함수 호출
        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getIndex();
                RegCmt(userid, board_seq);
            }
        });


    }

    // onResume() 은 해당 액티비티가 화면에 나타날 때 호출됨
    @Override
    protected void onResume() {
        super.onResume();
// 해당 액티비티가 활성화 될 때, 게시물 리스트를 불러오는 함수를 호출
        loadBoard();
       // LoadCmt();
       // getIndex();
    }

    //게시물 불러오는 함수
    public void loadBoard() {
        db.collection("Board")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String title1 = document.get("title").toString();
                                if (title1.equals(title_tv.getText().toString())) {
                                    String uid = document.get("uid").toString();
                                    user_tv.setText(uid);
                                    String content = document.get("content").toString();
                                    content_tv.setText(content);

                                }

                            }
                        }
                    }
                });

    }


    // 게시물의 댓글을 읽어오는 함수
    void LoadCmt() {

        db.collection("comment")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String seq = document.get("Boardindex").toString();
                                if (seq == board_seq) {


                                    String user = document.get("user").toString();
                                    String content = document.get("content").toString();

                                    ((TextView) customView.findViewById(R.id.cmt_userid_tv)).setText(user);
                                    ((TextView) customView.findViewById(R.id.cmt_content_tv)).setText(content);

// 댓글 레이아웃에 custom_comment 의 디자인에 데이터를 담아서 추가
                                    comment_layout.addView(customView);
                                }

                            }
                        }
                    }
                });

    }

    // 댓글을 등록하는 함수
    void RegCmt(String uid, String index) {
        String com = comment_et.getText().toString();
        Comment comment = new Comment(uid, com, index, getidx.getText().toString());

        db.collection("comments").add(comment);
        Intent intent = new Intent(DetailActivity.this, DetailActivity.class);
        Toast.makeText(DetailActivity.this, "댓글이 등록되었습니다.", Toast.LENGTH_SHORT).show();
        startActivity(intent);
        finish();

    }


    void getIndex() {

        db.collection("comments")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int i = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                getidx.setText(document.get("index").toString());
                            }
                        }
                    }
                });
    }
}
