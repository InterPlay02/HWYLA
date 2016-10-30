package mcp.mobius.waila.addons.capability;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.overlay.RayTracing;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.List;

public class HUDHandlerEnergy implements IWailaDataProvider {

    static final IWailaDataProvider INSTANCE = new HUDHandlerEnergy();

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (!config.getConfig("capability.energyinfo") || accessor.getTileEntity() == null)
            return currenttip;

        if (accessor.getNBTData().hasKey("forgeEnergy") && accessor.getTileEntity().hasCapability(CapabilityEnergy.ENERGY, accessor.getSide())) {
            NBTTagCompound energyTag = accessor.getNBTData().getCompoundTag("forgeEnergy");
            int stored = energyTag.getInteger("stored");
            int capacity = energyTag.getInteger("capacity");

            currenttip.add(String.format("%d / %d FE", stored, capacity));
            return currenttip;
        }

        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
        if (te != null) {
            te.writeToNBT(tag);
            RayTraceResult rayTrace = RayTracing.rayTraceServer(player, player.capabilities.isCreativeMode ? 5.0 : 4.5);
            EnumFacing side = null;
            if (rayTrace != null && rayTrace.typeOfHit == RayTraceResult.Type.BLOCK)
                side = rayTrace.sideHit;

            if (te.hasCapability(CapabilityEnergy.ENERGY, side)) {
                IEnergyStorage energyStorage = te.getCapability(CapabilityEnergy.ENERGY, side);
                NBTTagCompound energyTag = new NBTTagCompound();
                energyTag.setInteger("capacity", energyStorage.getMaxEnergyStored());
                energyTag.setInteger("stored", energyStorage.getEnergyStored());
                tag.setTag("forgeEnergy", energyTag);
            }
        }
        return tag;
    }
}
