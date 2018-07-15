package com.example.skyworthclub.sqlitedatabase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import SQLite.Database;
import SQLite.OperateDB;
import SQLite.Order;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";

    private EditText inputId;
    private EditText inputCustomName;
    private EditText inputOrderPrice;
    private EditText inputCountry;

    private TextView showSQLMsg;
    private ListView showDateListView;

    private List<Order> orderList;
    private OperateDB mOperateDB;

    private OrderListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOperateDB = new OperateDB(this);
        if (!mOperateDB.isDataExist())
            mOperateDB.initTable();

        initComponent();
        orderList = mOperateDB.getAllData();
        if (orderList != null){
            adapter = new OrderListAdapter(this, orderList);
            showDateListView.setAdapter(adapter);
        }

    }

    private void initComponent(){
        Button insertButton = findViewById(R.id.insertButton);
        Button deleteButton = findViewById(R.id.deleteButton);
        Button updateButton = findViewById(R.id.updateButton);
        Button query1Button = findViewById(R.id.query1Button);
        Button query2Button = findViewById(R.id.query2Button);
        Button query3Button = findViewById(R.id.query3Button);

        SQLBtnOnclickListener onclickListener = new SQLBtnOnclickListener();
        insertButton.setOnClickListener(onclickListener);
        deleteButton.setOnClickListener(onclickListener);
        updateButton.setOnClickListener(onclickListener);
        query1Button.setOnClickListener(onclickListener);
        query2Button.setOnClickListener(onclickListener);
        query3Button.setOnClickListener(onclickListener);

        inputId = findViewById(R.id.inputId);
        inputCustomName = findViewById(R.id.inputCustomName);
        inputOrderPrice = findViewById(R.id.inputOrderPrice);
        inputCountry = findViewById(R.id.inputCountry);

        showSQLMsg = findViewById(R.id.showSQLMsg);
        showDateListView = findViewById(R.id.showDateListView);
        showDateListView.addHeaderView(LayoutInflater.from(this).inflate(R.layout.sql_item, null),
                null, false);
    }

    public class SQLBtnOnclickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {

            String input_Id = inputId.getText().toString().trim();
            String input_CustomName = inputCustomName.getText().toString().trim();
            String input_OrderPrice = inputOrderPrice.getText().toString().trim();
            String input_Country = inputCountry.getText().toString().trim();

            switch (view.getId()){

                case R.id.insertButton:
                    boolean insertRes = mOperateDB.insertData(input_Id, input_CustomName, input_OrderPrice, input_Country);
                    if (insertRes)
                        showSQLMsg.setText("新增一条数据：\ninsert into Orders(Id, CustomName, OrderPrice, Country)" +
                            " \nvalues ("+input_Id+", "+input_CustomName+", "+input_OrderPrice+", "+input_Country+")");
                    else
                        showSQLMsg.setText("错误...");

                    refreshOrderList();
                    break;

                case R.id.deleteButton:
                    int deleteRes = mOperateDB.deleteOrder(input_Id, input_CustomName, input_OrderPrice, input_Country);
                    if (deleteRes>0)
                        showSQLMsg.setText("删除:"+deleteRes+" 条数据：\n删除名为"+input_Id+input_CustomName+input_OrderPrice+input_Country
                                +"的数据\ndelete from Orders where Id/CustomName/OrderPrice/Country... ");
                    else
                        showSQLMsg.setText("错误...");

                    refreshOrderList();
                    break;

                //根据id更新其他项的数据
                case R.id.updateButton:
                    int updateRes = mOperateDB.updateOrder(input_Id, input_CustomName, input_OrderPrice, input_Country);
                    if (updateRes>0)
                        showSQLMsg.setText("修改:"+updateRes+" 条数据：\n修改id="+input_Id
                                +"的数据 \nupdate from Orders where Id/CustomName/OrderPrice/Country..." );
                    else
                        showSQLMsg.setText("错误...");

                    refreshOrderList();
                    break;

                case R.id.query1Button:
                    StringBuilder msg = new StringBuilder();
                    List<Order> orderList1 = mOperateDB.rawQuery(input_Id, input_CustomName, input_OrderPrice, input_Country);

                    if (orderList1 != null){
                        msg.append("数据查询：\n此处将名为"+input_Id+input_CustomName+input_OrderPrice+input_Country
                                +"的信息提取出来\nselect * from Orders where id／CustomName/OrderPrice/Country...");
                        for (Order order : orderList){
                            msg.append("\n(" + order.id + ", " + order.customName + ", " + order.orderPrice + ", " + order.country + ")");
                        }
                    }
                    else
                        msg.append("错误...");

                    showSQLMsg.setText(msg);
                    break;

                case R.id.query2Button:
                    List<Order> orderList2 = mOperateDB.rawQuery(input_Id,input_CustomName, input_OrderPrice,input_Country);
                    if (orderList2 != null) {
                        int count = orderList2.size();
                        showSQLMsg.setText("统计查询：\n此处将名为" + input_Id + input_CustomName + input_OrderPrice + input_Country
                                + "的信息记录数提取出来\n 记录书为：" + count);
                    }
                    else
                        showSQLMsg.setText("错误...");

                    break;

                case R.id.query3Button:
                    StringBuilder msg2 = new StringBuilder();
                    List<Order> maxOrderPrice = mOperateDB.rawQuery("select * from "+Database.TABLE_NAME+" where id in (select max(id) from orders)", null);
                    if (maxOrderPrice != null){
                        msg2.append("比较查询：\n此处查询单笔数据中id最高的");
                        for (Order order : maxOrderPrice){
                            msg2.append("\n(" + order.id + ", " + order.customName + ", " + order.orderPrice + ", " + order.country + ")");
                        }
                    }

                    showSQLMsg.setText(msg2);
                    break;

                default:

                    break;
            }
        }
    }

    private void refreshOrderList(){
        // 注意：千万不要直接赋值，如：orderList = ordersDao.getAllDate() 此时相当于重新分配了一个内存 原先的内存没改变 所以界面不会有变化
        // Java中的类是地址传递 基本数据才是值传递
        orderList.clear();
        orderList.addAll(mOperateDB.getAllData());
        adapter.notifyDataSetChanged();
    }
}
