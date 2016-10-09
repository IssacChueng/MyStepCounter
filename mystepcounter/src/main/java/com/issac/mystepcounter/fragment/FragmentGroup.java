package com.issac.mystepcounter.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.issac.mystepcounter.R;
import com.issac.mystepcounter.view.ColorTextStrip;
import com.issac.mystepcounter.view.NoScrollViewPager;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentGroup#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentGroup extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    //--------------------------------------------
    private RecyclerView recyclerViewSpeed;
    private RecyclerView recyclerViewDistance;
    private ArrayList<View> viewContainer = new ArrayList<>(2);
    private ViewPager viewPager;
    private String[] items = new String[]{"挑战","holder","challenge"};
    //两个tab下的线
    private View line_tab1;
    private View line_tab2;
    //两个tab
    private ColorTextStrip tab1;
    private ColorTextStrip tab2;
    //------------------------------------------

    public FragmentGroup() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentGroup.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentGroup newInstance(String param1, String param2) {
        FragmentGroup fragment = new FragmentGroup();
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
        Context mContext = getContext();
        line_tab1 = view.findViewById(R.id.line_rank_1);
        line_tab2 = view.findViewById(R.id.line_rank_2);
        tab1 = (ColorTextStrip) view.findViewById(R.id.tab_rank_1);
        tab2 = (ColorTextStrip) view.findViewById(R.id.tab_rank_2);
        tab1.setOnClickListener(this);
        tab2.setOnClickListener(this);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager_rank);
        recyclerViewSpeed = new RecyclerView(mContext);
        recyclerViewDistance = new RecyclerView(mContext);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        );
        recyclerViewSpeed.setLayoutParams(lp);
        recyclerViewSpeed.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerViewSpeed.setAdapter(new RecyclerAdapter());
        recyclerViewDistance.setLayoutParams(lp);
        recyclerViewDistance.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerViewDistance.setAdapter(new RecyclerAdapter());
        viewContainer.add(recyclerViewSpeed);
        viewContainer.add(recyclerViewDistance);

        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return viewContainer.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view==object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ((ViewPager)container).addView(viewContainer.get(position));
                return viewContainer.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                ((ViewPager)container).removeView(viewContainer.get(position));
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tab1.resetColor();
                tab2.resetColor();
                line_tab1.setVisibility(View.INVISIBLE);
                line_tab2.setVisibility(View.INVISIBLE);
                if (position==0){
                    tab1.drawColor(0);
                    line_tab1.setVisibility(View.VISIBLE);
                }else{
                    tab2.drawColor(0);
                    line_tab2.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tab_rank_1:
                viewPager.setCurrentItem(0);
                break;
            case R.id.tab_rank_2:
                viewPager.setCurrentItem(1);
                break;
        }
    }

    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                    getContext()).inflate(R.layout.item_rank, parent, false));

            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.tv.setText(items[position]);
        }

        @Override
        public int getItemCount() {
            return items.length;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tv;

            public MyViewHolder(View view) {
                super(view);
                tv = (TextView) view.findViewById(R.id.item_name);
            }
        }
    }

}
