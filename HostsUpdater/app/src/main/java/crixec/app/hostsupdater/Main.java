package crixec.app.hostsupdater;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class Main extends Activity implements View.OnClickListener {

    private List<Host> hosts = new ArrayList<Host>();
    private ListView listView;
    private String SYSTEM_HOSTS = "/system/etc/hosts";
    private String LOCAL_HOSTS = "";
    private String BACKUP_HOSTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.hosts_listview);
        findViewById(R.id.download).setOnClickListener(this);
        findViewById(R.id.restore).setOnClickListener(this);
        addHostList("AdAway 的广告屏蔽 hosts", "https://adaway.org/hosts.txt", true);
        addHostList("Dan Pollock 的广告屏蔽 hosts", "http://someonewhocares.org/hosts/hosts", true);
        addHostList("Peter Lowe 的广告屏蔽 hosts", "https://hosts-file.net/ad_servers.txt", true);
        addHostList("MVPs.org 的广告屏蔽 hosts", "http://winhelp2002.mvps.org/hosts.txt", true);
        addHostList("个人收集部分视频软件广告屏蔽 hosts", "https://raw.githubusercontent.com/Crixec/China-Video-Player-AD-Hosts/master/China-video-player-ad-hosts", false);
        addHostList("世界那么大，我想去看看", "https://coding.net/u/scaffrey/p/hosts/git/raw/master/hosts", false);
        String[] sources = new String[hosts.size()];
        for (int i = 0; i < hosts.size(); i++) {
            sources[i] = hosts.get(i).getHostName();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_multiple_choice, sources);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        for (int i = 0; i < hosts.size(); i++) {
            listView.setItemChecked(i, hosts.get(i).isRecommand());
        }
        LOCAL_HOSTS = new File(getFilesDir(), "hosts.txt").getPath();
        BACKUP_HOSTS = new File(getFilesDir(), "system-backup-hosts.txt").getPath();
    }

    private void addHostList(String hostName, String hostUrl, boolean isRecommand) {
        hosts.add(new Host(hostName, hostUrl, isRecommand));
    }

    @Override
    protected void onResume() {
        super.onResume();
        findViewById(R.id.restore).setEnabled(new File(BACKUP_HOSTS).exists());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.download: {
                final List<String> checked = new ArrayList<>();
                final List<String> downloaded = new ArrayList<>();
                for (int i = 0; i < hosts.size(); i++) {
                    if (listView.isItemChecked(i)) {
                        checked.add(hosts.get(i).getHostUrl());
                    }
                }
                final ProgressDialog dialog = new ProgressDialog(this);
                dialog.setCancelable(false);
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.setMax(checked.size());
                dialog.setTitle("正在下载最新hosts");
                dialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        for (int i = 0; i < checked.size(); i++) {
                            dialog.setProgress(i + 1);
                            try {
                                File file = new File(getFilesDir(), i + "_hosts.txt");
                                downloaded.add(file.getPath());
                                download(checked.get(i), file.getPath());
                            } catch (IOException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                });
                                return;
                            }
                        }
                        mergeFiles(LOCAL_HOSTS, downloaded);
                        List<String> cmds = new ArrayList<>();
                        cmds.add("mount -o remount,rw /system");
                        if (!new File(BACKUP_HOSTS).exists()) {
                            cmds.add(String.format("cp -f '%s' '%s'", SYSTEM_HOSTS, BACKUP_HOSTS));
                        }
                        cmds.add(String.format("rm '%s'", SYSTEM_HOSTS));
                        cmds.add(String.format("cp -f '%s' '%s'", LOCAL_HOSTS, SYSTEM_HOSTS));
                        cmds.add(String.format("chmod 0644 '%s'", SYSTEM_HOSTS));
                        cmds.add("mount -o remount,ro /system");
                        ShellUtils.exec(cmds, null, true);
                        final boolean success = new File(LOCAL_HOSTS).length() == new File(SYSTEM_HOSTS).length();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (success)
                                    Toast.makeText(getApplicationContext(), "安装成功", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(getApplicationContext(), "安装失败", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                    }
                }).start();
                break;
            }
            case R.id.restore: {
                List<String> cmds = new ArrayList<>();
                cmds.add("mount -o remount,rw /system");
                if (new File(BACKUP_HOSTS).exists()) {
                    cmds.add(String.format("cp -f '%s' '%s'", BACKUP_HOSTS, SYSTEM_HOSTS));
                }
                cmds.add(String.format("chmod 0644 '%s'", SYSTEM_HOSTS));
                cmds.add("mount -o remount,ro /system");
                ShellUtils.exec(cmds, null, true);
                Toast.makeText(getApplicationContext(), "已还原", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }

    public static void download(String urlStr, String fileName) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        InputStream inputStream = conn.getInputStream();
        byte[] getData = readInputStream(inputStream);
        File file = new File(fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if (fos != null) {
            fos.close();
        }
        if (inputStream != null) {
            inputStream.close();
        }
    }

    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    public static void mergeFiles(String outFile, List<String> files) {
        FileChannel outChannel = null;
        try {
            outChannel = new FileOutputStream(outFile).getChannel();
            for (String f : files) {
                FileChannel fc = new FileInputStream(f).getChannel();
                ByteBuffer bb = ByteBuffer.allocate(1024 * 8);
                while (fc.read(bb) != -1) {
                    bb.flip();
                    outChannel.write(bb);
                    bb.clear();
                }
                fc.close();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (outChannel != null) {
                    outChannel.close();
                }
            } catch (IOException ignore) {
            }
        }
    }

}
