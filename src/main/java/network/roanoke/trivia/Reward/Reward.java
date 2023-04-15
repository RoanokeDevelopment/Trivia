package network.roanoke.trivia.Reward;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import network.roanoke.trivia.Trivia;

public class Reward {

    public String itemName;
    public String itemDisplayName;
    public Integer quantity;
    public ItemStack itemStack;

    public Reward(String itemName, String itemDisplayName, Integer quantity) {
        this.itemName = itemName;
        this.itemDisplayName = itemDisplayName;
        this.quantity = quantity;
        this.itemStack = getItemStack(itemName);

        Trivia.LOGGER.info("Reward item: " + itemDisplayName + " - itemName : " + itemName + " ItemStack: " + itemStack.toString());
    }

    // take the itemName and return an ItemStack
    public ItemStack getItemStack(String itemName) {
        return new ItemStack(Registry.ITEM.get(new Identifier(itemName)), quantity);
    }

}
