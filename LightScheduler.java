
package com.example.demo1;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread safe scheduler timer for traffic lights
 */
public class LightScheduler implements Runnable{
    private int delay; //delay in ms

    private Lock lock = new ReentrantLock(); //lock to guard the state
    private Condition stateCond = lock.newCondition(); //state condition

    public enum SyncMode {SYNCHRONOUS, OPPOSITE} //synchronization mode

    /**
     * State of a traffic light: GREEN or RED
     */
    public enum State {
        GREEN, RED;

        /**
         * Returns state synchronized with a given mode
         * @param mode sync mode
         * @return synced state
         */
        public State syncedState(SyncMode mode){
            if(mode == SyncMode.SYNCHRONOUS && this == GREEN ||
                mode == SyncMode.OPPOSITE && this == RED)
                return GREEN;

            return RED;
        }
    }

    //current state of the light, changed in run method in another thread and updated in this class
    private State state;

    /**
     * Constructs a scheduler with a given delay and initial state
     * @param delay delay in ms
     * @param initialState starting state of a traffic light
     */
    public LightScheduler(int delay, State initialState){
        this.delay = delay;
        this.state = initialState;
    }

    /**
     * returns the current state
     * @return state
     */
    public State getState() {
        return state;
    }

    /**
     * Wait till the desired state to come
     * @param desiredState desired state to wait for
     */
    public void waitForState(State desiredState){
        lock.lock();
        try {
            while (state != desiredState)
                stateCond.await();
        }
        catch(InterruptedException ex){
            Thread.currentThread().interrupt();
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * Runs the alternating scheduler
     */
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {

                //changing state and signalling
                lock.lock();
                state = State.RED;
                stateCond.signalAll();
                lock.unlock();

                Thread.sleep(delay);

                //changing state and signalling
                lock.lock();
                state = State.GREEN;
                stateCond.signalAll();
                lock.unlock();

                Thread.sleep(delay);
            }
        }

        catch (InterruptedException ex){
            Thread.currentThread().interrupt();
        }

    }
}
