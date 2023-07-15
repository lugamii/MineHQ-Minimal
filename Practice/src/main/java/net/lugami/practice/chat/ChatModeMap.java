package net.lugami.practice.chat;

import net.lugami.qlib.chat.ChatPopulator;

import java.util.UUID;

public class ChatModeMap extends PersistMap<ChatPopulator> {

    public ChatModeMap() {
        super("ChatModes", "ChatMode");
    }

    @Override
    public String getRedisValue(ChatPopulator chatPopulator) {
        return chatPopulator.getName();
    }

    @Override
    public Object getMongoValue(ChatPopulator chatPopulator) {
        return chatPopulator.getName();
    }

    @Override
    public ChatPopulator getJavaObject(String str) {
        return ChatManager.findByName(str);
    }

    public ChatPopulator getPopulator (UUID check) {
        return (contains(check) ? getValue(check) : new ChatPopulator.PublicChatProvider());
    }

    public void setPopulator (UUID update, ChatPopulator populator) {
        updateValueAsync(update, populator);
    }

//    @Override
//    public ChatMode getJavaObject(String str) {
//        return (ChatMode.valueOf(str));
//    }
//
//    @Override
//    public Object getMongoValue(ChatMode chatMode) {
//        return (chatMode.name());
//    }
//
//    public ChatMode getChatMode(UUID check) {
//        return (contains(check) ? getValue(check) : ChatMode.PUBLIC);
//    }
//
//    public void setChatMode(UUID update, ChatMode chatMode) {
//        updateValueAsync(update, chatMode);
//    }

}
