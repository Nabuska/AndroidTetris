package com.example.winnabuska.tetristddpractice;

import android.util.Log;

import com.annimon.stream.Optional;
import com.annimon.stream.function.Consumer;
import com.example.winnabuska.tetristddpractice.TetrisLogic.Block;
import com.example.winnabuska.tetristddpractice.TetrisLogic.TetrisModel;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Joona Enbuska on 24.10.2015.
 * TetrisController performs all TetrisActionLogic actions outside of UIThread
 **/

public class TetrisController extends Thread {

    private final long sleepTime = 300;
    private Runnable recurringRunnable;
    private TetrisModel model;
    private ScheduledThreadPoolExecutor executor;
    private boolean gameOver = false;

    public TetrisController(TetrisModel model){
        this.model = model;
        recurringRunnable = () -> model.onTick();
    }

    public void onActivityResume(){
        if((executor==null || executor.isShutdown())&&!gameOver)
            scheduleOnTickTask();
    }

    public void onActivityPause(){
        if(executor!=null)
            executor.shutdown();
    }

    public void onDoubleTapCenter(){
        executor.execute(() -> model.performHardDrop());
    }

    public void onSwipeDown(){
        executor.execute(()-> model.performSoftDrop());
    }

    public void onSwipeLeft(){
        executor.execute(() -> model.moveBlockHorizontally(Block.MOVE_DIRECTION_LEFT));
    }

    public void onSwipeRight(){
        executor.execute(() -> model.moveBlockHorizontally(Block.MOVE_DIRECTION_RIGHT));
    }

    public void onTapRight(){
        executor.execute(() -> model.rotateBlock(Block.CLOCKWISE));
    }

    public void onTapLeft(){
        executor.execute(() -> model.rotateBlock(Block.COUNTERCLOCKWISE));
    }

    public void onGameOver(){
        executor.shutdown();
        gameOver=true;
    }

    private void scheduleOnTickTask(){
        executor = new ScheduledThreadPoolExecutor(1);
        executor.scheduleWithFixedDelay(recurringRunnable, sleepTime, sleepTime, TimeUnit.MILLISECONDS);
    }
}
