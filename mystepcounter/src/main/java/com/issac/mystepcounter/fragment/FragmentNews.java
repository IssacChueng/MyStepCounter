package com.issac.mystepcounter.fragment;


import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.google.gson.Gson;
import com.issac.mystepcounter.AppContext;
import com.issac.mystepcounter.NewsActivity;
import com.issac.mystepcounter.R;
import com.issac.mystepcounter.pojo.News;
import com.issac.mystepcounter.pojo.Result;
import com.issac.mystepcounter.pojo.Return;
import com.issac.mystepcounter.utils.AppOperator;
import com.issac.mystepcounter.utils.CacheManager;
import com.issac.mystepcounter.utils.HttpUtil;
import com.issac.mystepcounter.utils.RecyclerAdapter;
import com.issac.mystepcounter.view.MyDecoration;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentNews#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentNews extends Fragment implements SwipeRefreshLayout.OnRefreshListener, RecyclerAdapter.OnItemClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    //--------------------------------------------
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipLayout;
    private RecyclerAdapter adapter;
    private AsyncHttpResponseHandler handler;
    private Context mContext;
    private String CACHE_NAME = getClass().getName();
    private Result result;
    //------------------------------------------

    public FragmentNews() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentNews.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentNews newInstance(String param1, String param2) {
        FragmentNews fragment = new FragmentNews();
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
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        mContext = getContext();
        initWidgit(view);
        initData();
        Log.i("async","async");


        return view;
    }

    private void initWidgit(View view) {
        swipLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeLayout);
        swipLayout.setOnRefreshListener(this);
        swipLayout.setColorSchemeColors(ContextCompat.getColor(mContext,R.color.colorPrimary));
        recyclerView = (RecyclerView) view.findViewById(R.id.news_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecyclerAdapter(mContext,new ArrayList<News>());
        adapter.setListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new MyDecoration(getActivity(),MyDecoration.VERTICAL_LIST));


    }
    private void initData() {
        handler = new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("async","failed");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Gson gson = new Gson();
                Return returndata = gson.fromJson(responseString,Return.class);
                if (returndata.getReason().equals("成功的返回")){
                    result = returndata.getResult();
                    setListData(returndata.getResult());
                }
                Log.i("async","returndata="+responseString);
            }
        };
        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                Result data= (Result) CacheManager.readObject(mContext,CACHE_NAME);
                if (data == null){
                    requestData();
                }else{
                    adapter.add(data.getData());
                    requestData();
                }

            }
        });

    }

    public void requestData(){
        HttpUtil.get("?type=tiyu",handler);
        swipLayout.setRefreshing(false);
    }

    private void setListData(final Result result) {
        adapter.add(result.getData());
        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                CacheManager.saveObject(mContext,result,CACHE_NAME);
            }
        });
    }




    @Override
    public void onRefresh() {
        swipLayout.setRefreshing(true);
        requestData();
    }

    @Override
    public void onClick(int position) {

        if (position<0 || position>=adapter.getItemCount())
            return;
        Intent intent = new Intent(getActivity(), NewsActivity.class);
        intent.putExtra("url",this.result.getData().get(position).getUrl());
        if (AppContext.APILEVEL) {
            startNewsContent(intent);
        }else{
            startActivity(intent);
        }
    }

    @TargetApi(21)
    private void startNewsContent(Intent intent) {
        startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
    }
}
