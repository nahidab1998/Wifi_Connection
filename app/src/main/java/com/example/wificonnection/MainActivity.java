package com.example.wificonnection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

     TextView  txt_discover , txt_status ;
    private TextView txt_massage ;
    private EditText editText ;
    private ListView listView ;
    private ImageView img_send ;
    private SwitchCompat  txt_off_on ;

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;

    private BroadcastReceiver receiver;
    private IntentFilter intentFilter;

    private List<WifiP2pDevice> peers = new ArrayList<>();
    private String[] deviceNameArray;

    private WifiP2pDevice[] deviceArrey;

    private Socket socket;

    Serverclas serverclas;
    ClientClass clientClass;
    private String massage , status ;
    private WifiManager wifi;
    private MediaPlayer player;

    boolean ishost;
    int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 100;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initId();
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if(wifi.isWifiEnabled()) txt_off_on.setChecked(true);
        exqlistener();

    }



    private void exqlistener() {

        txt_off_on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(txt_off_on.isChecked()){
                    Toast.makeText(MainActivity.this, "turn on", Toast.LENGTH_SHORT).show();
                    wifi.setWifiEnabled(true);


                }else {
                    Toast.makeText(MainActivity.this, "turn off", Toast.LENGTH_SHORT).show();
                    wifi.setWifiEnabled(false);

                }


            }
        });

//        txt_off_on.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//                NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//
//                if (!mWifi.isConnected()) {
//                    // Do whatever
////                    Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
////                    startActivityForResult(intent , 1);
//                    WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//                    wifi.setWifiEnabled(true);
////                    txt_off_on.setText("خاموش");
//
//                }
//                else {
//                    WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//                    wifi.setWifiEnabled(false);
////                    txt_off_on.setText("روشن");
//
//
//
////                    Toast.makeText(MainActivity.this, "وای فای روشن است", Toast.LENGTH_SHORT).show();
////                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
////                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
////                                PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
////                        //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
////
////                    }else{
//////                        getScanningResults();
////                        //do something, permission was previously granted; or legacy device
////                        Toast.makeText(MainActivity.this, "hello", Toast.LENGTH_SHORT).show();
////                    }
//                }
//
//            }
//        });



//        txt_discover.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//                NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//
//                if (mWifi.isConnected()) {
//                    manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
//                        @Override
//                        public void onSuccess() {
//                            txt_status.setText("دستگاه یافت شد");
//                        }
//
//                        @Override
//                        public void onFailure(int i) {
//                            txt_status.setText("دستگاهی یافت نشد ");
//                        }
//                    });
//                }
//                else {
//                    Toast.makeText(MainActivity.this, "وای فای خود را روشن کنید", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final WifiP2pDevice device = deviceArrey[i];
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                manager.connect(channel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        txt_status.setText("متصل شد به : " + device.deviceAddress);
                    }

                    @Override
                    public void onFailure(int i) {
                        txt_status.setText("اتصال برقرار نیست");
                    }
                });
            }
        });

        img_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                massage = editText.getText().toString();
                status = txt_status.getText().toString();

                if(TextUtils.isEmpty(massage)){

                    Toast.makeText(MainActivity.this, "پیغام خود را بنویسید", Toast.LENGTH_SHORT).show();

                }else {
                    if (status =="میزبان" || status == "مشتری"){

                        ExecutorService executor  = Executors.newSingleThreadExecutor();
                        String msg = editText.getText().toString();
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                if (msg != null && ishost)
                                {
                                    serverclas.write(msg.getBytes());

                                }else if (msg != null && !ishost)
                                {

                                    clientClass.write(msg.getBytes());
                                }
                            }
                        });
//                        Toast.makeText(MainActivity.this, "ارسال شد", Toast.LENGTH_SHORT).show();

                    }else {
                        Toast.makeText(MainActivity.this, "اتصال برقرار نیست", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        registerReceiver(receiver , intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }


    public  class Serverclas extends Thread{
        ServerSocket serverSocket;
        private InputStream inputStream;
        private OutputStream outputStream;

        public void write(byte[] bytes)
        {
            try {
                outputStream.write(bytes);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        @Override
        public void run(){
            try {
                serverSocket = new ServerSocket(8888);
                socket = serverSocket.accept();
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();

            }catch (IOException e){
                e.printStackTrace();
            }

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    byte[] buffer = new byte[1024];
                    int bytes;

                    while (socket != null){
                        try {
                            bytes = inputStream.read(buffer);
                            if (bytes > 0){
                                int finalBytes = bytes;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        String tempMSG = new String(buffer , 0 , finalBytes);
                                        txt_massage.setText(tempMSG);
                                        player= MediaPlayer.create(MainActivity.this,R.raw.notification);
                                        player.start();
                                        txt_massage.setTextColor(getResources().getColor(R.color.matn));

                                        editText.setText("");
                                    }
                                });
                            }

                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    public class ClientClass extends Thread{
        String hostAdd;
        private InputStream inputStream;
        private OutputStream outputStream;

        public ClientClass(InetAddress hostAddress){
            hostAdd = hostAddress.getHostAddress();
            socket = new Socket();
        }

        public void write(byte[] bytes)
        {
            try {
                outputStream.write(bytes);
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        @Override
        public void run(){
            try {
                socket.connect(new InetSocketAddress(hostAdd , 8888) , 500);
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            }catch (IOException e){
                e.printStackTrace();
            }

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(new Runnable() {
                @Override
                public void run() {
                   byte[] buffer = new byte[1024];
                   int bytes;

                   while (socket != null){
                       try {
                           bytes = inputStream.read(buffer);
                           if (bytes >0){
                               int finalbytes = bytes;
                               handler.post(new Runnable() {
                                   @Override
                                   public void run() {
                                      String tempMSG = new String(buffer, 0 ,finalbytes);
                                      txt_massage.setText(tempMSG);
                                       txt_massage.setTextColor(getResources().getColor(R.color.matn));
                                       editText.setText("");
                                   }
                               });
                           }
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                   }
                }
            });
        }
    }

    private void initId() {
        img_send = findViewById(R.id.bt_send);
        txt_discover = findViewById(R.id.bt_listen);
        txt_off_on = findViewById(R.id.bt_offon);
        editText = findViewById(R.id.editxt_message);
        txt_massage = findViewById(R.id.txt_message);
        listView = findViewById(R.id.listview_device);
        txt_status = findViewById(R.id.txt_status);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        receiver = new WiFiDirectBroadcastReceiver(manager ,channel , this);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
            if (!wifiP2pDeviceList.equals(peers)){

                peers.clear();
                peers.addAll(wifiP2pDeviceList.getDeviceList());

                deviceNameArray = new String[wifiP2pDeviceList.getDeviceList().size()];
                deviceArrey = new WifiP2pDevice[wifiP2pDeviceList.getDeviceList().size()];

                int index = 0 ;
                for (WifiP2pDevice device : wifiP2pDeviceList.getDeviceList()){
                    deviceNameArray[index] = device.deviceName;
                    deviceArrey[index] = device;
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1 , deviceNameArray);
            listView.setAdapter(adapter);

            if (peers.size() == 0){
                txt_status.setText("دستگاهی یافت نشد");
                return;
            }
        }
    };

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            final InetAddress groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;
            if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner)
            {
                txt_status.setText("میزبان");
                ishost = true;
                serverclas = new Serverclas();
                serverclas.start();
            }else if (wifiP2pInfo.groupFormed){

                txt_status.setText("مشتری");
                ishost = false;
                clientClass = new ClientClass(groupOwnerAddress);
                clientClass.start();
            }
        }
    };

}