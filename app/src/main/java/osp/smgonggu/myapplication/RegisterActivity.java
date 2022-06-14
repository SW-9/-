package osp.smgonggu.myapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    private FirebaseFirestore db;

    //storage-> 이미지를 업로드 하기 위한 저장소
    FirebaseStorage storage;
    StorageReference storageRef;//스토리지 주소
    StorageReference ThumbnailRef;
    StorageReference ThumbnailImgRef;
    // 로그에 사용할 TAG 변수 선언
    final private String TAG = getClass().getSimpleName();

    // 사용할 컴포넌트 선언
    EditText title_et, content_et;
    Button reg_button;
    TextView textview;
    Button getidx;//야매용 버튼... 전역변수의 의미로 사용할 예정 index를 얻어오는데 사용함
    // 유저아이디 변수
    String nick;
    String title;
    String content;
    Bitmap thumbnail;
    String index;
    private final int GET_GALLERY_IMAGE = 200;
    private ImageView imageview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

// ListActivity 에서 넘긴 userid 를 변수로 받음

// 컴포넌트 초기화
        title_et = findViewById(R.id.title_et);
        content_et = findViewById(R.id.content_et);
        reg_button = findViewById(R.id.reg_button);
        imageview = findViewById(R.id.imageView);
        textview = findViewById(R.id.textView);
        getidx= findViewById(R.id.getidx);
        getidx.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        //데이터를 추가시키는 함수
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);


        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        //현재 로그인한 유저의 정보 가져오기
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    nick= user.getDisplayName();

        imageview.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            addImg.launch(intent);
        });
        imageview.setDrawingCacheEnabled(true);
        imageview.buildDrawingCache();

// 버튼 이벤트 추가
        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
// 게시물 등록 함수
                if (title_et.getText().toString().equals("")) {
                    Toast.makeText(RegisterActivity.this, "제목을 입력해주세요", Toast.LENGTH_SHORT).show();
                } else if (content_et.getText().toString().equals("")) {
                    Toast.makeText(RegisterActivity.this, "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                // else if(textview.getText()!="이미지가 등록되었습니다."){
                //     Toast.makeText(RegisterActivity.this,"이미지를 입력해주세요",Toast.LENGTH_SHORT).show();
                // }
                else {
                    title = title_et.getText().toString();
                    thumbnail = ((BitmapDrawable) imageview.getDrawable()).getBitmap();
                    content = content_et.getText().toString();
                    regboard(nick, title, thumbnail, content);
                }
            }
        });

    }

    //버튼을 클릭하면 갤러리를 불러오고 불러온 이미지를 이미지뷰에 추가하는 함수
    ActivityResultLauncher<Intent> addImg = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Log.e(TAG, "result : " + result);
                        Intent intent = result.getData();
                        Log.e(TAG, "intent : " + intent);
                        Uri uri = intent.getData();
                        Log.e(TAG, "uri : " + uri);
                        imageview.setImageURI(uri);
                        Glide.with(RegisterActivity.this)
                                .load(uri)
                                .into(imageview);
                        textview.setText("이미지가 등록되었습니다.");
                    }
                }
            });


    public void regboard(String uid, String title, Bitmap thumbnail,String content) {
        getIndex();

        String index= Integer.toString(Integer.parseInt(getidx.getText().toString())+1);
        Board board = new Board(uid, title, content,index);
        //uploadImg(thumbnail,index);//이미지 등록함수
        db.collection("Board").add(board);
        Intent intent = new Intent(RegisterActivity.this, ListActivity.class);
        Toast.makeText(RegisterActivity.this, "등록되었습니다.", Toast.LENGTH_SHORT).show();
        startActivity(intent);
        finish();
    }

    void getIndex() {

        db.collection("Board")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                getidx.setText(document.get("index").toString());
                            }
                        }
                    }
                });


    }

    void uploadImg(Bitmap thumbnail, int index) {
        String a = "index" + index + ".jpg";
        ThumbnailRef = storageRef.child(a);
        ThumbnailImgRef = storageRef.child("images" + "index" + index + ".jpg");
        ThumbnailRef.getName().equals(ThumbnailImgRef.getName());
        ThumbnailRef.getPath().equals(ThumbnailImgRef.getPath());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = ThumbnailRef.putBytes(data);


    }
}
