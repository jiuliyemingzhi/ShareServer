package com.jiuli.shareserver.Server;

import java.util.concurrent.TimeUnit;

public class DispatcherHandler implements Runnable {
    private volatile boolean isCancel = false;

    public final void cancel() {
        isCancel = true;
    }

    @Override
    public void run() {
        try {
            while (!isCancel) {
                if (ChannelHandlerPool.size() > 0) {
                    for (MyServerChannelGroup group
                            : ChannelHandlerPool.myServerChannelGroupMap.values()) {
                        group.writeAndFlush();
                    }
                }
                TimeUnit.MILLISECONDS.sleep(800);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
