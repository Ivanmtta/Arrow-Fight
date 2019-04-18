package cs4330.cs.utep.arrowfight;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements ClientThread.ClientListener, ServerThread.ServerListener {

    private final int REQUEST_ENABLE_BLUETOOTH = 0;
    private final int REQUEST_DISCOVER_BLUETOOTH = 1;
    private final int REQUEST_DEVICE_LOCATION = 2;

    private ArrayList<String> listOfDevices;
    private ArrayAdapter<String> listAdapter;

    private BluetoothAdapter bluetoothAdapter;

    public static BluetoothSocket connectedSocket;
    public static final String APP_NAME = "ArrowFight";
    public static final UUID APP_UUID = UUID.fromString("7bbc96be-5366-11e9-8647-d663bd873d93");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();
        listOfDevices = new ArrayList<>();
        ListView devicesList = findViewById(R.id.devicesList);
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listOfDevices);
        devicesList.setAdapter(listAdapter);
        devicesList.setOnItemClickListener((parent, view, position, id) -> {
            String deviceInfo = parent.getItemAtPosition(position).toString();
            String deviceAddress = deviceInfo.substring(deviceInfo.length() - 17);
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
            ClientThread client = new ClientThread(this, device, bluetoothAdapter);
            client.start();
        });
        initializeBluetooth();
    }

    private void requestPermissions(){
        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,},
                    REQUEST_DEVICE_LOCATION);
        }
    }

    private void initializeBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            /* Device supports bluetooth */
            if (!bluetoothAdapter.isEnabled()) {
                /* Start activity to ask the user to enable bluetooth */
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, REQUEST_ENABLE_BLUETOOTH);
            }
        } else {
            displayMessage("Your device does not support bluetooth");
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            /* Bluetooth is enabled */
            if (resultCode == RESULT_OK) {
                displayMessage("Bluetooth is now enabled");
            }
            /* Bluetooth enabling was cancelled */
            else if (resultCode == RESULT_CANCELED) {
                displayMessage("Failed to initialize bluetooth");
            }
        }
        else if (requestCode == REQUEST_DISCOVER_BLUETOOTH) {
            if(resultCode == 120) {
                displayMessage("Device is now discoverable");
            }
            else if(resultCode == RESULT_CANCELED){
                displayMessage("Failed to make device discoverable");
            }
        }
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                listOfDevices.add(device.getName() + "\n" + device.getAddress());
                listAdapter.notifyDataSetChanged();
            }
        }
    };

    public void scanForDevices(View view) {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        bluetoothAdapter.startDiscovery();
        displayMessage("Scanning for devices");
    }

    public void makeDeviceDiscoverable(View view) {
        if (!bluetoothAdapter.isDiscovering()) {
            Intent startDiscovering = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            startActivityForResult(startDiscovering, REQUEST_DISCOVER_BLUETOOTH);
        }
        ServerThread server = new ServerThread(this, bluetoothAdapter);
        server.start();
    }

    public void initializeConnectedThread(BluetoothSocket connectedSocket){
        MainActivity.connectedSocket = connectedSocket;
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    /**
     * Method called before the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    /**
     * Displays a message to the screen.
     * @param msg message that will be displayed
     */
    public void displayMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}