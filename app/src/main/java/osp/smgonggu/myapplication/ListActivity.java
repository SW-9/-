package osp.smgonggu.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class ListActivity extends AppCompatActivity{

    // 로그에 사용할 TAG 변수
    final private String TAG = getClass().getSimpleName();

    // 사용할 컴포넌트 선언
    ListView listView;
    Button reg_button;

    FirebaseAuth mAuth;

    private FirebaseFirestore db;
    // 리스트뷰에 사용할 제목 배열
    ArrayList<String> titleList = new ArrayList<>();

    // 클릭했을 때 어떤 게시물을 클릭했는지 게시물 번호를 담기 위한 배열
    ArrayList<String> seqList = new ArrayList<>();

    String name;
    String email;
    String seq;
   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);


        mAuth = FirebaseAuth.getInstance();
        db= FirebaseFirestore.getInstance();
       FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
               .setPersistenceEnabled(true)
               .build();
       db.setFirestoreSettings(settings);


        //현재 로그인한 유저의 정보 가져오기
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //만약에 현재 로그인이 된 상태라면
        if (user != null) {
            // Name, email address, and profile photo Url
           name = user.getDisplayName();
           email = user.getEmail();

            // Check if user's email is verified

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
        }
// 컴포넌트 초기화
        listView = findViewById(R.id.listView);

// listView 를 클릭했을 때 이벤트 추가
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


// 게시물의 번호와 userid를 가지고 DetailActivity 로 이동
                Intent intent = new Intent(ListActivity.this, DetailActivity.class);
                intent.putExtra("board_seq", seqList.get(i));
                intent.putExtra("title", titleList.get(i));
                startActivity(intent);

            }
        });

// 버튼 컴포넌트 초기화
        reg_button = findViewById(R.id.reg_button);

// 버튼 이벤트 추가
        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

// userid 를 가지고 RegisterActivity 로 이동
                Intent intent = new Intent(ListActivity.this, RegisterActivity.class);
                intent.putExtra("userid", name);
                startActivity(intent);
            }
        });
    }


    // onResume() 은 해당 액티비티가 화면에 나타날 때 호출됨
    @Override
    protected void onResume() {
        super.onResume();
// 해당 액티비티가 활성화 될 때, 게시물 리스트를 불러오는 함수를 호출
        getBoard();
    }

    //게시물 불러오는 함수
    public void getBoard(){
        db.collection("Board")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            titleList.clear();
                            seqList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {


                                    String title = document.get("title").toString();
                                    String uid = document.get("uid").toString();
                                    seq = document.get("index").toString();
                                    titleList.add(title);
                                    seqList.add(seq);
                                    ArrayAdapter arrayAdapter = new ArrayAdapter<String>(ListActivity.this, android.R.layout.simple_list_item_1, titleList);
                                    listView.setAdapter(arrayAdapter);
                            }
                        }
                    }
                });

    }


}