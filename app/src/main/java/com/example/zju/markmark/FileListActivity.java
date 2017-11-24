package com.example.zju.markmark;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class FileListActivity extends AppCompatActivity {

    private static final String TAG = "FileListActivity";
    private String fileType = "";
    ListView listView;
    SimpleAdapter simpleAdapter;
    ArrayList<String> paths = new ArrayList<String>();
    ArrayList<String> names = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        Intent intent = getIntent();
        fileType = intent.getStringExtra("file_type");
        Log.d(TAG,fileType);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        File root = Environment.getExternalStorageDirectory(); //获得外部存储的根目录
        GenFileList(root,paths,names);
        ArrayList<Map<String, String>> maps = new ArrayList<Map<String,String>>();
        for (int i=0; i<paths.size(); i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("first", names.get(i));
            map.put("second", paths.get(i));
            maps.add(map);
        }
        simpleAdapter = new SimpleAdapter(FileListActivity.this, maps, R.layout.file_list,
                new String[]{"first","second"}, new int[]{android.R.id.text1,android.R.id.text2});
        listView = (ListView) findViewById(R.id.file_list);
        listView.setAdapter(simpleAdapter);
        simpleAdapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                String absolutePath = paths.get(position) + "/" + names.get(position);
                Toast.makeText(FileListActivity.this,absolutePath,Toast.LENGTH_LONG).show();
            }
        });
    }

    //遍历文件夹，对于符合格式要求的文件，把路径存入paths中，把单纯的文件名存入names中
    public void GenFileList (File dir, ArrayList<String> paths, ArrayList<String> names) {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) { //是文件夹
                 GenFileList(file,paths,names);
            } else { //是文件
                if (file.getName().endsWith("."+fileType)) { //如果文件名以 .txt或者.json结尾
                    names.add(file.getName());
                    paths.add(file.getParent());
                }
            }
        }
    }
}
