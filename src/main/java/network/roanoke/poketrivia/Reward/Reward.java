package network.roanoke.poketrivia.Reward;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import network.roanoke.poketrivia.PokeTrivia;

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

        if (itemStack == null) {
            PokeTrivia.LOGGER.error("Failed to load reward item: " + itemName);
        } else {
            PokeTrivia.LOGGER.info("Loaded reward item: " + itemName);
        }

    }

    // take the itemName and return an ItemStack
    public ItemStack getItemStack(String itemName) {
        return new ItemStack(Registry.ITEM.get(new Identifier(itemName)), quantity);
    }

}
