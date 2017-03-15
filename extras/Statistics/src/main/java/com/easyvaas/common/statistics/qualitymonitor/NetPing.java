package com.easyvaas.common.statistics.qualitymonitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import com.easyvaas.common.statistics.utils.StatisticsLogger;

class NetPing {
    private static final String TAG = NetPing.class.getSimpleName();

    private static final String MATCH_PING_IP = "(?<=from ).*(?=: icmp_seq=1 ttl=)";

    private NetPingListener listener;
    private int pingCount;
    private PingResultEntity result;

    public NetPing(NetPingListener listener, int count) {
        this.listener = listener;
        this.pingCount = count;
    }

    public interface NetPingListener {
        void OnNetPingFinished(String log, PingResultEntity result);
    }

    public void exec(String ip) {
        result = new PingResultEntity(ip, 0, 0, 0);
        StringBuilder log = new StringBuilder(256);
        String pingStatus = execPing(ip);
        if (Pattern.compile(MATCH_PING_IP).matcher(pingStatus).find()) {
            StatisticsLogger.i(TAG, "status" + pingStatus);
            log.append("\t" + pingStatus);
        } else {
            if (pingStatus.length() == 0) {
                log.append("ping: cannot resolve " + ip + ": Unknown host");
            } else {

                log.append("ping: cannot resolve " + ip + ": Timeout");
            }
        }

        this.listener.OnNetPingFinished(log.toString(), result);
    }

    private String execPing(String ip) {
        String cmd = "ping -c ";
        Process process = null;
        String str = "";
        BufferedReader reader = null;
        try {
            process = Runtime.getRuntime().exec(
                    cmd + this.pingCount + " -i 0.2 " + ip);
            reader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                str += line;
                parsePingOut(line);
            }
            reader.close();
            process.waitFor();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }

        return str;
    }

    private void parsePingOut(String out) {
        if (out.contains("packet loss")) {
            int percentIndex = out.indexOf("% packet loss");
            out = out.substring(0, percentIndex);

            int lastIndex = out.lastIndexOf(",");
            out = out.substring(lastIndex);
            out = out.replace(",", "");
            out = out.replace(" ", "");

            float loss_rate = Float.valueOf(out);

            result.setLoss_rate(loss_rate);
        } else if (out.contains("min/avg/max/mdev")) {
            int equalIndex = out.indexOf("=");
            int msIndex = out.indexOf("ms");
            out = out.substring(equalIndex, msIndex);
            out = out.replace("=", "");
            out = out.replace(" ", "");

            String timeArray[] = out.split("/");

            if (timeArray.length == 4) {
                float avg = Float.valueOf(timeArray[1]);
                float mdev = Float.valueOf(timeArray[3]);

                result.setRtt_avg(avg);
                result.setRtt_std_dev(mdev);
            }
        }
    }
}
