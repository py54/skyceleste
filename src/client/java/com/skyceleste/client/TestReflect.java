package com.skyceleste.client;

import net.minecraft.client.gui.components.ChatComponent;
import java.lang.reflect.Method;

public class TestReflect {
    public void run() {
        for (Method m : ChatComponent.class.getDeclaredMethods()) {
            System.out.println(m.getName() + " " + m.getParameterCount() + " " + m.getReturnType().getName());
        }
    }
}
