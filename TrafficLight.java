package com.example.demo1;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import static javafx.application.Platform.runLater;

class TrafficLight implements Runnable{

    //shapes
    private final Circle trafficRed, trafficGreen;
    private final Rectangle pedestrianRed, pedestrianGreen;

    //delay in ms, by default taken from the main application file
    private int delay = TrafficApplication.delay; //delay between switches
    private LightScheduler scheduler;

    //synchronization mode
    private LightScheduler.SyncMode syncMode;

    //ms between blinks
    private static int BLINK_MS = 140;

    //main != null only if this traffic light is synchronized with another one
    TrafficLight main;

    /**
     * Main traffic light constructor
     * @param trafficRed red circle
     * @param trafficGreen green circle
     * @param pedestrianRed red rectangle
     * @param pedestrianGreen green rectangle
     */
    public TrafficLight(Circle trafficRed, Circle trafficGreen,
                        Rectangle pedestrianRed, Rectangle pedestrianGreen){
       this.trafficRed = trafficRed;
       this.trafficGreen = trafficGreen;
       this.pedestrianRed = pedestrianRed;
       this.pedestrianGreen = pedestrianGreen;

       this.syncMode = LightScheduler.SyncMode.SYNCHRONOUS;
        scheduler = new LightScheduler(delay, LightScheduler.State.GREEN);

    }

    /**
     * Synchronized dependent traffic light constructor
     * @param trafficRed red circle
     * @param trafficGreen green circle
     * @param pedestrianRed red rectangle
     * @param pedestrianGreen green rectangle
     * @param main main traffic light to synchronize with
     * @param synchronizationMode sync mode (synced vs opposite)
     */
    public TrafficLight(Circle trafficRed, Circle trafficGreen,
                        Rectangle pedestrianRed, Rectangle pedestrianGreen, TrafficLight main, LightScheduler.SyncMode synchronizationMode){
        this(trafficRed, trafficGreen, pedestrianRed, pedestrianGreen);
        this.main = main;
        this.syncMode = synchronizationMode;
    }

    /**
     * Sets delay of a main traffic light
     * @param delay delay in ms
     */
    public void setDelay(int delay) {
        if(main != null)
            throw new RuntimeException("Only independent traffic lights may have delay set");

        this.delay = delay;
    }

    //disables all colors
    private void resetColors(){
        runLater(() -> {
            trafficRed.setFill(Color.TRANSPARENT);
            trafficGreen.setFill(Color.TRANSPARENT);
            pedestrianRed.setFill(Color.TRANSPARENT);
            pedestrianGreen.setFill(Color.TRANSPARENT);
        });
    }

    //allows traffic to go
    private void enableTraffic(){
        runLater(() -> {
            trafficGreen.setFill(Color.GREEN);
            pedestrianRed.setFill(Color.RED);
        });


        scheduler.waitForState(LightScheduler.State.RED.syncedState(syncMode));
    }

    //allows pedestrians to go
    private void enablePedestrian(){
        runLater(() -> trafficRed.setFill(Color.RED));

        //blink until traffic goes on green
        try {
            while(scheduler.getState() != LightScheduler.State.GREEN.syncedState(syncMode)) {
                Thread.sleep(BLINK_MS);
                runLater(() -> pedestrianGreen.setFill(Color.GREEN));
                Thread.sleep(BLINK_MS);
                runLater(() -> pedestrianGreen.setFill(Color.TRANSPARENT));
            }
        }

        catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            runLater(() -> trafficRed.setFill(Color.TRANSPARENT));
        }

    }

    //starts primary (main) traffic light
    private void startPrimary(){
        scheduler = new LightScheduler(delay, LightScheduler.State.GREEN); //create new light scheduler
        new Thread(scheduler).start();

        //alternate states
        while(!Thread.currentThread().isInterrupted()){

            enableTraffic();
            resetColors();

            enablePedestrian();
            resetColors();
        }
    }

    //starts dependent traffic light
    private void startDependent(){
        scheduler = main.scheduler; //scheduler is the same as of the main traffic light

        while(!Thread.currentThread().isInterrupted()) {
            LightScheduler.State state = scheduler.getState();

            //initial mode depends on the synchronization mode
            if((state == LightScheduler.State.GREEN && syncMode == LightScheduler.SyncMode.SYNCHRONOUS) ||
                    (state == LightScheduler.State.RED && syncMode == LightScheduler.SyncMode.OPPOSITE))
                enableTraffic();

            else
                enablePedestrian();

            resetColors();
        }
    }

    /**
     * Start the traffic light
     */
    @Override
    public void run() {
        if(main == null) //if this traffic light is primary
            startPrimary();

        else
            startDependent();

    }
}

