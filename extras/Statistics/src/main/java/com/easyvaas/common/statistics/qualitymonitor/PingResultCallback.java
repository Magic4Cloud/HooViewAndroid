package com.easyvaas.common.statistics.qualitymonitor;

import java.util.List;

interface PingResultCallback {
    void OnPingFinished(List<PingResultEntity> results);
}
