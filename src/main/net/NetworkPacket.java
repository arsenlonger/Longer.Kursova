package main.net;

import java.io.Serializable;

public class NetworkPacket implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum PacketType {
        CONNECT,
        CHAT_MESSAGE,
        VOTE_CAST,
        GAME_STATE_UPDATE
    }

    private final PacketType type;
    private final String senderName;
    private final String content;

    public NetworkPacket(PacketType type, String senderName, String content) {
        this.type = type;
        this.senderName = senderName;
        this.content = content;
    }

    public PacketType getType() { return type; }
    public String getSenderName() { return senderName; }
    public String getContent() { return content; }
}
