package com.jiuli.shareserver.Server;

import com.jiuli.shareserver.Server.message.Group;
import com.jiuli.shareserver.Server.message.RespModel;
import com.jiuli.shareserver.Server.message.TypeUtil;
import com.jiuli.shareserver.Server.message.UserInfo;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.internal.PlatformDependent;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentMap;

public class MyServerChannelGroup extends DefaultChannelGroup {

    public final ConcurrentMap<ChannelId, UserInfo>
            userInfoConcurrentMap = PlatformDependent.newConcurrentHashMap();

    public MyServerChannelGroup(EventExecutor executor, byte groupID) {
        super(executor);
        this.groupID = groupID;
    }

    private byte groupID;

    public void setGroupID(byte groupID) {
        this.groupID = groupID;
    }

    public byte getGroupID() {
        return groupID;
    }

    public UserInfo put(Channel channel, UserInfo userInfo) {
        super.add(channel);
        return userInfoConcurrentMap.put(channel.id(), userInfo);
    }


    public boolean remove(Channel channel) {
        boolean remove = super.remove(channel);
        return null != userInfoConcurrentMap.remove(channel.id()) && remove;
    }

    public void writeAndFlush() {
        RespModel<Group> model = new RespModel<>();
        model.setType(TypeUtil.GROUP);
        model.setSessionID(0);
        model.setResult(new Group(groupID, new ArrayList<>(userInfoConcurrentMap.values())));
        writeAndFlush(model);
    }

    public boolean containsKey(Channel channel) {
        return userInfoConcurrentMap.containsKey(channel.id());
    }

    @Override
    public void clear() {
        super.clear();
        userInfoConcurrentMap.clear();
    }
}
