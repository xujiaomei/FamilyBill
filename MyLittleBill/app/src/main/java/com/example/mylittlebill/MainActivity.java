package com.example.mylittlebill;

import android.accounts.Account;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private List<CostBean> mCostBeanList;
    private DatabaseHelper mDatabaseHelper;
    private CostListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        mDatabaseHelper = new DatabaseHelper(this);
        mCostBeanList = new ArrayList<>();
        ListView costList = findViewById(R.id.lv_main);
        //数据来源
        initCostData();

        mAdapter = new CostListAdapter(this, mCostBeanList);
        costList.setAdapter(mAdapter);


        ///单击每一个item实现删除功能
        costList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("删除");
                builder.setMessage("是否删除这笔账单?");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDatabaseHelper.deleteOne(mCostBeanList.get(position));
                        mCostBeanList.remove(position);
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("取消",null);
                builder.create().show();
            }
        });
        //点击圆形邮件号时，添加一个新的账单分别到数据库和list中，以对话框的形式
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                View viewDialog = inflater.inflate(R.layout.new_cost_data, null);
                final EditText title = viewDialog.findViewById(R.id.et_cost_title);
                final EditText money = viewDialog.findViewById(R.id.et_cost_money);
                final DatePicker date = viewDialog.findViewById(R.id.dp_cost_date);
                builder.setView(viewDialog);
                builder.setTitle("添加新账单");
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CostBean costBean = new CostBean();
                        costBean.costTitle = title.getText().toString();
                        costBean.costMoney = money.getText().toString();
//                        costBean.costDate = date.getYear() + "-" + (date.getMonth() + 1) + "-" +
//                                date.getDayOfMonth();
                        //储存date时特别要注意，我们后面要根据costDate排序，所以要将其标准化
                        costBean.costDate = date.getYear()+"-";
                        if(date.getMonth()+1<10)   costBean.costDate+="0";
                        costBean.costDate+=(date.getMonth()+1)+"-";
                        if(date.getDayOfMonth()<10)    costBean.costDate+="0";
                        costBean.costDate+=date.getDayOfMonth();

                        //当输入为空时提醒
                        if(Objects.equals(costBean.costTitle, "") || Objects.equals(costBean.costMoney, ""))
                        {
                            Toast.makeText(MainActivity.this,"信息不完整",Toast.LENGTH_SHORT).show();
                            return;
                        }if(costBean.costMoney.length()>4)
                        {
                            Toast.makeText(MainActivity.this,"你有那么多钱吗",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        //加入一个新的账单并实时更新
                        mDatabaseHelper.insertCost(costBean);
                        mCostBeanList.add(costBean);
                        mAdapter.notifyDataSetChanged();
                    }
                });//把这几个从EditText和DATePicker中取出来，存入数据库中
                builder.setNegativeButton("Cancel", null);
                builder.create().show();
            }
        });

    }


  //测试数据
    private void initCostData() {
//        mDatabaseHelper.deleteAllData();
//        for (int i = 0; i < 6; i++) {
//            CostBean costBean = new CostBean();
//            costBean.costTitle =  i + "ahah";
//            costBean.costDate = "12-12";
//            costBean.costMoney = "30";
//            //mCostBeanList.add(costBean);  //int index,CostBean
//            mDatabaseHelper.insertCost(costBean);
//        }
        Cursor cursor = mDatabaseHelper.getAllCostData();
        if (cursor != null){
            while (cursor.moveToNext()){
                CostBean costBean = new CostBean();
                costBean.costTitle = cursor.getString(cursor.getColumnIndex("cost_title"));
                costBean.costDate = cursor.getString(cursor.getColumnIndex("cost_date"));
                costBean.costMoney = cursor.getString(cursor.getColumnIndex("cost_money"));
                mCostBeanList.add(costBean);
            }
            cursor.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_chart) {
            Intent intent = new Intent(MainActivity.this,ChartsActivity.class);
            intent.putExtra("cost_list", (Serializable) mCostBeanList);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //实现再按一次退出程序
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出记账本", Toast.LENGTH_SHORT).show();
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
