package com.minecolonies.util;

import com.minecolonies.colony.CitizenData;
import com.minecolonies.entity.EntityCitizen;
import net.minecraft.entity.player.EntityPlayerMP;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Contains colony specific utility.
 */
public class ColonyUtils
{
    /**
     * Checks if a citizen is missing from the world.
     * @param citizen the citizen to check.
     * @return true if so.
     */
    public static boolean isCitizenMissingFromWorld(@NotNull final CitizenData citizen)
    {
        final EntityCitizen entity = citizen.getCitizenEntity();

        return entity != null && entity.worldObj.getEntityByID(entity.getEntityId()) != entity;
    }

    /**
     * Checks if the colony has new subscribers.
     * @param oldSubscribers old subscribers.
     * @param subscribers all subscribers.
     * @return true if so.
     */
    public static boolean hasNewSubscribers(@NotNull final Set<EntityPlayerMP> oldSubscribers, @NotNull final Set<EntityPlayerMP> subscribers)
    {
        for (final EntityPlayerMP player : subscribers)
        {
            if (!oldSubscribers.contains(player))
            {
                return true;
            }
        }
        return false;
    }
}
