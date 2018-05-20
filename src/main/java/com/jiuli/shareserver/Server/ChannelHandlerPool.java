package com.jiuli.shareserver.Server;

import com.jiuli.shareserver.Server.message.ReqModel;
import com.jiuli.shareserver.Server.message.UserInfo;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.internal.PlatformDependent;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

public class ChannelHandlerPool {

    private static final byte POOL_CAPACITY = 50;//最大group数量
    private static final byte GROUP_NORM_SIZE = 30;//一个group标准容量
    private static final byte GROUP_MAXSIZE = 50;//一个group最大容量


    public static final ConcurrentMap<Byte, MyServerChannelGroup> myServerChannelGroupMap = PlatformDependent.newConcurrentHashMap();
    public static final ConcurrentMap<ChannelId, UserInfo> channelUserMap = PlatformDependent.newConcurrentHashMap();
    private static final ConcurrentMap<String, Channel> userChannelMap = PlatformDependent.newConcurrentHashMap();

    static int size() {
        return myServerChannelGroupMap.size();
    }

    private synchronized static byte getNewGroupId() {
        for (byte i = 1; i < POOL_CAPACITY + 1; i++) {
            if (!myServerChannelGroupMap.containsKey(i)) {
                return i;
            }
        }
        return 1;
    }

    private static MyServerChannelGroup getNewMyServerChannelGroup(byte groupID) {
        return new MyServerChannelGroup(GlobalEventExecutor.INSTANCE, groupID);
    }

    public static boolean put(Channel channel, UserInfo userInfo) {
        if (userInfo == null) {
            return false;
        }
        Channel channelOld = userChannelMap.get(userInfo.getUuid());
        if (channelOld != null && channelOld != channel) {
            remove(channelOld);
        }

        if (channelOld == null || channel != channelOld) {
            userChannelMap.put(userInfo.getUuid(), channel);
        }

        UserInfo odlInfo = channelUserMap.get(channel.id());
        if (odlInfo != null) {
            odlInfo.setOf(userInfo);
            return false;
        }

        Collection<MyServerChannelGroup> values = myServerChannelGroupMap.values();
        if (size() == 0) {
            byte groupId = getNewGroupId();
            myServerChannelGroupMap.put(groupId, getNewMyServerChannelGroup(groupId));
        }
        if (size() < POOL_CAPACITY) {
            for (MyServerChannelGroup channels : values) {
                if (channels.size() < GROUP_NORM_SIZE) {
                    put(channels, channel, userInfo);
                    return true;
                }
            }

            byte groupId = getNewGroupId();
            MyServerChannelGroup channels = getNewMyServerChannelGroup(groupId);
            put(channels, channel, userInfo);
            myServerChannelGroupMap.put(groupId, channels);
            return true;
        }

        for (MyServerChannelGroup channels : values) {
            if (channels.size() < GROUP_MAXSIZE) {
                put(channels, channel, userInfo);
                return true;
            }
        }
        return false;
    }

    private static void put(MyServerChannelGroup channels, Channel channel, UserInfo userInfo) {
        channels.put(channel, userInfo);
        channelUserMap.put(channel.id(), userInfo);
    }

    public final static boolean remove(Channel channel) {
        if (channel == null) return false;
        channel.close();
        UserInfo userInfo = channelUserMap.remove(channel.id());
        if (userInfo != null) {
            userChannelMap.remove(userInfo.getUuid());
        }
        for (Map.Entry<Byte, MyServerChannelGroup> entry : myServerChannelGroupMap.entrySet()) {
            MyServerChannelGroup group = entry.getValue();
            if (group.remove(channel)) {
                if (group.size() == 0) {
                    myServerChannelGroupMap.remove(entry.getKey());
                }
                return true;
            }
        }
        return false;
    }

    public static byte getInGrouID(Channel channel) {
        for (Map.Entry<Byte, MyServerChannelGroup> entry : myServerChannelGroupMap.entrySet()) {
            if (entry.getValue().containsKey(channel)) {
                return entry.getKey();
            }
        }
        return -1;
    }

    public static boolean join(Channel channel, ReqModel reqModel) {
        if (reqModel != null) {
            return false;
        }
        return false;
    }

}
