package jp.nita.notcamerabut;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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
			if(pressAnotherKeyButton.isChecked()){
				KeyEventSender sender = new KeyEventSender();
				sender.execute(KeyEvent.KEYCODE_VOLUME_DOWN);
				return true;
			}
			if(launchAppButton.isChecked()){
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.addCategory(Intent.CATEGORY_HOME);
				startActivity(intent);
				return true;
			}
			if(!pressAnotherKeyButton.isChecked() && !launchAppButton.isChecked()){
				if(e.getAction()==KeyEvent.ACTION_UP){
					Toast.makeText(this,getString(R.string.camera_key_pressed),Toast.LENGTH_SHORT).show();
				}
				return true;
			}
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
}
