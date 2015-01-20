package jp.nita.notcamerabut;

import java.lang.reflect.Method;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button press=(Button)findViewById(R.id.press);
		press.setOnClickListener(this);
		Button startActivity=(Button)findViewById(R.id.start_activity);
		startActivity.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		RadioButton pressAnotherKeyButton = (RadioButton)(findViewById(R.id.radioButton2));
		RadioButton launchAppButton = (RadioButton)(findViewById(R.id.radioButton3));

		String eventAction="";
		if(e.getAction()==KeyEvent.ACTION_DOWN){
			eventAction="Down";
		}else if(e.getAction()==KeyEvent.ACTION_UP){
			eventAction="Up";
		}else if(e.getAction()==KeyEvent.ACTION_MULTIPLE){
			eventAction="...";
		}
		String message = ""+e.getKeyCode()+" "+eventAction;
		((TextView)findViewById(R.id.message)).setText(message);

		if(e.getKeyCode()==KeyEvent.KEYCODE_CAMERA){
			if(e.getAction()==KeyEvent.ACTION_UP){
				if(pressAnotherKeyButton.isChecked()){
					KeyEventSender sender = new KeyEventSender();
					sender.execute(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
					return true;
				}
				if(launchAppButton.isChecked()){
					Intent intent = new Intent(Intent.ACTION_MAIN);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.addCategory(Intent.CATEGORY_HOME);
					startActivity(intent);
					return true;
				}
			}else{
				return true;
			}
		}
		if(e.getKeyCode()==KeyEvent.KEYCODE_VOLUME_UP){
			if(e.getAction()==KeyEvent.ACTION_UP){
				if(pressAnotherKeyButton.isChecked()){
					KeyEventSender sender = new KeyEventSender();
					sender.execute(KeyEvent.KEYCODE_MEDIA_NEXT);
					return true;
				}
			}
			return true;
		}
		if(e.getKeyCode()==KeyEvent.KEYCODE_VOLUME_DOWN){
			if(e.getAction()==KeyEvent.ACTION_UP){
				if(pressAnotherKeyButton.isChecked()){
					KeyEventSender sender = new KeyEventSender();
					sender.execute(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
					return true;
				}
			}
			return true;
		}

		return super.dispatchKeyEvent(e);
	}

	private class KeyEventSender extends AsyncTask<Integer, Object, Object> {
		@Override
		protected Object doInBackground(Integer... params) {
			int keycode = (Integer)(params[0]);
			Instrumentation ist = new Instrumentation();
			ist.sendKeyDownUpSync(keycode);
			return null;
		}
	}

	@Override
	public void onClick(View v) {
		if(v==findViewById(R.id.press)){
			int keyCode=0;
			try{
				keyCode=Integer.parseInt(((EditText)findViewById(R.id.editText1)).getText().toString());
			}catch(Exception ignore){

			}
			KeyEventSender sender = new KeyEventSender();
			sender.execute(keyCode);
		}else if(v==findViewById(R.id.start_activity)){
			try {
				Class<?> serviceManagerClass = Class.forName("android.os.ServiceManager");
				Method getService = serviceManagerClass.getMethod("getService", String.class);
				IBinder retbinder = (IBinder) getService.invoke(serviceManagerClass, "statusbar");
				Class<?> statusBarClass = Class.forName(retbinder.getInterfaceDescriptor());
				Object statusBarObject = statusBarClass.getClasses()[0].getMethod("asInterface", IBinder.class).invoke(null, new Object[] { retbinder });
				if (android.os.Build.VERSION.SDK_INT >= 16) { // android.os.Build.VERSION_CODES.JELLY_BEAN
					Method preloadRecentApps = statusBarClass.getMethod("preloadRecentApps");
					preloadRecentApps.setAccessible(true);
					preloadRecentApps.invoke(statusBarObject);
				}
				Method toggleRecentApps = statusBarClass.getMethod("toggleRecentApps");
				toggleRecentApps.setAccessible(true);
				toggleRecentApps.invoke(statusBarObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
