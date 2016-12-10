package com.issac.mystepcounter;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;

public class AvatarEdit extends AppCompatActivity {

    private final int ALBUM_OK = 1, CAMERA_OK = 2,CUT_OK=3;
    private File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_edit);

        file = new File(Environment.getExternalStorageDirectory(),"temp.jpg");
        file.delete();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_album,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_openCameral:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(intent,CAMERA_OK);
                break;
            case R.id.menu_openAlbum:
                Intent albumintent = new Intent(Intent.ACTION_PICK,null);
                albumintent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                startActivityForResult(albumintent,ALBUM_OK);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case ALBUM_OK:
                if (data!= null){
                    clipPhoto(data.getData(),ALBUM_OK);
                }
                break;
            case CAMERA_OK:
                if (file.exists()){
                    clipPhoto(Uri.fromFile(file),CAMERA_OK);
                }
                break;
            case CUT_OK:
                if (data!= null){
                    getBitmapFromUri(data);
                }else
                {
                    Log.e("Main","data为空");
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getBitmapFromUri(Intent data) {
        setResult(RESULT_OK,data);
    }

    public void clipPhoto(Uri uri,int type) {


        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop = true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例，这里设置的是正方形（长宽比为1:1）
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", false);

        /**
         * 此处做一个判断
         * １，相机取到的照片，我们把它做放到了定义的目录下。就是file
         * ２，相册取到的照片，这里注意了，因为相册照片本身有一个位置，我们进行了裁剪后，要给一个裁剪后的位置，
         * 　　不然onActivityResult方法中，data一直是null
         */
        if(type==CAMERA_OK)
        {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        }
        startActivityForResult(intent, CUT_OK);
    }
}
