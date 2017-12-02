package com.example.zju.markmark;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private String fileType = "";
    private String filePath = "";
    private MarkedTextView textView;
    private String text;

    private boolean editMode = false;
    private String oldText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                textView = (MarkedTextView)findViewById(R.id.content);
                text = textView.getText().toString();
                editMode = !editMode;
                if (editMode) {
                    String newText = "";
                    for (String retval: text.split("。")) {
                        retval = retval.concat("。\n\n");
                        newText = newText.concat(retval);
                    }
                    /*textView.setText(newText);*/
                    textView.setText(text, TextView.BufferType.SPANNABLE);
                    getEachSentence(textView);
                    textView.setMovementMethod(LinkMovementMethod.getInstance());
                    oldText = text;
                }
                else {
                    /*textView.setText(oldText);*/
                    textView.setText(text);
                }

                /*Snackbar.make(view, "Begin Editing", Snackbar.LENGTH_LONG)
                        .setAction("How to edit?",new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(MainActivity.this,"Draw and click.",Toast.LENGTH_SHORT).show();
                            }
                        }).show();*/
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Log.i(TAG, "onCreate()");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent!=null) {
            Log.d(TAG, "INTENT IS NOT NULL");
            fileType = intent.getStringExtra("file_type");
            filePath = intent.getStringExtra("file_path");
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        if (filePath!=null && !filePath.equals("")) {
            Log.d(TAG, filePath + "  of  " + fileType);
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"UTF-8"));
                StringBuffer sb = new StringBuffer();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                text = sb.toString();
            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                if (br!=null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            textView = (MarkedTextView) findViewById(R.id.content);
            textView.setText(text);

            Log.i(TAG, "onStart()");
            try {
                File dir = new File("/storage/emulated/0/JS");
                File [] files = dir.listFiles();
                textView.cl();
                for (int i = 0; i < files.length; i++) {
                    String fileName = files[i].getAbsolutePath();
                    fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                    if (filePath.substring(filePath.lastIndexOf("/") + 1).replace(".txt", ".json").equals(fileName.substring(fileName.indexOf(":") + 2))) {
                        Log.i(TAG, filePath.substring(filePath.lastIndexOf("/") + 1).replace(".txt", ".json") + " ------ " + fileName.substring(fileName.indexOf(":") + 2));
                        br = new BufferedReader(new InputStreamReader(new FileInputStream(files[i].getAbsolutePath()),"UTF-8"));
                        /*Log.i(TAG, files[i].getAbsolutePath());*/
                        StringBuffer sb = new StringBuffer();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line);
                        }
                        String jsonstring = sb.toString();
                        /*Log.i(TAG, jsonstring);*/
                        GsonBuilder builder=new GsonBuilder();
                        Gson gson=builder.create();
                        Mark mmark = gson.fromJson(jsonstring, Mark.class);
                        for (MarkEntity mE : mmark.getEntityMentions()) {
                            int pos = text.indexOf(mE.getText(), 0);
                            /*Log.i(TAG, String.valueOf(pos));*/
                            while (pos != -1) {
                                textView.setStart(pos);
                                textView.setEnd(pos + mE.getTextLength() - 1);
                                textView.setLabel(mE.getLabel());
                                pos = text.indexOf(mE.getText(), pos + 1);
                            }
                        }
                    }
                }
                textView.invalidate();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if (br!=null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        editMode = false;
    }
    @Override
    public void onBackPressed() { //按下手机的后退按钮
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START); //如果左侧的菜单处于打开状态，就关闭菜单
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            textView = (MarkedTextView) findViewById(R.id.content);
            String str = "<h1>操作说明</h1>" +
                    "<h2>1. 如何打开文本文件？</h2>" +
                    "<p>你可以将需要标注的文本文件放在MarkMark下的txt文件夹里，这样点开侧边栏的“打开TXT”就能直接在列表里看到你的文本文件了。</p>" +
                    "<p>当然，方便起见，你也可以直接将文本文件放在存储目录下的任意文件夹下，我们右上角菜单的“显示全部文件”可以让你很快捷地找到你的文件。</p>" +
                    "<h2>2. 如何标注文本？</h2>" +
                    "<p>在打开文本文件后，点击右下角地浮动按钮便可以将文本分成一个一个的句子。</p>" +
                    "<p>你可以选择打开任意句子进入标注模式。</p>" +
                    "<p>在标注模式中，与其他文本编辑文件不同，你在选择文本时不需要长按触发，只需要轻触滑动或者点击字符便可选择想要的文本，并可以通过再次轻触滑动或者点击字符进行加选减选。</p>" +
                    "<p>每次选择后都有对话卡片弹出，对话卡片上共有三种实体（国家、城市、人物）和两种关系（城市及其所在国家、人物及其所属国家）可以选择，不同的实体以不同的颜色填充加以区分，而不同的关系则以不同颜色的线框以示区别，并且，关系的两个实体之间还有细线相连，使其更加一目了然。</p>" +
                    "<p>你也可以通过选择之前标注内容中的任意部分并点击对话卡片上的“删除”将其取消标注。</p>" +
                    "<p>标注后结果将以句子为单位自动保存到MarkMark下的json文件夹里。</p>" +
                    "<h2>3. 如何查看结果？</h2>" +
                    "<p>重新进入到阅读文本界面就可以看到标注结果了。</p>" +
                    "<p>你也可以打开侧边栏的“浏览JSON”查看json源文件。</p>";
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                textView.setText(Html.fromHtml(str, Html.FROM_HTML_MODE_LEGACY));
            } else {
                textView.setText(Html.fromHtml(str));
            }
            return true;
        } else if (id == R.id.font_big) {
            textView = (MarkedTextView) findViewById(R.id.content);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,textView.getTextSize()+3);//getTextSize获取的值是px的值
        } else if (id == R.id.font_small) {
            textView = (MarkedTextView) findViewById(R.id.content);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,textView.getTextSize()-3);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_opentxt) {
            Intent intent = new Intent(MainActivity.this,FileListActivity.class);
            intent.putExtra("file_type","txt");
            startActivity(intent);
        } else if (id == R.id.nav_savejson) {

        } else if (id == R.id.nav_openjson) {
            Intent intent = new Intent(MainActivity.this,FileListActivity.class);
            intent.putExtra("file_type","json");
            startActivity(intent);
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /*public void textClicked(View v) {
        if (text != null) {
            String sentenceString = clickedSentence;
            int sentenceNumber = 1;
            String articleString = filePath;
            Intent intent = BangActivity.newIntent(MainActivity.this, articleString, sentenceNumber, sentenceString);
            startActivity(intent);
        }
    }*/

    public void getEachSentence(MarkedTextView textView) {
        Spannable spans = (Spannable)textView.getText();
        Integer[] indices = getIndices(textView.getText().toString().trim(), '。');
        int start = 0;
        int end;
        for (int i = 0; i <= indices.length; i++) {
            ClickableSpan clickSpan = getClickableSpan(i);
            end = (i < indices.length ? indices[i] : spans.length());
            spans.setSpan(clickSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            start = end + 1;
        }
        /*textView.setHighlightColor(Color.BLUE);*/
    }

    private ClickableSpan getClickableSpan(final int index){
        return new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                TextView tv = (MarkedTextView) widget;
                String s = tv.getText().subSequence(tv.getSelectionStart(), tv.getSelectionEnd()).toString();
                if (text != null) {
                    String sentenceString = s;
                    int sentenceNumber = index;
                    String articleString = filePath;
                    Intent intent = BangActivity.newIntent(MainActivity.this, articleString, sentenceNumber, sentenceString);
                    startActivity(intent);
                }
                Log.i(TAG, "tapped on:" + s);
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(Color.BLACK);
                ds.setUnderlineText(false);
            }
        };
    }

    public static Integer[] getIndices(String s, char c) {
        int pos = s.indexOf(c, 0);
        List<Integer> indices = new ArrayList<Integer>();
        while (pos != -1) {
            indices.add(pos);
            pos = s.indexOf(c, pos + 1);
        }
        return (Integer[]) indices.toArray(new Integer[0]);
    }

}