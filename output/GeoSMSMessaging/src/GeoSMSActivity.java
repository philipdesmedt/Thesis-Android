package be.pds.sms;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class GeoSMSActivity extends Activity {

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        setupmySms();
        setupmyGeo();
    }

	
	private EditText editPhoneNr;
	private EditText editMessage;
	private Button sendMessage;
	
	private void setupmySms() {    
	    editPhoneNr = (EditText) findViewById(R.id.txtPhoneNo);
	    editMessage = (EditText) findViewById(R.id.txtMessage);
	    sendMessage = (Button) findViewById(R.id.btnSendSMS);
	    
		    sendMessage.setOnClickListener(new View.OnClickListener() {
	
				@Override
				public void onClick(View v) {
					String phoneNo = editPhoneNr.getText().toString();
					String message = editMessage.getText().toString();
					if (phoneNo.length() > 0 && message.length() > 0) {
						sendSMS(phoneNo, message);
					} else {
						Toast.makeText(getBaseContext(), "Please enter both phone number and message.",
								Toast.LENGTH_SHORT).show();
					}
				}
		    });
	}
	
	private void sendSMS(String phoneNumber, String message) {
		String SENT = "SMS_SENT";
		String DELIVERED = "SMS_DELIVERED";
		
		PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
				new Intent(SENT), 0);
		PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
				new Intent(DELIVERED), 0);
		
		//---when the SMS has been sent---
		registerReceiver(new BroadcastReceiver() {
	
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
					case Activity.RESULT_OK:
						Toast.makeText(getBaseContext(), "SMS sent",
								Toast.LENGTH_SHORT).show();
						break;
					case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
						Toast.makeText(getBaseContext(), "Generic failure", 
								Toast.LENGTH_SHORT).show();
					    break;
					case SmsManager.RESULT_ERROR_NO_SERVICE:
						Toast.makeText(getBaseContext(), "No service", 
					    		Toast.LENGTH_SHORT).show();
					    break;
					case SmsManager.RESULT_ERROR_NULL_PDU:
						Toast.makeText(getBaseContext(), "Null PDU", 
								Toast.LENGTH_SHORT).show();
					    break;
					case SmsManager.RESULT_ERROR_RADIO_OFF:
						Toast.makeText(getBaseContext(), "Radio off", 
								Toast.LENGTH_SHORT).show();
					    break;
					default:
						Toast.makeText(getBaseContext(), "Fail.", 
								Toast.LENGTH_SHORT).show();
						break;
				}
			}
			
		}, new IntentFilter(SENT));
		
		//---when the SMS has been delivered---
		registerReceiver(new BroadcastReceiver() {
	
			@Override
			public void onReceive(Context context, Intent intent) {
				switch (getResultCode()) {
					case Activity.RESULT_OK:
						Toast.makeText(getBaseContext(), "SMS delivered",
								Toast.LENGTH_SHORT).show();
						break;
					case Activity.RESULT_CANCELED:
						Toast.makeText(getBaseContext(), "SMS not delivered",
								Toast.LENGTH_SHORT).show();
						break;
				}
			}
			
		}, new IntentFilter(DELIVERED));
		
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
	
	}
	
	
	private void setupmyGeo() {
	
		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
	
			@Override
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location provider.
				useNewLocation(location);
			}
	
			@Override
			public void onProviderDisabled(String provider) { 
			}
	
			@Override
			public void onProviderEnabled(String provider) {
			}
	
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
			}
		};
		
		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		// the second is the minimum time interval between notifications
		// the third is the minimum change in distance between notifications
	}
	
	private void useNewLocation(Location loc) {
		editMessage.setText(loc.toString());
	    editMessage.invalidate();
	
	
	}
	
}