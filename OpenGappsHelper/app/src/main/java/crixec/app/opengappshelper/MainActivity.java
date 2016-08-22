package crixec.app.opengappshelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity {

    Spinner arch;
    Spinner api;
    Spinner variant;

    private class Device {
        String arch;
        String api;
        String variant;

        public String getArch() {
            return arch;
        }

        public void setArch(String arch) {
            this.arch = arch;
        }

        public String getApi() {
            return api;
        }

        public void setApi(String api) {
            this.api = api;
        }

        public String getVariant() {
            return variant;
        }

        public void setVariant(String variant) {
            this.variant = variant;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arch = (Spinner) findViewById(R.id.arch);
        api = (Spinner) findViewById(R.id.api);
        variant = (Spinner) findViewById(R.id.variant);
        final Device device = new Device();
        arch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                device.setArch((String) arch.getAdapter().getItem(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        api.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                device.setApi((String) api.getAdapter().getItem(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        variant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                device.setVariant((String) variant.getAdapter().getItem(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        findViewById(R.id.download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new QueryTask(device.getArch(), device.getApi(), device.getVariant()).execute();
            }
        });
    }

    private class Progress {
        long max;
        long current;
        String filename;

        public long getMax() {
            return max;
        }

        public void setMax(long max) {
            this.max = max;
        }

        public long getCurrent() {
            return current;
        }

        public void setCurrent(long current) {
            this.current = current;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }
    }

    class QueryTask extends AsyncTask<Void, Progress, File> {
        String arch;
        String api;
        String variant;
        ProgressDialog dialog;

        public QueryTask(String arch, String api, String variant) {
            this.arch = arch;
            this.api = api;
            this.variant = variant;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setCancelable(false);
            dialog.setTitle("下载中");
            dialog.setMessage("正在连接文件...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            dialog.dismiss();
            if (file != null) {
                Toast.makeText(getApplicationContext(), "已保存到文件：" + file.getPath(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onProgressUpdate(Progress... values) {
            super.onProgressUpdate(values);
            dialog.setMessage(values[0].getFilename());
            dialog.setMax((int) values[0].getMax());
            dialog.setProgress((int) values[0].getCurrent());
        }

        @Override
        protected File doInBackground(Void... voids) {
            final String url = OpenGappsHelper.obtainDownloadUrl(arch, api, variant);
            String filename = url.substring(url.lastIndexOf("/") + 1);
            File file = new File(Environment.getExternalStorageDirectory(), filename);
            Progress progress = new Progress();
            progress.setFilename(filename);
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            try {
                HttpResponse response = httpClient.execute(httpGet);
                if (response.getStatusLine().getStatusCode() != 200) return null;
                InputStream is = response.getEntity().getContent();
                byte[] buffer = new byte[4096];
                int c = -1;
                FileOutputStream fos = new FileOutputStream(file);
                progress.setMax(response.getEntity().getContentLength());
                long count = 0;
                while ((c = is.read(buffer)) != -1) {
                    count += c;
                    fos.write(buffer, 0, c);
                    fos.flush();
                    progress.setCurrent(count);
                    publishProgress(progress);
                }
                is.close();
                fos.close();
                return file;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
