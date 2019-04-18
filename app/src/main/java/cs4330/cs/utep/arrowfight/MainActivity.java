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

    /* Constants to requests android features */
    private final int REQUEST_ENABLE_BLUETOOTH = 0;
    private final int REQUEST_DISCOVER_BLUETOOTH = 1;
    private final int REQUEST_DEVICE_LOCATION = 2;
    /* List to storage paired devices */
    private ArrayList<String> listOfDevices;
    private ArrayAdapter<String> listAdapter;
    /* Local device Bluetooth adapter */
    private BluetoothAdapter bluetoothAdapter;
    /* Socket used to transmit and receive information */
    public static BluetoothSocket connectedSocket;
    /* App name and App UUID to open a socket connection */
    public static final String APP_NAME = "ArrowFight";
    public static final UUID APP_UUID = UUID.fromString("7bbc96be-5366-11e9-8647-d663bd873d93");

    /**
     * Initialize activity's layout and initialize bluetooth connection.
     * @param savedInstanceState data from the previews close activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /* Request location permissions */
        requestPermissions();
        /* Initialize list view using array of devices */
        listOfDevices = new ArrayList<>();
        ListView devicesList = findViewById(R.id.devicesList);
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listOfDevices);
        devicesList.setAdapter(listAdapter);
        /* Set action when an element in the list is selected */
        devicesList.setOnItemClickListener((parent, view, position, id) -> {
            /* Get the information of the current device selected */
            String deviceInfo = parent.getItemAtPosition(position).toString();
            /* Get the address of the selected device from the information string */
            String deviceAddress = deviceInfo.substring(deviceInfo.length() - 17);
            /* Initialize a bluetooth device using the selected device address */
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
            /* Initialize a client thread that will initialize a socket using the server connection */
            ClientThread client = new ClientThread(this, device, bluetoothAdapter);
            client.start();
        });
        /* Initialize bluetooth service */
        initializeBluetooth();
    }

    /**
     * This method checks the current android version, and if the version is 22 or
     * newest it requests location access.
     */
    private void requestPermissions(){
        if(Build.VERSION.SDK_INT >= 23) {
            /* Request location permission */
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,},
                    REQUEST_DEVICE_LOCATION);
        }
    }

    /**
     * Turn on bluetooth if it is not enabled.
     */
    private void initializeBluetooth() {
        /* Get the default local device Bluetooth adapter */
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter != null) {
            /* Device supports bluetooth */
            if(!bluetoothAdapter.isEnabled()) {
                /* Start activity to ask the user to enable bluetooth */
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, REQUEST_ENABLE_BLUETOOTH);
            }
        }
        else {
            displayMessage("Your device does not support bluetooth");
        }
    }

    /**
     * BroadcastReceiver used to discover bluetooth devices.
     */
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        /**
         * This method is called when the BroadcastReceiver is receiving an
         * Intent broadcast.
         * @param context The Context in which the receiver is running
         * @param intent The Intent being received
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            /* The intent received is a bluetooth device */
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                /* Get the bluetooth device and add it into the list of devices */
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                listOfDevices.add(device.getName() + "\n" + device.getAddress());
                listAdapter.notifyDataSetChanged();
            }
        }
    };

    /**
     * Method called after an activity started returns with a result.
     * @param requestCode Request code originally supplied to startActivityForResult()
     * @param resultCode Result code returned by the child activity through its setResult()
     * @param data Result data from the caller
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /* If the request code returned is the same used to enable bluetooth */
        if(requestCode == REQUEST_ENABLE_BLUETOOTH) {
            /* Bluetooth is enabled */
            if(resultCode == RESULT_OK) {
                displayMessage("Bluetooth is now enabled");
            }
            /* Bluetooth enabling was cancelled */
            else if(resultCode == RESULT_CANCELED) {
                displayMessage("Failed to initialize bluetooth");
            }
        }
        /* if the request code returned is the same used to make device discoverable */
        else if(requestCode == REQUEST_DISCOVER_BLUETOOTH) {
            /* Device is now discoverable for 120 seconds */
            if(resultCode == 120) {
                displayMessage("Device is now discoverable");
            }
            /* The user did not enabled discoverability */
            else if(resultCode == RESULT_CANCELED){
                displayMessage("Failed to make device discoverable");
            }
        }
    }

    /**
     * This method will be executed when the user presses the button to
     * scan for bluetooth devices.
     * @param view The view that invoke this click listener
     */
    public void scanForDevices(View view) {
        /* Initialize intent filter to find devices and register the receiver */
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        /* start discovering bluetooth devices */
        bluetoothAdapter.startDiscovery();
        displayMessage("Scanning for devices");
    }

    /**
     * This method will be executed when the user presses the button to
     * host a game as a server.
     * @param view The view that invoke this click listener
     */
    public void makeDeviceDiscoverable(View view) {
        /* if the device is not discovering devices, start an activity to enable it */
        if(!bluetoothAdapter.isDiscovering()) {
            Intent startDiscovering = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            startActivityForResult(startDiscovering, REQUEST_DISCOVER_BLUETOOTH);
        }
        /* Initialize a server thread that will initialize listen to client sockets */
        ServerThread server = new ServerThread(this, bluetoothAdapter);
        server.start();
    }

    /**
     * This method will be invoked from the client or server thread once both
     * the server and client threads are connected.
     * @param connectedSocket connected socket to create a connected thread
     */
    @Override
    public void initializeConnectedThread(BluetoothSocket connectedSocket){
        MainActivity.connectedSocket = connectedSocket;
        /* Launch the Game activity with both sockets connected */
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    /**
     * Method called before the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        /* Stop listening from bluetooth devices */
        unregisterReceiver(receiver);
    }

    /**
     * Displays a message to the screen.
     * @param message message that will be displayed
     */
    public void displayMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}