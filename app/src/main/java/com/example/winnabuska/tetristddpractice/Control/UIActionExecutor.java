package com.example.winnabuska.tetristddpractice.Control;

import com.annimon.stream.Optional;
import com.annimon.stream.function.Consumer;
import com.annimon.stream.function.Supplier;
import com.example.winnabuska.tetristddpractice.TetrisLogic.Square;

/**
 * Created by Joona Enbuska on 24.10.2015.
 * TetrisActionExecutor is a class that provides an easy to use interface for performing TetrisActionLogic
 * tasks ouside of UI Thread.
 * There are only 3 methods that should be called directly: 'performAction(Consumer<UIActionExecutor> sideTask)',
 * 'pauseExecution' and 'continueExecution'.
 **/

public class UIActionExecutor extends Thread {

    private long sleepTime = 400;
    private long alarm;
    private boolean paused = false;
    private Supplier<Boolean> recurringTask;
    private Optional<Consumer<UIActionExecutor>> oneTimeTask;
    private TetrisController controller;

    public UIActionExecutor(TetrisController controller){
        oneTimeTask = Optional.empty();
        this.controller = controller;
        this.recurringTask = () ->  controller.onTick();
        start();
    }

    /*performAction takes a new Consumer as its oneTimeTask IF the last oneTimeTask has fully been executed*/
    public synchronized void performAction(Consumer<UIActionExecutor> sideTask){
        if(!oneTimeTask.isPresent())
            oneTimeTask = Optional.of(sideTask);
        else
            oneTimeTask = Optional.empty();
    }

    public void run(){
        while(recurringTask.get()) {
            if (paused) {
                synchronized (this) {
                    try{
                        while(paused)wait();
                    } catch (InterruptedException e) {}
                    notifyAll();
                }
            }
            alarm = System.currentTimeMillis() + sleepTime;
            do{
                oneTimeTask.ifPresent(te -> {
                    oneTimeTask.get().accept(UIActionExecutor.this);
                    oneTimeTask = Optional.empty();
                });
                try{sleep(1);}catch (InterruptedException e){}
            }while (wakeUpTime());
            sleepTime--;
        }
    }

    public void pauseExecutions(){
        paused = true;
    }

    public synchronized void continueExecutions(){
        paused=false;
        notifyAll();
    }

    public void performHorizontalMove(final int MOVE_DIRECTION){
        controller.moveBlockHorizontally(MOVE_DIRECTION);
    }

    private boolean wakeUpTime(){
        return alarm>System.currentTimeMillis();
    }

    public void performRotate(final int ROTATE_DIRECTION){
        controller.rotateBlock(ROTATE_DIRECTION);
    }

    public void performHardDrop(){
        controller.performHardDrop();
        postponeAlarm();
    }

    public void performSoftDrop(){
        controller.performSoftDrop();
        postponeAlarm();
    }

    /*postpones alarm time so that the next tick will not happen before a full sleepTime has passed from the given moment*/
    private void postponeAlarm() {
        long now = System.currentTimeMillis();
        alarm += sleepTime - (alarm - now);
    }
}
