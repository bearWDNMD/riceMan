package com.example.riceMan.ui.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.riceMan.R;
import com.example.riceMan.adapter.ManageAdapter;
import com.example.riceMan.entity.Rice;
import com.example.riceMan.ui.activity.ArticleDetailActivity;
import com.example.riceMan.db.DatabaseHelper;
import com.example.riceMan.utils.DatabaseUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ManageFragment extends Fragment {

    private static final String ARG_SHOW_TEXT = "text";
    private ListView listView;
    private ArrayList<Rice> rices=new ArrayList<>();
    private String mContentText;
    private int flag=0;

    public ManageFragment() {
        // Required empty public constructor
    }


    public static ManageFragment newInstance(String param1) {
        ManageFragment fragment = new ManageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SHOW_TEXT, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mContentText = getArguments().getString(ARG_SHOW_TEXT);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.fragment_know, container, false);
        listView=(ListView)rootView.findViewById(R.id.lv_loadknow);
        initView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(getContext(), ArticleDetailActivity.class);
                Rice rice= ManageAdapter.list.get(i);
                Log.i("rice",rice.toString());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
                Date date = new Date(System.currentTimeMillis());
                DatabaseUtil.insert_History(rice.getName(),String.valueOf(rice.getId()),simpleDateFormat.format(date),0,getContext());
                try {
                    DatabaseHelper dbHelper = new DatabaseHelper(getContext(), "riceKnow.db",null,1);
                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                    Cursor cursor = db.query("Rices", null, "id=?", new String[]{String.valueOf(rice.getId())}, null, null, null, null);
                    while (cursor.moveToNext())
                    {
                        int x=cursor.getInt(cursor.getColumnIndex("flag"));
                        rice.setFlag(x);
                    }
                }catch (Exception e){
                    Log.e("<<<<<<<","error");
                }
                intent.putExtra("rice",rice);

                startActivity(intent);
            }
        });
        return rootView;
    }
    private void initView(){
        if (flag!=1){
            flag=1;
            try {
                DatabaseHelper dbHelper = new DatabaseHelper(getActivity(), "riceKnow.db",null,1);
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.query("Rices", null, null, null, null, null, null, null);
                if (cursor.moveToFirst()){
                    do {
                        Rice rice=new Rice();
                        rice.setId(cursor.getInt(cursor.getColumnIndex("id")));
                        rice.setPrice(cursor.getString(cursor.getColumnIndex("price")));
                        rice.setName(cursor.getString(cursor.getColumnIndex("name")));
                        rice.setImgurl(cursor.getString(cursor.getColumnIndex("imgurl")));
                        rices.add(rice);
                    }while (cursor.moveToNext());
                }
            }catch (Exception e){
                Log.e("ManageFragment",e.toString());
            }
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(new ManageAdapter(rices,getActivity()));
            }
        });
    }
}