package com.example.sw221103;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class DrawActivity extends AppCompatActivity implements View.OnTouchListener {
    private Paint paint = new Paint();
    private Bitmap bitmap;
    private Canvas canvas;
    private ImageView imageView;
    private Button saveButton;
    private float startX, startY;
    private static final int PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.draw_main);

        // View 초기화
        imageView = findViewById(R.id.imageView);
        saveButton = findViewById(R.id.saveButton);

        // 그림판 비트맵 생성
        bitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        imageView.setImageBitmap(bitmap);





        // 터치 이벤트 리스너 등록
        imageView.setOnTouchListener(this);

        // 저장 버튼 클릭 이벤트 리스너 등록
        saveButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                // 권한 체크
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // 권한이 있으면 저장
                    saveToGallery();
                } else {
                    // 권한이 없으면 권한 요청
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                }
            }
        });




        Button button1 = (Button) findViewById(R.id.clearButton) ;
        button1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                bitmap.eraseColor(Color.WHITE);
                view.invalidate();
            }

        });

    }







    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 터치 다운 시 시작 좌표 저장
                startX = event.getX();
                startY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                // 터치 이동 시 선 그리기
                canvas.drawLine(startX, startY, event.getX(), event.getY(), paint);
                imageView.invalidate();
                startX = event.getX();
                startY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                // 터치 업 시 마지막 선 그리기
                canvas.drawLine(startX, startY, event.getX(), event.getY(), paint);
                imageView.invalidate();
                break;
        }
        return true;
    }

    private void saveToGallery() {
        // 그림판 비트맵 저장
        String displayName = "MyDrawing_" + System.currentTimeMillis() + ".jpg";
        String mimeType = "image/jpeg";
        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DISPLAY_NAME, displayName);
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType);
        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            Toast.makeText(this, "이미지가 갤러리에 저장되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("DrawActivity", "이미지 저장 실패", e);
            Toast.makeText(this, "이미지 저장에 실패하였습니다.", Toast.LENGTH_SHORT).show();
        }

        // Firebase Storage에 업로드
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("images/" + displayName);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("DrawActivity", "Firebase Storage 업로드 실패", exception);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("DrawActivity", "Firebase Storage 업로드 성공");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveToGallery();
            } else {
                Toast.makeText(this, "권한이 없어 이미지를 저장할 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}