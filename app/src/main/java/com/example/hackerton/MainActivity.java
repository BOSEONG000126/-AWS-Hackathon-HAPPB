package com.example.hackerton;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.KKimage;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<Object[]> itemList = new ArrayList<>();
    ImageView imageView1 ,imageView2,imageView3,imageView4,imageView5;
    TextView id11,id22,id33,id44,id55;
    TextView text11,text22,text33,text44,text55;
    List<Bitmap> bitmapList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView1 = findViewById(R.id.User_image);
        imageView2 = findViewById(R.id.User_image2);
        imageView3 = findViewById(R.id.User_image3);
        imageView4 = findViewById(R.id.User_image4);
        imageView5 = findViewById(R.id.User_image5);

        id11 = findViewById(R.id.ID);
        id22 = findViewById(R.id.ID2);
        id33 = findViewById(R.id.ID3);
        id44 = findViewById(R.id.ID4);
        id55 = findViewById(R.id.ID5);


        text11 = findViewById(R.id.text);
        text22 = findViewById(R.id.text2);
        text33 = findViewById(R.id.text3);
        text44 = findViewById(R.id.text4);
        text55 = findViewById(R.id.text5);


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
                            itemList.add(new Object[]{bitmap, id, text});

                            for (Object[] item : itemList) {
                                Bitmap b1 = (Bitmap) item[0];
                                String text1 = (String) item[1];
                                String text2 = (String) item[2];
                                bitmapList.add(b1);
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
                            }
                            imageView1.setImageBitmap((Bitmap) itemList.get(0)[0]);
                            id11.setText(itemList.get(0)[1].toString());
                            text11.setText(itemList.get(0)[2].toString());

                            imageView2.setImageBitmap((Bitmap) itemList.get(1)[0]);
                            id22.setText(itemList.get(1)[1].toString());
                            text22.setText(itemList.get(1)[2].toString());

                            imageView3.setImageBitmap((Bitmap) itemList.get(2)[0]);
                            id33.setText(itemList.get(2)[1].toString());
                            text33.setText(itemList.get(2)[2].toString());

                            imageView4.setImageBitmap((Bitmap) itemList.get(3)[0]);
                            id44.setText(itemList.get(3)[1].toString());
                            text44.setText(itemList.get(3)[2].toString());

                            imageView5.setImageBitmap((Bitmap) itemList.get(4)[0]);
                            id55.setText(itemList.get(4)[1].toString());
                            text55.setText(itemList.get(4)[2].toString());



                        }
                    });

                },
                error -> {
                    // 쿼리 실패 시 오류 처리를 수행합니다.
                    Log.e("Amplify API", "Query failed", error);
                }
        );






        Intent intent1 = getIntent();
        String userId = intent1.getStringExtra("userId");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.bottom_home:
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
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
            }
            return false;
        });

    }
}