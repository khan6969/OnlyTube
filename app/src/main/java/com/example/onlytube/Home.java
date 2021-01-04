package com.example.onlytube;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;

public class Home extends Fragment {
    ViewPager  viewPager;
    CircleIndicator circleIndicator;
    ViewPagerAdapter viewPagerAdopter;
    Timer timer;
    Handler handler;
    RecyclerView recyclerView;
    GridLayoutManager gridLayoutManager;
    RecyclerView.Adapter myAdapter;
    RecyclerView.LayoutManager MyLayout;
    IconsAdapter iconsAdapter;
    ArrayList<IconList> StaticImages = new ArrayList<>();


    public Home() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        recyclerView=view.findViewById(R.id.ListOfproduct);
        MyLayout = new  GridLayoutManager(getContext(), 3
                ,GridLayoutManager.VERTICAL,false);
        viewPager = new ViewPager(getContext());
        viewPager = view.findViewById(R.id.Viewpager);
        recyclerView.setLayoutManager(MyLayout);
        recyclerView.setAdapter(myAdapter);
        circleIndicator=view.findViewById(R.id.circleIndicater);
        circleIndicator.setViewPager(viewPager);

        final List<Integer> imagelist = new ArrayList<>();
        imagelist.add(R.drawable.youtube);
        imagelist.add(R.drawable.download);
        imagelist.add(R.drawable.facebook);
        imagelist.add(R.drawable.instagram);

        viewPagerAdopter = new ViewPagerAdapter(imagelist,getContext());
        viewPager.setAdapter(viewPagerAdopter);
        circleIndicator.setViewPager(viewPager);

        timer = new Timer();
        handler = new Handler();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        int i= viewPager.getCurrentItem();
                        if(i==imagelist.size()-1){
                            i=0;
                            viewPager.setCurrentItem(i,true);
                        }else{
                            i++;
                            viewPager.setCurrentItem(i,true);
                        }
                    }
                });
            }
        },3000,3000);

        StaticImages  = new ArrayList<IconList>();
        StaticImages.add(new IconList("Youtube",R.drawable.youtube));
        StaticImages.add(new IconList("FaceBook",R.drawable.facebook));
        StaticImages.add(new IconList("Instagram",R.drawable.instagram));
        iconsAdapter = new IconsAdapter(getContext(),StaticImages);
        recyclerView.setAdapter(iconsAdapter);
        return view;


    }
}