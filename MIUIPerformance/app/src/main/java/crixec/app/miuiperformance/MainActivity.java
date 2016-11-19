package crixec.app.miuiperformance;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by crixec on 16-11-8.
 */
public class MainActivity extends Activity implements View.OnClickListener {

    private final static String TARGET_PROPERTY_KEY = "ro.product.device";
    private final static String TARGET_PROPERTY_VALUE = "virgo";
    private final static String TOKEN = "crixec.miui.performance.device";
    private boolean isOpened = false;
    private Button open;
    private Button close;
    private String tmpStr;
    private TextView digest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        open = (Button) findViewById(R.id.open);
        close = (Button) findViewById(R.id.close);
        digest = (TextView) findViewById(R.id.digest);
        checkIsPerformanceModeOpened();
        close.setOnClickListener(this);
        open.setOnClickListener(this);
    }


    private void checkIsPerformanceModeOpened() {
        if (!getProp(TOKEN).equals("") && getProp(TARGET_PROPERTY_KEY).equals(TARGET_PROPERTY_KEY)) {
            close.setEnabled(true);
            open.setEnabled(false);
        } else {
            close.setEnabled(false);
            open.setEnabled(true);
        }
        String desp = String.format("%s %s\n%s %s", TARGET_PROPERTY_KEY, getProp(TARGET_PROPERTY_KEY), TOKEN, getProp(TOKEN));
        digest.setText("大部分MIUI系统 设置-电量 里面并没有性能模式，性能模式不是效果模式，两者不一样，本工具通过特殊方式达到开启性能模式的办法开启后可以用本工具关闭，操作之后都哟啊重启才能生效，工具需要root" + "\n" + desp);
    }

    private void showToast(CharSequence text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    private boolean setProp(String key, String value) {
        List<String> cmds = new ArrayList<>();
        cmds.add("mount -o remount,rw /system");
        cmds.add(String.format("sed 's/%s/%s/' /system/build.prop", key, value));
        cmds.add("mount -o remount,ro /system");
        return ShellUtils.exec(cmds, null, true) == 0;
    }

    private String getProp(String key) {
        tmpStr = "";
        String cmd = String.format("getprop '%s'", key);
        ShellUtils.exec(cmd, new ShellUtils.Result() {
            @Override
            public void onStdout(String text) {
                tmpStr = text;
                Log.i("MainActivity", text);
            }

            @Override
            public void onStderr(String text) {

            }
        }, false);
        return tmpStr;
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkIsPerformanceModeOpened();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open: {
                String device = getProp(TARGET_PROPERTY_KEY);
                if (setProp(TARGET_PROPERTY_KEY, TARGET_PROPERTY_VALUE) && getProp(TARGET_PROPERTY_KEY).equals(TARGET_PROPERTY_VALUE)) {

                    showToast("开启成功，重启生效");
                    setProp(TOKEN, device);
                } else {
                    showToast("开启失败！");
                    setProp(TOKEN, "");
                }
                break;
            }
            case R.id.close: {
                String device = getProp(TOKEN);
                if (device == null || device.equals("")) {
                    device = getProp(TARGET_PROPERTY_KEY);
                }
                if (setProp(TARGET_PROPERTY_KEY, device) && getProp(TARGET_PROPERTY_KEY).equals(device)) {
                    showToast("关闭成功，重启生效");
                    setProp(TOKEN, "");
                } else {
                    showToast("关闭失败！");
                }
            }
            checkIsPerformanceModeOpened();
        }
    }
}
