package com.jiuli.shareserver.Server.message;

import com.jiuli.shareserver.Server.ChannelHandlerPool;
import com.jiuli.shareserver.Server.MyServerChannelGroup;
import io.netty.channel.Channel;

import java.util.Collection;
import java.util.HashSet;


public class Groups {

    private int allCount;
    private byte inGroupID;
    private int capacity;
    private HashSet<GroupSimpleInfo> groupSimpleInfo = new HashSet<>();

    public int getAllCount() {
        return allCount;
    }

    public void setAllCount(int allCount) {
        this.allCount = allCount;
    }


    public HashSet<GroupSimpleInfo> getGroupSimpleInfo() {
        return groupSimpleInfo;
    }

    public void setGroupSimpleInfo(HashSet<GroupSimpleInfo> groupSimpleInfo) {
        this.groupSimpleInfo = groupSimpleInfo;
    }

    public byte getInGroupID() {
        return inGroupID;
    }


    public void setInGroupID(byte inGroupID) {
        this.inGroupID = inGroupID;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public static Groups getGroups(Channel channel) {

        Groups groups = new Groups();
        groups.setInGroupID(ChannelHandlerPool.getInGrouID(channel));
        groups.allCount = ChannelHandlerPool.channelUserMap.size();
        UserInfo userInfo = ChannelHandlerPool.channelUserMap.get(channel.id());

        if (userInfo == null) {
            return null;
        }
        for (MyServerChannelGroup myServerChannelGroup : ChannelHandlerPool.myServerChannelGroupMap.values()) {
            StringBuilder buffer = new StringBuilder();
            byte count = 0;

            Collection<UserInfo> values = myServerChannelGroup.userInfoConcurrentMap.values();

            for (UserInfo value : values) {
                buffer.append(value.getName());
                buffer.append(", ");
                if (count++ > 4) {
                    break;
                }
            }

            String names = buffer.toString();
            groups.groupSimpleInfo.add(
                    new GroupSimpleInfo(
                            myServerChannelGroup.getGroupID(),
                            (byte) values.size(),
                            count > 0 ? names.substring(0, names.length() - 2) : names));
        }
        return groups;
    }

    static class GroupSimpleInfo {
        private byte groupId;
        private byte groupCount;
        private String names;

        GroupSimpleInfo(byte groupId, byte groupCount, String names) {
            this.groupId = groupId;
            this.groupCount = groupCount;
            this.names = names;
        }

        public byte getGroupCount() {
            return groupCount;
        }

        public void setGroupCount(byte groupCount) {
            this.groupCount = groupCount;
        }

        public String getNames() {
            return names;
        }

        public void setNames(String names) {
            this.names = names;
        }

        public byte getGroupId() {
            return groupId;
        }

        public void setGroupId(byte groupId) {
            this.groupId = groupId;
        }

    }
}
