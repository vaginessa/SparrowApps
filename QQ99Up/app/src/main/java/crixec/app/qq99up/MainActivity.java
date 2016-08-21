package crixec.app.qq99up;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static String[] api = {
            "http://api.52beizhi.cn/quan/index.php?hm=<qq_number>",
            "http://www.xkw520.top/index.php?hm=<qq_number>",
            "http://la.vvoso.com/?qq=<qq_number>"
    };
    private static String token = "<qq_number>";

    private EditText editText;
    private Button btn;
    private Spinner spinner;
    private String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        editText = (EditText) findViewById(R.id.qqnumber);
        btn = (Button) findViewById(R.id.invoke);
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                url = api[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flash(editText.getText().toString());
            }
        });
    }

    public void flash(String number) {
        if (number.length() < 5) return;
        final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        dialog.setCancelable(false);
        dialog.setMessage("正在给：" + number + " 拉圈圈");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setTitle("最后的进度会小卡哦");
        dialog.show();
        final WebView webView = new WebView(this);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                dialog.setProgress(newProgress);
                if (newProgress == 100) {
                    dialog.cancel();
                    webView.stopLoading();
                    Toast.makeText(getApplicationContext(), "拉完了，过几分钟再去查看", Toast.LENGTH_SHORT).show();
                }
            }
        });
        webView.loadUrl(url.replace(token, number) + number);
    }
}
