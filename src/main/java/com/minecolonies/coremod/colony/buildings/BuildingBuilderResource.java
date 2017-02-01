package com.minecolonies.coremod.colony.buildings;

import com.minecolonies.coremod.entity.ai.item.handling.ItemStorage;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.io.Serializable;
import java.util.Comparator;

import org.jetbrains.annotations.NotNull;

/**
 * Information about a resource.
 * - How many are needed to finish the build
 * - How many are available to the builder
 * - How many are in the player's inventory (client side only)
 */
public class BuildingBuilderResource extends ItemStorage
{
    /**
     * Availability status of the resource.
     * according to the builder's chest, inventory and the player's inventory
     */
    public enum RessourceAvailability
    {
        NOT_NEEDED,
        DONT_HAVE,
        NEED_MORE,
        HAVE_ENOUGH
    }

    private       int       amountAvailable;
    private       int       amountPlayer;


    /**
     * Constructor for a resource.
     *
     * @param item              the item.
     * @param damageValue       it's damage value.
     * @param amount            amount for this resource.
     */
     public BuildingBuilderResource(@NotNull final Item item, final int damageValue, final int amount)
     {
         super(item, damageValue, amount, false);
         this.amountAvailable=0;
         this.amountPlayer=0;
     }

    /**
     * Constructor for a resource.
     *
     * @param item              the item.
     * @param damageValue       it's damage value.
     * @param amount            amount for this resource.
     * @param available         optional amount available for this resource
     */
     public BuildingBuilderResource(@NotNull final Item item, final int damageValue, final int amount, final int available)
     {
         this(item, damageValue, amount);
         this.amountAvailable=available;
     }

    public String getName()
    {
        //It is the bet way ?
        return new ItemStack(getItem()).getDisplayName();
    }

    /**
     * get the amount available for this resource.
     * i.e. amount in the chest + amount in the builder's inventory
     */
    public int getAvailable()
    {
        return amountAvailable;
    }

    /**
     * Setter for the available resource amount.
     *
     * @param amount this is the new amount available
     */
    public void setAvailable(final int amount)
    {
        amountAvailable = amount;
    }

    /**
     * Add to the current amount available.
     *
     * @param amount to add to the current amount available
     */
    public void addAvailable(final int amount)
    {
        this.amountAvailable+=amount;
    }

    public void setPlayerAmount(final int amount)
    {
        amountPlayer = amount;
    }

    public int getPlayerAmount()
    {
        return amountPlayer;
    }

    public RessourceAvailability getAvailabilityStatus()
    {
        if (getAmount() > amountAvailable)
        {
            if (amountPlayer == 0)
            {
                return RessourceAvailability.DONT_HAVE;
            }
            if (amountPlayer < (getAmount()-amountAvailable))
            {
                return RessourceAvailability.NEED_MORE;
            }
            return RessourceAvailability.HAVE_ENOUGH;
        }
        return RessourceAvailability.NOT_NEEDED;
    }

    @Override
    public String toString()
    {
        final int itemId= Item.getIdFromItem(getItem());
        return getName() + "(p:"+amountPlayer+" a:" +amountAvailable+" n:"+getAmount()+" id="+itemId+" damage="+getDamageValue()+") => "+getAvailabilityStatus().name();
    }


    /**
     * Comparator class for BuildingBuilderResource.
     *
     * This is use in the gui to order the list of resources needed.
     */
    public static class ResourceComparator implements Comparator<BuildingBuilderResource>, Serializable
    {
        private static final long serialVersionUID = 1;

        /**
         * Compare to resource together.
         *
         * We want the item availalable in the player inventory first and the one not needed last
         * In alphabetical order otherwise
         */
        @Override
        public int compare(BuildingBuilderResource resource1, BuildingBuilderResource resource2)
        {
            if  (resource1.getAvailabilityStatus()==resource2.getAvailabilityStatus())
            {
                return resource1.getName().compareTo(resource2.getName());
            }

            return resource2.getAvailabilityStatus().compareTo(resource1.getAvailabilityStatus());
        }
    }

}
