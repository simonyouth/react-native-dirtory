package com.example.choosedir;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.react.ReactActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zyx on 2018/3/3.
 */

public class ChooseFileActivity extends ReactActivity {
    private static final int REQUESCODE = 5;
    private static final String NEED_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private String mPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    public RecyclerView rvFileView;
    private EditText editText;
    private Button btChose;
    private List<String> data;
    private List<String> searchData;
    private FileAdapter fileAdapter;
    private static final String TAG = "ZYX";
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String res = editable.toString();
            if (res.trim().equals("")) {
                fileAdapter.setData(data);
                return;
            }
            if (searchData == null) {
                searchData = new ArrayList<>();
            }
            searchData.clear();
            for (int i = 0; i < data.size(); i++) {
                String content = data.get(i).substring(data.get(i).lastIndexOf("/")).toLowerCase();
                if (content.contains(res)) {
                    searchData.add(data.get(i));
                }
            }
            Log.d("ZYX", "searchData size " + searchData.size());
            fileAdapter.setData(searchData);
        }
    };

    public static final String SELECTPATH = "SELECTPATH";
    public static final int RESULTCODE = 3;
    public static void enterActivityForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, ChooseFileActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filechoose_activity_main);
        initView();
        initData();
    }
    private void initData() {
        if (Build.VERSION.SDK_INT >= 23) {//判断系统版本是否大于6.0
            if (checkSelfPermission(NEED_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
                //检查是否有读写权限
                loadDataFrompATH(mPath);
            } else {
                requestPermissions(new String[]{NEED_PERMISSION}, REQUESCODE);
            }
            return;
        }
        loadDataFrompATH(mPath);//系统版本小于6.0直接加载数据
    }


    private void loadDataFrompATH(final String mPath) {
        data.clear();//data为RecyclerView中要显示的数据
        new Thread() {
            public void run() {
                super.run();
                File file = new File(mPath);
                File[] listFiles = file.listFiles();
                for (File f : listFiles
                        ) {
                    if (!f.isDirectory() || f.getName().startsWith(".")) {
                        continue;
                    }
                    data.add(f.getAbsolutePath());
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fileAdapter.setData(data);
                    }
                });
            }
        }.start();
    }

    private void initView() {
        editText = (EditText) findViewById(R.id.editText);
        rvFileView = (RecyclerView) findViewById(R.id.rvFileView);
        btChose = (Button) findViewById(R.id.btChose);
        data = new ArrayList<>();
        rvFileView.setLayoutManager(new LinearLayoutManager(this));
        fileAdapter = new FileAdapter(data, this, R.layout.filechoose_layout_fileitme);
        fileAdapter.setEvent(new FileAdapter.Event() {
            @Override
            public void enterNextDir(String path) {
                editText.setText("");
                mPath = path;
                loadDataFrompATH(mPath);
            }
        });
        rvFileView.setAdapter(fileAdapter);
        editText.addTextChangedListener(textWatcher);
        btChose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putStringArrayListExtra(SELECTPATH, fileAdapter.getSelectData());
                setResult(RESULTCODE, intent);
                finish();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUESCODE && permissions[0].equals(NEED_PERMISSION)) {
            loadDataFrompATH(mPath);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {//点击的是返回键
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {//按键的按下事件
                Log.d("ZYX", mPath);
                if (mPath.trim().equals(Environment.getExternalStorageDirectory().getAbsolutePath())) {
                    return super.onKeyDown(keyCode, event);
                } else {
                    File file = new File(mPath);
                    if (!file.exists()) {
                        return super.onKeyDown(keyCode, event);
                    }
                    mPath = file.getParent();
                    loadDataFrompATH(mPath);
                    return false;
                }
            }


        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}
