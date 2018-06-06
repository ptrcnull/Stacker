package ml.bjorn.stacker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class KeyListener {

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (Keybinds.stack.isPressed()) {
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            PlayerControllerMP playerController = Minecraft.getMinecraft().playerController;

            InventoryPlayer inventory = player.inventory;
            Container inventoryContainer = player.inventoryContainer;

            ItemStack current = inventory.getCurrentItem();
            int currentCount = current.getCount();
            Slot currentSlot = inventoryContainer.getSlot(inventory.currentItem);

            Stacker.logger.info("Current item: " + current.getDisplayName() + "(" + current.getUnlocalizedName() + ") * " + current.getCount());
            Stacker.logger.info("Current slot ID: " + DataSlotToNetworkSlot(currentSlot.slotNumber));

            if (currentCount == current.getMaxStackSize()) return;

            for (Slot foundSlot : inventoryContainer.inventorySlots) {

                ItemStack found = foundSlot.getStack();
                int foundCount = found.getCount();
                if (foundCount == 0) continue;

//                Stacker.logger.info("item: " + found.getUnlocalizedName() + ", count: " + foundCount + ", sn: " + foundSlot.slotNumber);

                if (!found.isItemEqual(current)) continue;
                if (foundSlot.slotNumber == DataSlotToNetworkSlot(currentSlot.slotNumber)) continue;

                Stacker.logger.info("Found another stack of " + current.getDisplayName() + " with count " + foundCount + " at slot " + foundSlot.slotNumber);

                playerController.windowClick(inventoryContainer.windowId, foundSlot.slotNumber, 0, ClickType.PICKUP, player);
                playerController.windowClick(inventoryContainer.windowId, DataSlotToNetworkSlot(currentSlot.slotNumber), 0, ClickType.PICKUP, player);

                if (foundCount + currentCount > current.getMaxStackSize()) {
                    playerController.windowClick(inventoryContainer.windowId, foundSlot.slotNumber, 0, ClickType.PICKUP, player);
                }

                return;
            }
            player.sendStatusMessage(new TextComponentString(TextFormatting.RED + "Item not found!"), true);
        }
    }

    // https://gist.github.com/SirCmpwn/459a1691c3dd751db160
    /// <summary>
    /// Thanks to some idiot at Mojang
    /// </summary>
    private static int DataSlotToNetworkSlot(int index) {
        if (index <= 8)
            index += 36;
        else if (index == 100)
            index = 8;
        else if (index == 101)
            index = 7;
        else if (index == 102)
            index = 6;
        else if (index == 103)
            index = 5;
        else if (index >= 80 && index <= 83)
            index -= 79;
        return index;
    }
}
