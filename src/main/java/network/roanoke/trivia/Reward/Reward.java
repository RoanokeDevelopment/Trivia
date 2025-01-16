package network.roanoke.trivia.Reward;

import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
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
        return new ItemStack(Registries.ITEM.get(Identifier.tryParse(itemName)), quantity);
    }

}
