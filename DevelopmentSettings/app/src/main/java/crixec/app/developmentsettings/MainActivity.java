package crixec.app.developmentsettings;

import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		Intent intent = new Intent();
		intent.setClassName("com.android.settings", "com.android.settings.DevelopmentSettings");
		startActivity(intent);
		finish();
    }
}
