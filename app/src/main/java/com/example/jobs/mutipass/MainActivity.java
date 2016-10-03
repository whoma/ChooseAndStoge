package com.example.jobs.mutipass;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button login;
    private EditText edit_acount;
    private EditText edit_passwd;
    private CheckBox remenber;
    private ImageView muti;
    private PopupWindow window;
    private ListView listView;
    private List<UserData> lists;
    private Gson gson;
    private FileOutputStream out;
    private BufferedWriter writer;
    private FileInputStream in;
    private BufferedReader reader;
    private String acount;
    private String passwd;
    private String writeAcount;
    private String writePasswd;
    private AcountAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        //先看下有没有以前保存的密码
        String data = openFile();
        if (!TextUtils.isEmpty(data) && !data.equals("[]")) {
            lists = gson.fromJson(data, new TypeToken<List<UserData>>() {
            }.getType());
            writeAcount = lists.get(0).getAcount();
            writePasswd = lists.get(0).getPasswd();
            edit_acount.setText(writeAcount);
            edit_passwd.setText(writePasswd);
            remenber.setChecked(true);
        }
        adapter = new AcountAdapter(MainActivity.this, R.layout.simple_item, lists, new CallBackClick() {
            @Override
            public void onClick(View v) {
                int index = (int) v.getTag();
                UserData user = lists.get((Integer) v.getTag());
                switch (v.getId()) {
                    case R.id.muti_acount:
                        writeAcount = user.getAcount();
                        writePasswd = user.getPasswd();
                        edit_acount.setText(writeAcount);
                        edit_passwd.setText(writePasswd);
                        window.dismiss();
                        break;
                    case R.id.image_delete:
                        lists.remove(index);
                        nowStoge();
                        adapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
            }
        });
        listView.setAdapter(adapter);
        window = new PopupWindow(listView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        window.setTouchable(true);
        window.setOutsideTouchable(true);
        window.setBackgroundDrawable(new ColorDrawable());

        muti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.showAsDropDown(v);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acount = edit_acount.getText().toString();
                passwd = edit_passwd.getText().toString();
                //判断用户账号和密码是否正确
                if (isAvilable()) {
                    //存储密码
                    if (remenber.isChecked()) {
                        //是否选择记住密码
                        stogeUser();
                    }
                    //登陆成功
                    Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * 初始化
     */
    public void initView() {
        login = (Button) findViewById(R.id.login);
        edit_acount = (EditText) findViewById(R.id.edit_acount);
        edit_passwd = (EditText) findViewById(R.id.edit_passwd);
        remenber = (CheckBox) findViewById(R.id.remenber);
        muti = (ImageView) findViewById(R.id.muti);
        listView = new ListView(MainActivity.this);
        lists = new ArrayList<UserData>();
        gson = new Gson();
        try {
            //以防止没有创建时读取错误
            out = openFileOutput("user", Context.MODE_APPEND);
            in = openFileInput("user");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        writer = new BufferedWriter(new OutputStreamWriter(out));
        reader = new BufferedReader(new InputStreamReader(in));
        try {
            out.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        acount = passwd = writeAcount = writePasswd = "";
    }

    /**
     * 读取文件
     */
    public String openFile() {
        StringBuilder builder = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return builder.toString();
    }

    /**
     * 进行登陆认证
     * 可以更改认证的内容
     */
    public boolean isAvilable() {
        if (acount.equals("") && passwd.equals("")) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 进行密码的存储
     */
    public void stogeUser() {
        //判断用户是否输入了新的账号和密码
        if (!acount.equals("") && !writeAcount.equals(acount) && !lists.contains(new UserData(acount, passwd))) {
            lists.add(new UserData(acount, passwd));
            String data = gson.toJson(lists);
            try {
                //先进行数据清空
                out = openFileOutput("user", MODE_PRIVATE);
                writer = new BufferedWriter(new OutputStreamWriter(out));
                writer.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 将缓存在lists中的数据进行存储
     */
    public void nowStoge() {
        try {
            out = openFileOutput("user", MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        writer = new BufferedWriter(new OutputStreamWriter(out));
        String data = gson.toJson(lists);
        try {
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
