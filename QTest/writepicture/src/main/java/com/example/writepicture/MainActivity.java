package com.example.writepicture;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import static android.media.MediaRecorder.VideoSource.CAMERA;

public class MainActivity extends AppCompatActivity {
    private Button take_photo;
    public static final int TAKE_PHOTO = 1;
    private ImageView imageview;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        take_photo = (Button) findViewById(R.id.take_photo);
        imageview = (ImageView) findViewById(R.id.imageview);

        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //拍照获取图片
                take_photo();
            }
        });
    }

    /**
     *拍照获取图片
     **/
    public void take_photo() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA && resultCode == Activity.RESULT_OK && null != data){
            String sdState=Environment.getExternalStorageState();
            if(!sdState.equals(Environment.MEDIA_MOUNTED)){
                Log.d("TAG", "sd card unmount");
                return;
            }
            Bundle bundle = data.getExtras();
            //获取相机返回的数据，并转换为图片格式
            Bitmap bitmap = (Bitmap)bundle.get("data");
            // 首先保存图片
            File file = null;
            String fileName = System.currentTimeMillis() + ".jpg";
 //           File root = new File(Environment.getExternalStorageDirectory(), getPackageName());
            File root = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File dir = new File(root, "images");
            if (dir.mkdirs() || dir.isDirectory()) {
                file = new File(dir, fileName);
            }
            try {
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //其次把文件插入到系统图库
            try {
                MediaStore.Images.Media.insertImage(this.getContentResolver(),
                        file.getAbsolutePath(), fileName, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            // 最后通知图库更新
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                MediaScannerConnection.scanFile(this, new String[]{file.getAbsolutePath()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                mediaScanIntent.setData(uri);
                                sendBroadcast(mediaScanIntent);
                            }
                        });
            } else {
                String relationDir = file.getParent();
                File file1 = new File(relationDir);
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(file1.getAbsoluteFile())));
            }

            //显示图片
            imageview.setImageBitmap(bitmap);
        }

    }
}
