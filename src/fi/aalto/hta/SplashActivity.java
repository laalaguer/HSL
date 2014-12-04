package fi.aalto.hta;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class SplashActivity extends Activity{
	protected int _splashTime = 3000;
	protected boolean _active = true;
	protected boolean loadMain = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_layout);
		if(savedInstanceState != null && savedInstanceState.containsKey("isStarted")){
			
		}else{
		    Thread splashTread = new Thread() {
		        @Override
		        public void run() {
		            try {
		                int waited = 0;
		                while(_active && (waited < _splashTime)) {
		                    sleep(100);
		                    if(_active) {
		                        waited += 100;
		                    }
		                }
		            } catch(InterruptedException e) {
		                // do nothing
		            } finally {
		            	if(loadMain){
		            		startActivity(new Intent(SplashActivity.this, MainActivity.class));
		            	}
		                finish();
		            }
		        }
		    };
		    splashTread.start();
		}
		
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("isStarted", true);
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	    if (event.getAction() == MotionEvent.ACTION_DOWN) {
	        _active = false;
	    }
	    return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) || keyCode == KeyEvent.KEYCODE_HOME) {
			loadMain = false;
			_active = false;
	    }
		return super.onKeyDown(keyCode, event);
	}
	
}
