package com.example.hackerton;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.KKimage;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {


    ArrayList<Uri> uri = new ArrayList<>();
    //RecyclerAdapter adapter;

    private static final int Read_Permission = 101;

    private final int Fragment_1 = 1;
    private final int Fragment_2 = 2;

    private final int GALLERY_REQ_CODE = 1000;
    ImageView imgGallery;

    TextView text;

    List<Object[]> itemList = new ArrayList<>();
    ImageView imageView1 ,imageView2,imageView3;
    TextView id11,id22,id33;
    TextView text11,text22,text33;
    List<Bitmap> bitmapList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent1 = getIntent();
        String userId = intent1.getStringExtra("userId");

        imageView1 = findViewById(R.id.imageView3);
        imageView2 = findViewById(R.id.imageView4);
        imageView3 = findViewById(R.id.imageView5);

        /*
        id11 = findViewById(R.id.ID);
        id22 = findViewById(R.id.ID2);
        id33 = findViewById(R.id.ID3);
        text11 = findViewById(R.id.text);
        text22 = findViewById(R.id.text2);
        text33 = findViewById(R.id.text3);
        */


        Amplify.API.query(
                ModelQuery.list(KKimage.class),
                response -> {
                    for (KKimage kKimage : response.getData()) {
                        // 각 정보에 대한 작업을 수행합니다.
                        String text = kKimage.getText();
                        String url = kKimage.getImageUrl();
                        String id = kKimage.getUser();

                        String imagePath = url;
                        if (imagePath != null) {
                            String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
                            Log.i("bobo", imagePath);
                            Log.i("bobo", imageName);

                            // S3에서 이미지 다운로드
                            AmazonS3Client s3Client = new AmazonS3Client(new BasicAWSCredentials("AKIAXMKJTUBCIJTY5FI6", "xPugeZIUA4InPTgmveumACShTOLTVJyjxCwVWeoi"));
                            S3Object s3Object = s3Client.getObject("imagekk", imageName);

                            Log.i("Amplify API", s3Object.toString());
                            // 다운로드한 이미지를 Bitmap으로 변환
                            InputStream inputStream = s3Object.getObjectContent();
                            Log.i("Amplify API", inputStream.toString());
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            Log.i("Amplify API", bitmap.toString());
                            Log.i("Amplify API", id);
                            Log.i("Amplify API", text);
                            itemList.add(new Object[]{bitmap, id, text});
                            //imageView1.setImageBitmap(b1);
                            //id11.setText(text1);
                            //text11.setText(text2);

                            for (Object[] item : itemList) {
                                Bitmap b1 = (Bitmap) item[0];
                                String text1 = (String) item[1];
                                String text2 = (String) item[2];
                                bitmapList.add(b1);
                                //imageView1.setImageBitmap(b1);
                                //id11.setText(text1);
                                //text11.setText(text2);
                                Log.d("MainActivity", "Bitmap: " + b1 + ", Text1: " + text1 + ", Text2: " + text2);
                            }

                        } else {
                            // url이 Null인 경우에 대한 처리
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // itemList를 사용하여 UI 업데이트 작업 수행
                            for (Object[] item : itemList) {
                                Bitmap bitmap = (Bitmap) item[0];
                                String id = (String) item[1];
                                String text = (String) item[2];

                                //imageView1.setImageBitmap(bitmap);
                                //id11.setText(id);
                                //text11.setText(text);
                            }

                            imageView1.setImageBitmap((Bitmap) itemList.get(0)[0]);
                            //id11.setText(itemList.get(1)[1].toString());
                            //text11.setText(itemList.get(1)[2].toString());

                            imageView2.setImageBitmap((Bitmap) itemList.get(1)[0]);
                            //id22.setText(itemList.get(2)[1].toString());
                            //text22.setText(itemList.get(2)[2].toString());

                            imageView3.setImageBitmap((Bitmap) itemList.get(2)[0]);
                            //id33.setText(itemList.get(3)[1].toString());
                            //text33.setText(itemList.get(3)[2].toString());


                        }
                    });

                },
                error -> {
                    // 쿼리 실패 시 오류 처리를 수행합니다.
                    Log.e("Amplify API", "Query failed", error);
                }
        );






        text = findViewById(R.id.ID);
        text.setText(userId);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_profile);


        FloatingActionButton btnGallery = findViewById(R.id.floatingActionButton);

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), gggActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                //Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //startActivityForResult(intent, GALLERY_REQ_CODE);

            }
        });

        FragmentView(Fragment_1);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.bottom_home:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
                case R.id.bottom_message:
                    startActivity(new Intent(getApplicationContext(), MessageActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
                case R.id.bottom_polar_bear:
                    startActivity(new Intent(getApplicationContext(), PolarBearActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
                case R.id.bottom_info:
                    startActivity(new Intent(getApplicationContext(), InfoActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
                case R.id.bottom_profile:

                    return true;
            }
            return false;
        });
        

    }
    private void FragmentView(int fragment){

        //FragmentTransactiom를 이용해 프래그먼트를 사용합니다.
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (fragment){
            case 1:
                // 첫번 째 프래그먼트 호출
                Fragment1 fragment1 = new Fragment1();
                transaction.replace(R.id.fragment_container, fragment1);
                transaction.commit();
                break;

            case 2:
                // 두번 째 프래그먼트 호출
                Fragment2 fragment2 = new Fragment2();
                transaction.replace(R.id.fragment_container, fragment2);
                transaction.commit();
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQ_CODE && data != null) {
                Uri imageUri = data.getData();

                // RecyclerView에 이미지 추가하는 로직을 여기에 작성합니다.
            }
        }
    }
}