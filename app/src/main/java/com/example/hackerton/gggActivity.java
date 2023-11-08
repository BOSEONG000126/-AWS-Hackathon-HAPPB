package com.example.hackerton;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.KKimage;


import java.io.File;

public class gggActivity extends AppCompatActivity {

    private final int GALLERY_REQ_CODE = 1000;
    EditText text;
    Button Bt;

    private Uri selectedImageUri;
    private String imagePath = "kk";
    private ImageView imageView ;



    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQ_CODE && resultCode == RESULT_OK && data != null) {
            // 선택한 이미지의 URI 가져오기
            selectedImageUri = data.getData();
            imagePath = getRealPathFromURI(selectedImageUri);

            imageView.setImageURI(selectedImageUri);



        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ggg);

        Intent intent1 = getIntent();
        String userId = intent1.getStringExtra("userId");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, PERMISSION_REQUEST_CODE);
        } else {
            // 권한이 이미 허용된 경우 작업 수행
            // ...
        }





        imageView = findViewById(R.id.imageButton);

        text = findViewById(R.id.Explain);
        Log.i("MyAmplifyApp", "KKimage wit");
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 갤러리 앱 실행
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GALLERY_REQ_CODE);

            }
        });

        //저장
        Bt = findViewById(R.id.UploadButton);
        Bt.setOnClickListener(v->{
            String Text = text.getText().toString();

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    String accessKey = "AKIAXMKJTUBCIJTY5FI6";
                    String secretKey = "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";

                    AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

                    // S3 클라이언트 생성
                    AmazonS3 s3Client = new AmazonS3Client(credentials);
                    s3Client.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2));

                    // 업로드할 이미지 파일
                    File imageFile = new File(imagePath);
                    Log.i("MyAmplifyApp", "KKimage wit");

                    // S3 버킷 및 객체 키
                    String bucketName = "imagekk";
                    PutObjectRequest request = new PutObjectRequest(bucketName, imageFile.getName(), imageFile);
                    s3Client.putObject(request);
                    Log.i("MyAmplifyApp", imageFile.getName());
                }
            });
            thread.start();

            KKimage model = KKimage.builder()
                    .user(userId)
                    .imageUrl(imagePath)
                    .text(Text)
                    .build();
            Amplify.API.mutate(ModelMutation.create(model),
                    response -> Log.i("MyAmplifyApp", "KKimage with id: " + response.getData().getId()),
                    error -> Log.e("MyAmplifyApp", "Create failed", error)
            );

            Intent intent2 = new Intent(this, ProfileActivity.class);
            startActivity(intent2);


        });



    }

    // AsyncTask 클래스 정의
    private class NetworkTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            // 네트워크 작업 수행
            // 여기에 S3 업로드 코드를 넣으세요




            return null;
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);
        if (cursor == null) return null;
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }



}