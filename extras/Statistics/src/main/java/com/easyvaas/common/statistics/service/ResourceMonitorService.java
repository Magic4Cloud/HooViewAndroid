package com.easyvaas.common.statistics.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Debug;
import android.os.IBinder;

public class ResourceMonitorService extends Service {
    private static final String TAG = ResourceMonitorService.class.getSimpleName();

    private int memTotal, pId;
    private int intervalRead = 1000;
    private int intervalUpdate;
    private int intervalWidth;
    private int maxSamples = 2000;
    private boolean firstRead = true;
    private long workT, totalT, workAMT, total, totalBefore, work, workBefore, workAM, workAMBefore;

    private BufferedReader reader;
    private String s;
    private String[] sa;
    private List<String> memUsed, memAvailable, memFree, cached, threshold;
    private List<Float> cpuTotal, cpuAM;
    private List<Integer> memoryAM;

    private ActivityManager am;
    private ActivityManager.MemoryInfo mi;
    private Debug.MemoryInfo[] amMI;

    public void setIntervals(int intervalRead, int intervalUpdate, int intervalWidth) {
        this.intervalRead = intervalRead;
        this.intervalUpdate = intervalUpdate;
        this.intervalWidth = intervalWidth;
    }

    public int getIntervalRead() {
        return intervalRead;
    }

    public int getIntervalUpdate() {
        return intervalUpdate;
    }

    public int getIntervalWidth() {
        return intervalWidth;
    }

    public List<Float> getCPUTotalP() {
        return cpuTotal;
    }

    public List<Float> getCPUAMP() {
        return cpuAM;
    }

    public List<Integer> getMemoryAM() {
        return memoryAM;
    }

    public int getMemTotal() {
        return memTotal;
    }

    public List<String> getMemUsed() {
        return memUsed;
    }

    public List<String> getMemAvailable() {
        return memAvailable;
    }

    public List<String> getMemFree() {
        return memFree;
    }

    public List<String> getCached() {
        return cached;
    }

    public List<String> getThreshold() {
        return threshold;
    }

    @Override public void onCreate() {
        cpuTotal = new ArrayList<Float>(maxSamples);
        cpuAM = new ArrayList<Float>(maxSamples);
        memoryAM = new ArrayList<Integer>(maxSamples);
        memUsed = new ArrayList<String>(maxSamples);
        memAvailable = new ArrayList<String>(maxSamples);
        memFree = new ArrayList<String>(maxSamples);
        cached = new ArrayList<String>(maxSamples);
        threshold = new ArrayList<String>(maxSamples);

        pId = android.os.Process.myPid();

        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        amMI = am.getProcessMemoryInfo(new int[] { pId });
        mi = new ActivityManager.MemoryInfo();

        readThread.start();
    }

    @Override public void onDestroy() {
        try {
            readThread.interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
        synchronized (this) {
            readThread = null;
            notify();
        }
    }

    private Runnable readRunnable = new Runnable() {
        @Override
        public void run() {
            // The service makes use of an explicit Thread instead of a Handler because with the Threat the code is executed more synchronously.
            Thread thisThread = Thread.currentThread();
            while (readThread == thisThread) {
                read();
                try {
                    Thread.sleep(intervalRead);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

/*		public synchronized void stop() {
            readThread = null;
			notify();
		}*/

    };

    public class ResourceDataBinder extends Binder {
        public ResourceMonitorService getService() {
            return ResourceMonitorService.this;
        }
    }

    private volatile Thread readThread = new Thread(readRunnable, "readThread");

    private void read() {
        try {
            reader = new BufferedReader(new FileReader("/proc/meminfo"));
            s = reader.readLine();
            while (s != null) {
                // Memory is limited as far as we know
                while (memFree.size() >= maxSamples) {
                    cpuTotal.remove(cpuTotal.size() - 1);
                    cpuAM.remove(cpuAM.size() - 1);
                    memoryAM.remove(memoryAM.size() - 1);

                    memUsed.remove(memUsed.size() - 1);
                    memAvailable.remove(memAvailable.size() - 1);
                    memFree.remove(memFree.size() - 1);
                    cached.remove(cached.size() - 1);
                    threshold.remove(threshold.size() - 1);
                }

                // Memory values. Percentages are calculated in the ActivityMain class.
                if (firstRead && s.startsWith("MemTotal:")) {
                    memTotal = Integer.parseInt(s.split("[ ]+", 3)[1]);
                    firstRead = false;
                } else if (s.startsWith("MemFree:"))
                    memFree.add(0, s.split("[ ]+", 3)[1]);
                else if (s.startsWith("Cached:"))
                    cached.add(0, s.split("[ ]+", 3)[1]);

                s = reader.readLine();
            }
            reader.close();

            // http://stackoverflow.com/questions/3170691/how-to-get-current-memory-usage-in-android
            am.getMemoryInfo(mi);
            if (mi == null) { // Sometimes mi is null
                memUsed.add(0, String.valueOf(0));
                memAvailable.add(0, String.valueOf(0));
                threshold.add(0, String.valueOf(0));
            } else {
                memUsed.add(0, String.valueOf(memTotal - mi.availMem / 1024));
                memAvailable.add(0, String.valueOf(mi.availMem / 1024));
                threshold.add(0, String.valueOf(mi.threshold / 1024));
            }

            memoryAM.add(
                    amMI[0].getTotalPrivateDirty() + amMI[0].getTotalSharedDirty() + amMI[0].getTotalPss());
            //			Log.d("TotalPrivateDirty", String.valueOf(amMI[0].getTotalPrivateDirty()));
            //			Log.d("TotalPrivateClean", String.valueOf(amMI[0].getTotalPrivateClean()));
            //			Log.d("TotalPss", String.valueOf(amMI[0].getTotalPss()));
            //			Log.d("TotalSharedDirty", String.valueOf(amMI[0].getTotalSharedDirty()));

            //			CPU usage percents calculation. It is possible negative values or values higher than 100% may appear.
            //			http://stackoverflow.com/questions/1420426
            //			http://kernel.org/doc/Documentation/filesystems/proc.txt
            reader = new BufferedReader(new FileReader("/proc/stat"));
            sa = reader.readLine().split("[ ]+", 9);
            work = Long.parseLong(sa[1]) + Long.parseLong(sa[2]) + Long.parseLong(sa[3]);
            total = work + Long.parseLong(sa[4]) + Long.parseLong(sa[5]) + Long.parseLong(sa[6]) + Long
                    .parseLong(sa[7]);
            reader.close();

            reader = new BufferedReader(new FileReader("/proc/" + pId + "/stat"));
            sa = reader.readLine().split("[ ]+", 18);
            workAM = Long.parseLong(sa[13]) + Long.parseLong(sa[14]) + Long.parseLong(sa[15]) + Long
                    .parseLong(sa[16]);
            reader.close();

            if (totalBefore != 0) {
                totalT = total - totalBefore;
                workT = work - workBefore;
                workAMT = workAM - workAMBefore;

                cpuTotal.add(0, restrictPercentage(workT * 100 / (float) totalT));
                cpuAM.add(0, restrictPercentage(workAMT * 100 / (float) totalT));
            }

            totalBefore = total;
            workBefore = work;
            workAMBefore = workAM;

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private float restrictPercentage(float percentage) {
        if (percentage > 100)
            return 100;
        else if (percentage < 0)
            return 0;
        else
            return percentage;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new ResourceDataBinder();
    }
}
