package com.issac.mystepcounter.fragment;


import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.issac.mystepcounter.MainActivity;
import com.issac.mystepcounter.R;
import com.issac.mystepcounter.utils.MyHourFormatter;
import com.issac.mystepcounter.view.MyMarkerView;
import com.issac.mystepcounter.view.PieView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentHomePage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentHomePage extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //-------------------------------
    private View mFragmentView;
    private PieView mPieView;
    private LineChart mLineChartHome;
    private MarkerView mv;
    //-------------------------------
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public FragmentHomePage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentHomePage.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentHomePage newInstance(String param1, String param2) {
        FragmentHomePage fragment = new FragmentHomePage();
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
        if (mFragmentView == null){
            mFragmentView = inflater.inflate(R.layout.fragment_home_page,container,false);
            mPieView = (PieView) mFragmentView.findViewById(R.id.stepCounts);
            mLineChartHome = (LineChart) mFragmentView.findViewById(R.id.lineChartHome);
            initChart();


        }
        return mFragmentView;
    }

    private void initChart() {
        mLineChartHome.setOnChartValueSelectedListener((MainActivity)getActivity());
        mLineChartHome.setDrawGridBackground(false);
        mLineChartHome.setNoDataText(getContext().getString(R.string.no_data));
        mLineChartHome.setTouchEnabled(true);
        mLineChartHome.setDragEnabled(false);
        mLineChartHome.setScaleEnabled(false);
        mLineChartHome.setVisibility(View.VISIBLE);
        //双手触摸缩放
        mLineChartHome.setPinchZoom(false);
        mLineChartHome.setDescription("");

        mv = new MyMarkerView(getContext(),R.layout.markview);
        mLineChartHome.setMarkerView(mv);

        /*LimitLine llXAxis = new LimitLine(20f,"Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f,10f,0f);
        llXAxis.setLineColor(Color.DKGRAY);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);*/

        XAxis xAxis = mLineChartHome.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.enableGridDashedLine(10f,10f,0f);
        xAxis.setValueFormatter(new MyHourFormatter());






        Typeface tf = Typeface.DEFAULT_BOLD;
        LimitLine ll1 = new LimitLine(150f,"Upper Limit");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f,10f,0f);
        ll1.setLineColor(Color.TRANSPARENT);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);
        ll1.setTypeface(tf);

        LimitLine ll2 = new LimitLine(-30f,"Lower Limit");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f,10f,0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll2.setTextSize(10f);
        ll2.setTypeface(tf);

        YAxis yAxis = mLineChartHome.getAxisLeft();
        yAxis.removeAllLimitLines();
        yAxis.addLimitLine(ll1);
        yAxis.addLimitLine(ll2);
        yAxis.setAxisMaxValue(200f);
        yAxis.setAxisMinValue(-40f);
        yAxis.enableGridDashedLine(10f,10f,0f);
        yAxis.setDrawZeroLine(true);
        yAxis.setDrawLimitLinesBehindData(true);
        mLineChartHome.getAxisRight().setEnabled(false);

        setData(5,100);
        //mLineChartHome.animateX(2500);

        Legend l = mLineChartHome.getLegend();
        l.setForm(Legend.LegendForm.LINE);



        //--------------------------




    }

    private void setData(int count, float range) {
        ArrayList<Entry> values = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {

            float val = (float) (Math.random() * range) + 3;
            values.add(new Entry(i, val));
        }

        LineDataSet set1;

        if (mLineChartHome.getData() != null &&
                mLineChartHome.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)mLineChartHome.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mLineChartHome.getData().notifyDataChanged();
            mLineChartHome.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "DataSet 1");

            // set the line to be drawn like this "- - - - - -"
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);

            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.fade_red);
                set1.setFillDrawable(drawable);
            }
            else {
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            // set data
            mLineChartHome.setData(data);
        }

    }


    @Override
    public void onResume() {
        mPieView.startCircleAnimation();
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        Log.i("Main","-->onDestroyView");
        super.onDestroyView();
        if (null != mFragmentView){
            ((ViewGroup)mFragmentView.getParent()).removeView(mFragmentView);
        }
    }

    public void drawMarkerView(Entry e, Highlight h){


    }
}
