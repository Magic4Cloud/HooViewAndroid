package com.easyvaas.common.gift.animator;

import java.util.concurrent.CountDownLatch;

import android.view.ViewGroup;

import com.easyvaas.common.gift.action.Action;
import com.easyvaas.common.gift.workers.NormalWorker;
import com.easyvaas.common.gift.workers.RedCarFrameWorker;
import com.easyvaas.common.gift.workers.BoatWorker;
import com.easyvaas.common.gift.workers.CastleWorker;
import com.easyvaas.common.gift.workers.DeluxeCarWorker;
import com.easyvaas.common.gift.workers.MeteorWorker;
import com.easyvaas.common.gift.workers.PlaneWorker;
import com.easyvaas.common.gift.workers.RacingCarWorker;
import com.easyvaas.common.gift.workers.Worker;

class AnimationFetcher extends Thread implements ActionFetcher {
    private ActionQueue actionQueue;
    private boolean isRunning = true;

    private Worker boatWorker;
    private Worker castleWorker;
    private Worker deluxeCarWorker;
    private Worker meteorWorker;
    private Worker normalWorker;
    private Worker racingCarWorker;
    private Worker planeWorker;
    private Worker redCarFrameWorker;

    public AnimationFetcher(ViewGroup host, ActionQueue actionQueue) {
        this.actionQueue = actionQueue;
        boatWorker = new BoatWorker(host);
        castleWorker = new CastleWorker(host);
        deluxeCarWorker = new DeluxeCarWorker(host);
        meteorWorker = new MeteorWorker(host);
        normalWorker = new NormalWorker(host);
        racingCarWorker = new RacingCarWorker(host);
        planeWorker = new PlaneWorker(host);
        redCarFrameWorker = new RedCarFrameWorker(host);
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                doWork();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void start() {
        super.start();
    }

    private void doWork() throws InterruptedException {
        Action action = actionQueue.take();
        if (action == null) return;

        Worker worker = null;
        switch (action.getAnimType()) {
            case BOAT:
                worker = boatWorker;
                break;
            case CASTLE:
                worker = castleWorker;
                break;
            case CAR_DELUXE:
                worker = deluxeCarWorker;
                break;
            case METEOR:
                worker = meteorWorker;
                break;
            case NORMAL:
                worker = normalWorker;
                break;
            case CAR_RACING:
                worker = racingCarWorker;
                break;
            case PLANE:
                worker = planeWorker;
                break;
            case CAR_RED:
                worker = redCarFrameWorker;
                break;
        }
        if (worker != null) {
            CountDownLatch latch = new CountDownLatch(1);
            worker.workOnBackground(action, latch);
            latch.await();
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
        interrupt();
    }
}
