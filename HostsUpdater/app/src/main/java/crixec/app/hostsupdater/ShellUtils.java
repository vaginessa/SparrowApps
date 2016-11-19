package crixec.app.hostsupdater;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ShellUtils {
    public static interface Result {
        public void onStdout(String text);

        public void onStderr(String text);

    }

    private static interface Output {
        public void output(String text);
    }

    public static class OutputReader extends Thread {
        private Output output = null;
        private BufferedReader reader = null;
        private boolean isRunning = false;

        public OutputReader(BufferedReader reader, Output output) {
            this.output = output;
            this.reader = reader;
            this.isRunning = true;
        }

        public void close() {
            try {
                reader.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            String line = null;
            while (isRunning) {
                try {
                    line = reader.readLine();
                    if (line != null)
                        output.output(line);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                }
            }
        }

        public void cancel() {
            synchronized (this) {
                isRunning = false;
                this.notifyAll();
            }
        }
    }

    private static int exec(final String sh, final List<String> cmds, final Result result) {
        Process process = null;
        DataOutputStream stdin = null;
        OutputReader stdout = null;
        OutputReader stderr = null;
        int resultCode = -1;

        try {
            process = Runtime.getRuntime().exec(sh);
            stdin = new DataOutputStream(process.getOutputStream());
            stdout = new OutputReader(new BufferedReader(new InputStreamReader(process.getInputStream())),
                    new Output() {
                        @Override
                        public void output(String text) {
                            // TODO Auto-generated method stub
                            if (result != null)
                                result.onStdout(text);
                        }
                    });
            stderr = new OutputReader(new BufferedReader(new InputStreamReader(process.getErrorStream())),
                    new Output() {
                        @Override
                        public void output(String text) {
                            // TODO Auto-generated method stub
                            if (result != null)
                                result.onStderr(text);
                        }
                    });
            stdout.start();
            stderr.start();
            for (String cmd : cmds) {
                stdin.writeBytes(cmd);
                stdin.writeBytes("\n");
                stdin.flush();
            }
            stdin.writeBytes("exit $?\n");
            stdin.flush();
            resultCode = process.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            return resultCode;
        } finally {
            try {
                stdout.cancel();
                stderr.cancel();
                stdin.close();
                stdout.close();
                stderr.close();
            } catch (Exception e) {

            }
        }
    }

    public static int exec(final List<String> cmds, final Result result, final boolean isRoot) {
        String sh = isRoot ? "su" : "sh";
        return exec(sh, cmds, result);
    }

    public static int exec(final String cmd, final Result result, boolean isRoot) {
        List<String> cmds = new ArrayList<String>();
        cmds.add(cmd);
        return exec(cmds, result, isRoot);
    }

}
