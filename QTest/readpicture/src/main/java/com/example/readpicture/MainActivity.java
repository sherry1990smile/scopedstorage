package com.example.readpicture;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import static android.media.MediaRecorder.VideoSource.CAMERA;

public class MainActivity extends AppCompatActivity {
    private Button select_photo;
    private ImageView imageview;
    public static final int SELECT_PHOTO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        select_photo = (Button) findViewById(R.id.select_photo);
        imageview = (ImageView) findViewById(R.id.imageview);
        select_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //从相册中选取图片
                select_photo();
            }
        });
    }

    /**
     * 从相册中获取图片
     * */
    public void select_photo() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
            Intent picture = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(picture,CAMERA );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA && resultCode == Activity.RESULT_OK && null != data){


            Uri selectedImage = data.getData();
//            String[] filePathColumns={MediaStore.Images.Media.DATA};
//            Cursor c = this.getContentResolver().query(selectedImage, filePathColumns, null,null, null);
//            c.moveToFirst();
//            int columnIndex = c.getColumnIndex(filePathColumns[0]);
//            String picturePath= c.getString(columnIndex);
//            c.close();

            //获取图片并显示
            Bitmap bitmap = null;
            try {
                bitmap = getBitmapFromUri(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageview.setImageBitmap(bitmap);
     //       displayImage(picturePath);
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    /**
     * 根据图片路径显示图片的方法
     * */
    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
  //          ContentResolver cr = context.getContentResolver();
 //           ParcelFileDescriptor fd = cr.openFileDescriptor(captureUri, "r");
//            Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor());
            imageview.setImageBitmap(bitmap);
        }else {
            Toast.makeText(MainActivity.this,"failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

}
