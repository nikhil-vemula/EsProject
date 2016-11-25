package vsnick.jasmine.esproject;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.renderscript.Double2;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.Manifest;

public class MainActivity extends Activity implements OnMapReadyCallback {
    Button btnSendSMS;
    IntentFilter intentFilter;
    private GoogleMap mMap;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    private static final String phoneNo="+919985075565";
    private static final String message="es";
    private ProgressBar spinner;
    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            spinner.setVisibility(View.GONE);
            TextView SMSes = (TextView) findViewById(R.id.textView1);
            SMSes.setText("Location:"+intent.getExtras().getString("sms"));
            Double lat = Double.parseDouble(intent.getExtras().getString("lat"));
            Double log = Double.parseDouble(intent.getExtras().getString("log"));
            LatLng sydney = new LatLng(lat,log);
            mMap.addMarker(new MarkerOptions().position(sydney).title("Bus"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,18.0f));
        }
    };
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //---intent to filter for SMS messages received--
        intentFilter = new IntentFilter();
        intentFilter.addAction("SMS_RECEIVED_ACTION");
        spinner= (ProgressBar) findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);
        btnSendSMS = (Button) findViewById(R.id.btnSendSMS);
        btnSendSMS.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                //sendSMS("+919000466890", "{\"lat\":17.475195,\"log\":78.386600}");
                sendSMS();
                spinner.setVisibility(View.VISIBLE);
            }
        });
    }
    @Override
    protected void onResume() {
        //---register the receiver--
        registerReceiver(intentReceiver, intentFilter);
        super.onResume();
    }
    @Override
    protected void onPause() {
        //---unregister the receiver--
        unregisterReceiver(intentReceiver);
        super.onPause();
    }
    //---sends an SMS message to another device--
    protected void sendSMS() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
        else
        {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(getApplicationContext(),"SMS Sent",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, message, null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}
