package crixec.app.getqqavatar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.AndroidRuntimeException;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends Activity implements View.OnClickListener {

    private static String api = "http://q.qlogo.cn/headimg_dl?dst_uin=<qq_number>&spec=640&img_type=jpg";
    private static String token = "<qq_number>";

    private EditText editText;
    private Button getBtn;
    private Button saveBtn;
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        editText = (EditText) findViewById(R.id.qqnumber);
        getBtn = (Button) findViewById(R.id.get);
        getBtn.setOnClickListener(this);
        saveBtn = (Button) findViewById(R.id.save);
        saveBtn.setOnClickListener(this);
        imageView = (ImageView) findViewById(R.id.imageView);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.get:
                new LoadAvatarTask(imageView).execute(editText.getText().toString());
                break;
            case R.id.save:
                File file = (File) imageView.getTag();
                try {
                    File local = new File(Environment.getExternalStorageDirectory(), file.getName());
                    FileOutputStream fos = new FileOutputStream(local);
                    InputStream is = new FileInputStream(file);
                    writeStream(is, fos);
                    is.close();
                    fos.close();
                    Toast.makeText(getApplicationContext(), "已保存到：" + local.getPath(), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "保存失败，请给权限", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void writeStream(InputStream is, OutputStream os) throws IOException {
        byte[] buf = new byte[4096];
        int c;
        while ((c = is.read(buf)) != -1) {
            os.write(buf, 0, c);
            os.flush();
        }
    }

    class LoadAvatarTask extends AsyncTask<String, Integer, File> {
        ProgressDialog dialog;
        ImageView imageView;

        public LoadAvatarTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected File doInBackground(String... strings) {
            File save = null;
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                URL url = new URL(api.replace(token, strings[0]));
                URLConnection connection = url.openConnection();
                connection.setConnectTimeout(8000);
                connection.connect();
                is = connection.getInputStream();
                save = new File(getFilesDir(), strings[0] + ".jpg");
                save.deleteOnExit();
                fos = new FileOutputStream(save);
                writeStream(is, fos);
            } catch (IOException e) {
                throw new AndroidRuntimeException(e);
            } finally {
                try {
                    is.close();
                    fos.close();
                } catch (Exception e) {

                }
            }
            return save;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setCancelable(false);
            dialog.setMessage("正在获取头像...");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();
        }

        @Override
        protected void onPostExecute(File file) {
            dialog.dismiss();
            if (file == null) {
                saveBtn.setEnabled(false);
                imageView.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_SHORT).show();
            } else {
                imageView.setVisibility(View.VISIBLE);
                saveBtn.setEnabled(true);
                imageView.setImageURI(Uri.parse(file.getPath()));
                imageView.setTag(file);
            }
        }
    }
}
