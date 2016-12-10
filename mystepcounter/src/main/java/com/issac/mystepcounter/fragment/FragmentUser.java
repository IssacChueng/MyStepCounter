package com.issac.mystepcounter.fragment;


import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.issac.mystepcounter.AppContext;
import com.issac.mystepcounter.Login;
import com.issac.mystepcounter.MainActivity;
import com.issac.mystepcounter.R;
import com.issac.mystepcounter.UserActivity;
import com.issac.mystepcounter.pojo.User;
import com.issac.mystepcounter.view.CircleImageView;

import java.io.File;
import java.io.FileNotFoundException;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentUser#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentUser extends Fragment implements OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //-------------------------------
    private CircleImageView avatar;
    private TextView userName;
    private String path;
    public static int RESULT_LOGOUT=1;
    //-------------------------------

    public FragmentUser() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentUser.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentUser newInstance(String param1, String param2) {
        FragmentUser fragment = new FragmentUser();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_user, container, false);
        avatar = (CircleImageView) root.findViewById(R.id.img_avatar_big);
        userName = (TextView) root.findViewById(R.id.user_name);
        userName.setOnClickListener(this);
        avatar.setOnClickListener(this);
        Log.i("click","avatar.hasListener"+avatar.hasOnClickListeners());
        initUser();
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            initUser();
        }else if(resultCode == RESULT_LOGOUT){
            initUser();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initUser(){
        Log.i(AppContext.Tag,AppContext.HASUSER+"");

        if (AppContext.HASUSER){
            User user = BmobUser.getCurrentUser(User.class);
            /*BmobFile file = user.getAvatar();
            AppContext.setUserAvatar(getContext(),avatar,file);*/
            Log.i(AppContext.Tag,AppContext.HASUSER+""+"username="+BmobUser.getCurrentUser().getUsername());
            Log.i(AppContext.Tag,getActivity().getFilesDir()+AppContext.avatarFileName);
            avatar.setImageBitmap(AppContext.getBitmapByUrl(getActivity().getFilesDir()+AppContext.avatarFileName));
            userName.setText(user.getUsername());

        }else {
            userName.setText(null);
            avatar.setImageBitmap(AppContext.getBitmapById(R.mipmap.ic_user));
        }
    }

    private String getImagePath(Uri uri, String selection) {

        String path = null;
        Cursor cursor = getActivity().getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            }
            cursor.close();

        }
        return path;
    }

    @Override
    public void onClick(View v) {
        Log.i("click","clickavatar");
        switch (v.getId()){
            case R.id.img_avatar_big:
                Log.i("click","clickavatar");
                if (AppContext.HASUSER){
                    Intent intent = new Intent(getActivity(), UserActivity.class);
                    startActivityForResult(intent,0);
                }else{
                    //// TODO: 2016/11/24 登录注册页
                    Intent intent = new Intent(getActivity(), Login.class);
                    startActivityForResult(intent,0);
                }
                break;
            case R.id.user_name:
                Log.i("click","clickavatar");
                if (AppContext.HASUSER){
                    /*Intent intent = new Intent();
                    intent.setType("image*//*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent,1);*/
                    Intent intent = new Intent(getActivity(), UserActivity.class);
                    startActivityForResult(intent,0);
                }else{
                    //// TODO: 2016/11/24 登录注册页
                    Intent intent = new Intent(getActivity(), Login.class);
                    startActivityForResult(intent,0);
                }
                break;
            case R.id.click_weibo:
                break;
            case R.id.click_noti:
                break;
            case R.id.click_share:
                break;
            case R.id.click_app:
                break;
            case R.id.click_about:
                break;
        }
    }
}
