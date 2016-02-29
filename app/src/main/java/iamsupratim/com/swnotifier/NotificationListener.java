package iamsupratim.com.swnotifier;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

import java.util.UUID;

/**
 * Created by borax12 on 29/02/16.
 */
public class NotificationListener extends NotificationListenerService implements BluetoothAdapter.LeScanCallback {


    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt connectedGatt;
    private BluetoothDevice selectedDevice;
    public static final UUID SMARTBAND_CHARACTERISTIC = UUID.fromString("0000fff6-0000-1000-8000-00805f9b34fb");
    public static final UUID PEDOMETER_SERVICE_UUID = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);

        Log.d("SWNOTIFIER", "Notification posted" + sbn.getPackageName());

        if(sbn.getPackageName().equals("com.whatsapp")
                ||sbn.getPackageName().equals("com.google.android.apps.inbox")
                ||sbn.getPackageName().equals("com.Slack")
                ||sbn.getPackageName().equals("com.twitter.android")){

            if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {

                Toast.makeText(this, "NO LE Support.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {

                Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                enableBTIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(enableBTIntent);

            }

            BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);

            bluetoothAdapter = manager.getAdapter();


            if (selectedDevice == null) {
                startScan();
            } else {
                vibrateBand();
            }

        }


    }

    private void startScan() {

        bluetoothAdapter.startLeScan(this);

    }


    private void vibrateBand() {


        connectedGatt = selectedDevice.connectGatt(this, false, mGattCallback);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }


    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        Log.i("SWNOTIFIER", "New LE Device: " + device.getName() + " @ " + rssi);

        if (device.getName()!=null
                &&device.getName().contains("GOQii")) {
            selectedDevice = device;
            vibrateBand();
        }

    }


    private android.bluetooth.BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            Log.d("SWNOTIFIER", "Conntection State Change : " + status);

            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {


                gatt.discoverServices();

            } else if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_DISCONNECTED) {


            } else if (status != BluetoothGatt.GATT_SUCCESS) {

                gatt.disconnect();

            }


        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {

            Log.d("SWNOTIFIER", "Services Discovered: " + status);

            BluetoothGattCharacteristic characteristic;

            characteristic = gatt.getService(PEDOMETER_SERVICE_UUID).getCharacteristic(SMARTBAND_CHARACTERISTIC);
            if (characteristic == null) {

                Log.d("SWNOTIFIER", "Characteristic not found ");

            } else {

                characteristic.setValue(getVibrationDataByte());
                gatt.writeCharacteristic(characteristic);
            }

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }
    };

    private byte[] getVibrationDataByte() {
        byte abyte0[] = new byte[16];
        abyte0[0] = (byte) 54;
        abyte0[1] = (byte) 5;
        abyte0[2] = (byte) 0;
        abyte0[3] = (byte) 0;
        abyte0[4] = (byte) 0;
        abyte0[5] = (byte) 0;
        abyte0[6] = (byte) 0;
        abyte0[7] = (byte) 0;
        abyte0[8] = (byte) 0;
        abyte0[9] = (byte) 0;
        abyte0[10] = (byte) 0;
        abyte0[11] = (byte) 0;
        abyte0[12] = (byte) 0;
        abyte0[13] = (byte) 0;
        abyte0[14] = (byte) 0;
        int i = 0;
        do {
            while (i >= 15) {
                try {
                    Log.e("SWNOTIFIER", (new StringBuilder()).append("cmd: cmdBytes[15]:").append(abyte0[15]).toString());
                } catch (Exception exception) {
                    return abyte0;
                }
                return abyte0;
            }
            abyte0[15] = (byte) (abyte0[15] + abyte0[i]);
            i++;
        } while (true);
    }
}
