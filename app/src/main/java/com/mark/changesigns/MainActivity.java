package com.mark.changesigns;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity implements OnClickListener,
        OnItemClickListener {
    // private static final String TAG = "MainActivity";
    private static final String DEF_PKG = "jp.colopl.wcat";
    private static final int MSG_INIT_DATAS = 103;

    private static final int MSG_SET_STARS = 100;
    private static final int MSG_BACKUP_SAVE = 102;
    String fromSaveGetNameString = "";
    @ViewInject(R.id.listView)
    private ListView listView;
    @ViewInject(R.id.saveTextView)
    private TextView saveTextView;
    @ViewInject(R.id.pkgEditText)
    private EditText pkgEditText;
    @ViewInject(R.id.save1)
    private EditText save1;
    @ViewInject(R.id.save2)
    private EditText save2;
    @ViewInject(R.id.save3)
    private EditText save3;
    @ViewInject(R.id.save4)
    private EditText save4;
    @ViewInject(R.id.saveLocation)
    private EditText saveLocation;
    @ViewInject(R.id.saveNew)
    private Button saveNew;
    @ViewInject(R.id.accountTextView)
    private TextView accountTextView;
    @ViewInject(R.id.loginButton)
    private Button loginButton;
    private String pkg = DEF_PKG;
    private SharedPreferences mSharedPreferences;
    private XListAdapter mXListAdapter;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case MSG_INIT_DATAS:
                    mXListAdapter.add((String) msg.obj);
                    accounts.add((String) msg.obj);
                    break;


                case MSG_SET_STARS:
                    accountTextView.setText((String) msg.obj);

                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                            activityManager.killBackgroundProcesses(pkg);
                            Utils.doStartApplicationWithPackageName(MainActivity.this, pkg);
                        }
                    }, 1000);
                    break;

                case MSG_BACKUP_SAVE:
                    mXListAdapter.add((String) msg.obj);
                    accounts.add((String) msg.obj);
                    Toast.makeText(MainActivity.this, "备份成功:" + ((String) msg.obj), Toast.LENGTH_LONG)
                            .show();
                    break;


                default:
                    break;
            }
            saveTextView.setText("已备份存档数:" + mXListAdapter.getCount());

        }


    };
    private long exitTime;

    public static void chmod(String permission, String apkpath) {
        try {
            String command = "chmod " + permission + " " + apkpath;
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
           getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        //   getSupportActionBar().setIcon(R.mipmap.ic_launcher);
      //  getSupportActionBar().setLogo();
        try {
            ViewConfiguration mconfig = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(mconfig, false);
            }
        } catch (Exception ex) {
        }


        mSharedPreferences = getSharedPreferences("change_signs",
                Context.MODE_PRIVATE);
        mXListAdapter = new XListAdapter(this);


        pkg = mSharedPreferences.getString("pkg", DEF_PKG);
        String save1 = mSharedPreferences.getString("save1", "");
        String save2 = mSharedPreferences.getString("save2", "");
        String save3 = mSharedPreferences.getString("save3", "");
        String save4 = mSharedPreferences.getString("save4", "");

        String saveLocation = mSharedPreferences.getString("saveLocation", "/mnt/sdcard");


        this.saveLocation.setText(saveLocation);
        this.save1.setText(save1);
        this.save2.setText(save2);
        this.save3.setText(save3);
        this.save4.setText(save4);
        pkgEditText.setText(pkg);


        pkgEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                mSharedPreferences.edit().putString("pkg", s.toString().trim()).commit();


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        this.save1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    return;
                File f = new File(MainActivity.this.save1.getText().toString().trim());


                if (f == null || !f.exists()) {


                    Toast.makeText(MainActivity.this,
                            "存档1不存在存档文件", Toast.LENGTH_LONG).show();
                    return;
                }
                new ExecuteAsRoot().execute();


            }
        });
        this.save1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                mSharedPreferences.edit().putString("save1", s.toString().trim()).commit();
                setFromSaveGetNameString();

            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });
        this.save2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    return;
                File f = new File(MainActivity.this.save2.getText().toString().trim());
                if (f == null || !f.exists()) {
                   MainActivity.this.save2.setError("存档2为空");

                    Toast.makeText(MainActivity.this,
                            "存档2不存在存档文件", Toast.LENGTH_LONG).show();
                    return;
                }


            }
        });

        this.save2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSharedPreferences.edit().putString("save2", s.toString().trim()).commit();
                setFromSaveGetNameString();

            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });
        this.save3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus)
                    return;
                File f = new File(MainActivity.this.save3.getText().toString().trim());
                if (f == null || !f.exists()) {


                    Toast.makeText(MainActivity.this,
                            "存档3不存在存档文件", Toast.LENGTH_LONG).show();
                    return;
                }


            }
        });
        this.save3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setFromSaveGetNameString();
                mSharedPreferences.edit().putString("save3", s.toString().trim()).commit();

            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });
        this.save4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    return;

                File f = new File(MainActivity.this.save4.getText().toString().trim());
                if (f == null || !f.exists()) {


                    Toast.makeText(MainActivity.this,
                            "存档4不存在存档文件", Toast.LENGTH_LONG).show();
                    return;
                }



            }
        });
        this.save4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setFromSaveGetNameString();
                mSharedPreferences.edit().putString("save4", s.toString().trim()).commit();

            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });
        this.saveLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    return;
                File f = new File(MainActivity.this.saveLocation.getText().toString().trim());
                if (f == null || !f.exists()) {


                    Toast.makeText(MainActivity.this,
                            "此目录不存在存档文件", Toast.LENGTH_LONG).show();
                    return;
                }

