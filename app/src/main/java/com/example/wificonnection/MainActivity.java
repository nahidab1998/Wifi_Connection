package com.example.wificonnection;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView send , connecte , list_device ;
    private TextView massage ;
    private EditText editText ;
    private ListView listView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initId();
        

    }

    private void initId() {
        send = findViewById(R.id.bt_send);
        connecte = findViewById(R.id.bt_listen);
        list_device = findViewById(R.id.bt_listdevice);
        editText = findViewById(R.id.editxt_message);
        massage = findViewById(R.id.txt_message);
        listView = findViewById(R.id.listview_device);
    }
}