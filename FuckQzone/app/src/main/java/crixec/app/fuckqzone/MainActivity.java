package crixec.app.fuckqzone;
import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Spinner;
import android.view.View.OnClickListener;
import android.view.View;
import android.text.TextWatcher;
import android.text.Editable;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.Adapter;
import android.text.ClipboardManager;
import android.widget.Toast;


public class MainActivity extends Activity implements OnClickListener
{

	@Override
	public void onClick(View p1)
	{
		// TODO: Implement this method
		switch(p1.getId()){
			case R.id.generate:
				result.setText(getResult());
				break;
			case R.id.copy:
				ClipboardManager clip = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
				clip.setText(result.getText());
				Toast.makeText(getApplicationContext(), "复制成功", Toast.LENGTH_SHORT).show();
				break;
		}
	}
	private CharSequence getResult()
	{
		// TODO: Implement this method
		switch(spinner.getSelectedItemPosition()){
			case 0:
				return String.format("{uin:8888,nick:%s,who:1}", content.getText().toString());
			case 1:
				return String.format("%s[em]e10004[/em]{uin:123,nick:Windows}", content.getText().toString());
			case 2:
				return String.format("%s[em]e10002[/em]{uin:123,nick:iPhone}", content.getText().toString());
			case 3:
				return String.format("%s[em]e10004[/em]{uin:123,nick:iPad}", content.getText().toString());
			case 4:
				return String.format("%s[em]e10002[/em]{uin:123,nick:%s}", content.getText().toString(), model.getText().toString());
		}
		return "";
	}
	
	private EditText content;
	private EditText result;
	private Button generate;
	private Button copy;
	private Spinner spinner;
	private EditText model;
	private String[] type = {"说说蓝色字体", "仿Windows", "仿iPhone", "仿iPad", "自定义机型"};
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		spinner = (Spinner) findViewById(R.id.spinner);
		content = (EditText) findViewById(R.id.content);
		result = (EditText) findViewById(R.id.result);
		generate = (Button) findViewById(R.id.generate);
		copy = (Button) findViewById(R.id.copy);
		model = (EditText) findViewById(R.id.model);
		
		generate.setOnClickListener(this);
		copy.setOnClickListener(this);
		MyTextWatcher textWatcher = new MyTextWatcher();
		content.addTextChangedListener(textWatcher);
		result.addTextChangedListener(textWatcher);
		generate.setEnabled(false);
		copy.setEnabled(false);
		spinner.setAdapter(getAdapter());
		spinner.setOnItemSelectedListener(new OnItemSelectedListener(){

				@Override
				public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4)
				{
					// TODO: Implement this method
					if(p3 == 4){
						model.setVisibility(View.VISIBLE);
					}else{
						model.setVisibility(View.GONE);
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> p1)
				{
					// TODO: Implement this method
				}
			});
		
	}
	public ArrayAdapter getAdapter(){
		return new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, type);
	}
	class MyTextWatcher implements TextWatcher
	{

		@Override
		public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
		{
			// TODO: Implement this method
		}

		@Override
		public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
		{
			// TODO: Implement this method
			if(content.getText().length() < 1){
				generate.setEnabled(false);
			}else{
				generate.setEnabled(true);
			}
			if(result.getText().length() < 1){
				copy.setEnabled(false);
			}else{
				copy.setEnabled(true);
			}
		}

		@Override
		public void afterTextChanged(Editable p1)
		{
			// TODO: Implement this method
		}
		
		
	}
}