//                if (!f.canExecute() || !f.canRead() || !f.canWrite()) {
//                    Toast.makeText(MainActivity.this, "操作此存档文件缺少权限", Toast.LENGTH_LONG).show();
//                }

            }
        });
        this.saveLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setFromSaveGetNameString();
                mSharedPreferences.edit().putString("saveLocation", s.toString().trim()).commit();
//                Toast.makeText(MainActivity.this,
//                        "设置备份位置成功", Toast.LENGTH_LONG).show();
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });


        listView.setAdapter(mXListAdapter);
        listView.setOnItemClickListener(this);
        registerForContextMenu(listView);

        accountTextView.setText(mSharedPreferences.getString("account", "？"));

        setFromSaveGetNameString();

        // mXListAdapter.removeAll();

        initSaveDatas(this.saveLocation.getText().toString().trim());
        saveTextView.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        saveNew.setOnClickListener(this);
    }

    private void setFromSaveGetNameString() {
        File file1 = new File(this.save1.getText().toString().trim());
        File file2 = new File(this.save2.getText().toString().trim());
        File file3 = new File(this.save3.getText().toString().trim());
        File file4 = new File(this.save4.getText().toString().trim());
        if (file1 != null && file1.getName().length() > fromSaveGetNameString.length()) {
            fromSaveGetNameString = file1.getName();
        }
        if (file2 != null && file2.getName().length() > fromSaveGetNameString.length()) {
            fromSaveGetNameString = file2.getName();
        }
        if (file3 != null && file3.getName().length() > fromSaveGetNameString.length()) {
            fromSaveGetNameString = file3.getName();
        }
        if (file4 != null && file4.getName().length() > fromSaveGetNameString.length()) {
            fromSaveGetNameString = file4.getName();
        }
    }

    // STOPSHIP: 2016/06/03
    @Override
    public void onClick(View arg0) {


        switch (arg0.getId()) {


            case R.id.saveTextView:
                Toast.makeText(this, "正在刷新存档", Toast.LENGTH_LONG).show();
                mXListAdapter.removeAll();

                initSaveDatas(saveLocation.getText().toString().trim());
                break;

            case R.id.loginButton:
                if (mSharedPreferences.getString("account", "？").contains("？")) {
                    Toast.makeText(this, "帐号不能含有？空格等特殊字符", Toast.LENGTH_LONG).show();
                    return;
                }
                ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                activityManager.killBackgroundProcesses(pkg);
                Utils.doStartApplicationWithPackageName(this, pkg);
                break;


            case R.id.saveNew:

                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("输入备份存档名");
                final EditText editText = new EditText(this);
                editText.setHint("不能使用？空格等特殊符号");

                dialog.setView(editText);
                dialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                final String account = editText.getText()
                                        .toString().trim();

                                if (!account.isEmpty() && !account.equals("？")) {
                                    // 保存到数据库
                                    // TODO

                                    if (hasNameExist(account)) {
                                        Toast.makeText(MainActivity.this,
                                                "备份存档已存在,保存失败", Toast.LENGTH_LONG).show();

                                    } else {

                                        File file1 = new File(save1.getText().toString().trim());
                                        File file2 = new File(save2.getText().toString().trim());
                                        File file3 = new File(save3.getText().toString().trim());
                                        File file4 = new File(save4.getText().toString().trim());
                                        if (file1 != null && file1.exists() && file1.isFile()) {

                                            saveSaveDatas(account  , save1.getText().toString().trim());
                                        } else {
                                            Toast.makeText(MainActivity.this,
                                                    "指定的游戏目录不存在游戏存档", Toast.LENGTH_LONG).show();
                                        }
                                        if (file2 != null && file2.exists() && file2.isFile()) {

                                            saveSaveDatas(account  , save2.getText().toString().trim());
                                        }
                                        if (file3 != null && file3.exists() && file3.isFile()) {

                                            saveSaveDatas(account  , save3.getText().toString().trim());
                                        }
                                        if (file4 != null && file4.exists() && file4.isFile()) {

                                            saveSaveDatas(account  , save4.getText().toString().trim());
                                        }
                                    }

                                } else {
                                    Toast.makeText(MainActivity.this,
                                            "备份存档名含特殊字符,保存失败", Toast.LENGTH_LONG).show();
                                }
                                mHandler.postDelayed(new Runnable() {

                                    @Override
                                    public void run() {

                                        hideInputMethod();
                                    }
                                }, 500);


                            }
                        });
                dialog.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                                mHandler.postDelayed(new Runnable() {

                                    @Override
                                    public void run() {

                                        hideInputMethod();
                                    }
                                }, 500);
                            }
                        });
                dialog.setOnCancelListener(new OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface arg0) {

                        mHandler.postDelayed(new Runnable() {

                            @Override
                            public void run() {

                                hideInputMethod();
                            }
                        }, 500);
                    }
                });
                dialog.show();

                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,
                            long arg3) {


        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("提示");
        dialog.setMessage("设置【" + mXListAdapter.getItem(arg2)
                + "】为当前登录帐号？");
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {


                File file1 = new File(save1.getText().toString().trim());
                File file2 = new File(save2.getText().toString().trim());
                File file3 = new File(save3.getText().toString().trim());
                File file4 = new File(save4.getText().toString().trim());
                if (file1 != null && file1.exists()) {
                    replaceSaveDatas(arg2, save1.getText().toString().trim());

                } else {
                    Toast.makeText(MainActivity.this,
                            "指定的游戏目录不存在游戏存档", Toast.LENGTH_LONG).show();
                }

                if (file2 != null && file2.exists()) {
                    replaceSaveDatas(arg2, save2.getText().toString().trim());

                }
                if (file3 != null && file3.exists()) {
                    replaceSaveDatas(arg2, save3.getText().toString().trim());

                }
                if (file4 != null && file4.exists()) {
                    replaceSaveDatas(arg2, save4.getText().toString().trim());

                }

            }
        });
        dialog.setNegativeButton("取消", null);
        dialog.show();
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.main, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();

        final String account = mXListAdapter.getItem(info.position);
        final List<File> files = new ArrayList<>();
        final File file1 = new File(save1.getText().toString().trim());
        File file2 = new File(save2.getText().toString().trim());
        File file3 = new File(save3.getText().toString().trim());
        File file4 = new File(save4.getText().toString().trim());
        if (file1 == null && !file1.exists()) {
            Toast.makeText(this, "需要指定游戏存档位置1", Toast.LENGTH_LONG).show();

        } else {
            File file = new File(saveLocation.getText().toString().trim() + "/" + account + file1.getName());
            if(file!=null&&file.exists())
            files.add(file);
        }
        if (file2 == null && !file2.exists()) {
            // Toast.makeText(this, "需要指定游戏存档位置2", Toast.LENGTH_LONG).show();


        } else {
            File file = new File(saveLocation.getText().toString().trim() + "/" + account + file2.getName());
            if(file!=null&&file.exists())
            files.add(file);
        }
        if (file3 == null && !file3.exists()) {
            // Toast.makeText(this, "需要指定游戏存档位置3", Toast.LENGTH_LONG).show();

        } else {
            File file = new File(saveLocation.getText().toString().trim() + "/" + account + file3.getName());
            if(file!=null&&file.exists())
            files.add(file);
        }
        if (file4 == null && !file4.exists()) {
            // Toast.makeText(this, "没有需要指定游戏存档位置4", Toast.LENGTH_LONG).show();

        } else {
            File file = new File(saveLocation.getText().toString().trim() + "/" + account + file4.getName());
            if(file!=null&&file.exists())
            files.add(file);
        }


        //this.getFileStreamPath(account + ".xml");

        switch (item.getItemId()) {


            case R.id.delete:
//


                if (files.get(0).exists()) {
                    new AlertDialog.Builder(this)
                            .setTitle("提示")
                            .setMessage("是否删除存档：" + account + " ?")
                            .setPositiveButton("确定",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface arg0,
                                                            int arg1) {
                                            for (File file : files) {

                                                // File f=  new File(saveLocation.getText().toString().trim()+"/"+""+file.getName());
                                                //if (f!=null&&f.exists())
                                                file.delete();
                                            }

                                            mXListAdapter.remove(account);
                                            saveTextView.setText("已备份存档数:"
                                                    + mXListAdapter.getCount());
                                            ToastUtils.make(MainActivity.this,
                                                    "删除成功:" + account);
                                        }
                                    })
                            .setNegativeButton("取消",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface arg0,
                                                            int arg1) {


                                        }
                                    }).show();

                } else {
                    Toast.makeText(this, "文件不存在", Toast.LENGTH_LONG).show();
                }

                return true;
            case R.id.rename:

                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("输入备份存档名");
                final EditText editText = new EditText(this);
                editText.setHint("不能使用？空格等特殊符号");
                editText.setText(account);
                editText.setSelection(editText.length());
                editText.setSelectAllOnFocus(true);
                dialog.setView(editText);
                dialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String newN = editText.getText().toString().trim();
                                if (hasNameExist(newN)) {
                                    Toast.makeText(MainActivity.this,
                                            "备份存档名字重复,保存失败", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                boolean re = false;


                                for (File file : files) {

                                    if (file.exists()) {
                                        File newName = new File(saveLocation.getText().toString().trim() + "/" + newN + file.getName().replace(account,""));
                                      //  file.renameTo();

                                        boolean r = file.renameTo(newName);
                                        if (!re) {
                                            re = r;
                                        }


                                    } else {
                                        Toast.makeText(MainActivity.this,
                                                "存档文件不存在,更改失败", Toast.LENGTH_LONG).show();
                                    }

                                }

                                if (re) {
                                    mXListAdapter.remove(account);
                                    mXListAdapter.add(newN);

                                    accounts.add(newN);
                                    mXListAdapter.notifyDataSetChanged();
                                    ToastUtils.make(MainActivity.this, "修改存档名字成功");

                                }


                            }
                        });
                dialog.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                                mSharedPreferences.edit()
                                        .putString("account", account).commit();
                            }
                        });
                dialog.show();

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void initSaveDatas(final String path) {


        new Thread() {
            @Override
            public void run() {

                super.run();

                File[] files = new File(path).listFiles();

                if (files != null && files.length != 0) {
                    for (int i = 0; i < files.length; i++) {
                        //    fromSaveGetNameString.
                        if (!TextUtils.isEmpty(files[i].getName()) && files[i].isFile()) {

                            if (files[i].getName().contains(fromSaveGetNameString)) {
                                mHandler.obtainMessage(MSG_INIT_DATAS,
                                        files[i].getName().replace(fromSaveGetNameString, "").trim()

                                )
                                        .sendToTarget();

                            }
                        }


                    }

                } else {
                    ToastUtils.make(MainActivity.this, "读取存档失败,文件目录为空或文件目录不含存档");
                }
            }
        }.start();
    }

    private void replaceSaveDatas(final int position, final String save1Path) {

        new Thread(new Runnable() {

            @Override
            public void run() {

                new ExecuteAsRoot().execute();
                try {
                    byte[] buffer = new byte[4 * 1024];
                    File file = new File(save1Path);
                    String save = saveLocation.getText().toString().trim() + "/" + mXListAdapter
                            .getItem(position) + file.getName();


                    FileInputStream is = new FileInputStream(new File(save));

                    file.setExecutable(true);
                    file.delete();
                    FileOutputStream fos = new FileOutputStream(file, false);
                    int len;
                    while ((len = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);

                    }
                    fos.flush();
                    is.close();
                    fos.close();
                    String account = mXListAdapter.getItem(position);
                    mSharedPreferences.edit().putString("account", account)
                            .commit();


                    mHandler.obtainMessage(MSG_SET_STARS, account)
                            .sendToTarget();

                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    ToastUtils.make(MainActivity.this, "文件不存在,设置存档失败");

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    ToastUtils.make(MainActivity.this, "IO异常,设置存档失败");
                }
            }
        }).start();
    }


    //saveLocation
    private void saveSaveDatas(final String account, final String save) {

        new Thread(new Runnable() {

            @Override
            public void run() {

                new ExecuteAsRoot().execute();
                try {// BufferedWriter
                    byte[] buffer = new byte[4 * 1024];

                    File file = new File(save);
                    file.setExecutable(true);
                    file.setWritable(true);
                    file.setReadable(true);
                    if (file.canRead()) {
                        FileInputStream is = new FileInputStream(file);

                        int len;
                        File saveFile = new File(saveLocation.getText().toString().trim() + "/" + account+file.getName());
                        if (saveFile != null && !saveFile.exists())
                            saveFile.createNewFile();
                        FileOutputStream os = new FileOutputStream(saveFile);

//                    FileOutputStream os = MainActivity.this.openFileOutput(
//                            account + ".xml", Context.MODE_PRIVATE);
                        while ((len = is.read(buffer)) != -1) {
                            os.write(buffer, 0, len);
                        }
                        // os.flush();
                        is.close();
                        os.close();
                        if (!TextUtils.isEmpty(fromSaveGetNameString)&&file.getName().contains(fromSaveGetNameString)) {
                            mHandler.obtainMessage(MSG_BACKUP_SAVE, account)
                                    .sendToTarget();
                        }
                    }else{
                        ToastUtils.make(MainActivity.this, "存档文件读取失败");
                    }

                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch
                    // block
                    e.printStackTrace();
                    ToastUtils.make(MainActivity.this, "存档文件不存在,或备份存档位置不可用，备份失败");

                } catch (IOException e) {
                    // TODO Auto-generated catch
                    // block
                    e.printStackTrace();
                    ToastUtils.make(MainActivity.this, "IO异常,备份失败");//add

                }

            }
        }).start();
    }

    private boolean hasNameExist(String name) {


        for (int i = 0; i < mXListAdapter.getCount(); i++) {
            if (mXListAdapter.getItem(i).equals(name)) {
                // Log.e("TAG", name);
                Log.e("TAG", mXListAdapter.getItem(i));
                return true;
            }

        }
        return false;
    }

    private void hideInputMethod() {
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(getCurrentFocus()
                        .getApplicationWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public boolean onTouchEvent(MotionEvent event) {

        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {

                hideInputMethod();
            }
        }, 500);
        return true;
    }


    // ///////////////初始化程序时使用////////////////////////////////////
    public class ExecuteAsRoot extends AExecuteAsRoot {

        @Override
        protected ArrayList<String> getCommandsToExecute() {
            ArrayList<String> list = new ArrayList<String>();
            list.add("add kill-server");
            list.add("chmod 777 " + "data/data/" + pkg + "/shared_prefs/" + pkg
                    + ".xml");
            return list;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.fragment_photo_gallery, menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // pull out the SearchView
            MenuItem searchItem = menu.findItem(R.id.menu_item_search);
            SearchView searchView = (SearchView) searchItem.getActionView();

            // get the data from our searchable.xml as a SearchableInfo
            SearchManager searchManager = (SearchManager)
                    getSystemService(Context.SEARCH_SERVICE);
            ComponentName name = getComponentName();
            SearchableInfo searchInfo = searchManager.getSearchableInfo(name);

            searchView.setSearchableInfo(searchInfo);
            //     mSearchView.setIconifiedByDefault(true);
//                mSearchView.setOnQueryTextListener(this);
            searchView.setIconified(true);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {


                    mXListAdapter.removeAll();

                    for (int i = accounts.size() - 1; i >= 0; i--) {

                        if (accounts.get(i).contains(newText)) {

                            mXListAdapter.add(accounts.get(i));


                        }
                    }

                    return true;
                }
            });

        }
        return super.onCreateOptionsMenu(menu);
    }

    private List<String> accounts = new ArrayList<String>();

    @Override
    @TargetApi(11)
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_item_search:
                onSearchRequested();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

            if ((System.currentTimeMillis() - exitTime) > 2000)  //System.currentTimeMillis()无论何时调用，肯定大于2000
            {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
