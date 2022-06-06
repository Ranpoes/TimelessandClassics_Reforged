package com.tac.guns.client.handler;


import com.tac.guns.Reference;
import net.minecraftforge.fml.common.Mod;


/**
 * Author: ClumsyAlien
 */

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class IronSightSwitchEvent
{
    private static IronSightSwitchEvent instance;

    public static IronSightSwitchEvent get()
    {
        if(instance == null)
        {
            instance = new IronSightSwitchEvent();
        }
        return instance;
    }

    private IronSightSwitchEvent()
    {
    }

    //Disabled, mod conflicts mess with the Array handling big time!
    /*@SubscribeEvent
    public void onKeyPressed(InputEvent.KeyInputEvent event)
    {
        if(Minecraft.getInstance().player == null)
        {
            return;
        }
        if(KeyBinds.KEY_SIGHT_SWITCH.isPressed() && event.getAction() == GLFW.GLFW_PRESS)
        {
            PacketHandler.getPlayChannel().sendToServer(new MessageIronSightSwitch());
        }
    }*/

}

