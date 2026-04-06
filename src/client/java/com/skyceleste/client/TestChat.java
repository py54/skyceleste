package com.skyceleste.client;

import net.minecraft.client.gui.components.ChatComponent;

public class TestChat {
    public void test(ChatComponent c) {
        c.clearMessages(false);
        c.addMessage(null);
        // c.addMessage(null, null, null);
    }
}
