package com.criteo.publisher;

import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue;
import android.support.annotation.NonNull;
import java.util.concurrent.CountDownLatch;

public class ThreadingUtil {

  public static void runOnMainThreadAndWait(@NonNull Runnable runnable) {
    CountDownLatch latch = new CountDownLatch(1);

    new Handler(Looper.getMainLooper()).post(() -> {
      runnable.run();
      latch.countDown();
    });

    try {
      latch.await();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public static void waitForAllThreads(@NonNull TrackingCommandsExecutor trackingCommandsExecutor) {
    try {
      trackingCommandsExecutor.waitCommands();
    } catch(InterruptedException e) {
      throw new RuntimeException(e);
    }

    // FIXME This is a wait with two different steps. Because of those steps, it may me possible
    //  that we're not awaiting all threads. For instance, given an async task posting in main
    //  thread, which is again posting in async thread. Then the last task is not waited.
    //  Generally we have two level of tasks: async before and then on main thread after.
    //  But it would be great to have a single async entry point and consider main thread just as an
    //  normal async task.
    waitForMessageQueueToBeIdle();
  }

  /**
   * Blocks the current threads until the {@link MessageQueue} associated to the provided
   * {@link Looper} is idle.
   */
  public static void waitForMessageQueueToBeIdle() {
    CountDownLatch latch = new CountDownLatch(1);

    Looper looper = Looper.getMainLooper();
    MessageQueue messageQueue = looper.getQueue();

    messageQueue.addIdleHandler(() -> {
      latch.countDown();
      return false;
    });

    new Handler(looper).post(() -> {
      // No-op task just to give something to do to the main looper.
      // If it is doing nothing when this method is called, the idle handler may never be called
      // and then block the execution of the program.
    });


    try {
      latch.await();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
