package lumien.randomthings.Items;

import lumien.randomthings.RandomThings;
import lumien.randomthings.Network.Packets.PacketPaintBiome;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class ItemBiomePainter extends Item
{
	public ItemBiomePainter()
	{
		this.setUnlocalizedName("biomePainter");
		this.setMaxStackSize(1);
		this.setCreativeTab(RandomThings.creativeTab);
		this.setHasSubtypes(true);

		GameRegistry.registerItem(this, "biomePainter");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.itemIcon = par1IconRegister.registerIcon("RandomThings:biomePainter");
	}

	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
	{
		if (par2EntityPlayer.inventory.hasItem(ModItems.biomeSolution))
		{
			if (!par3World.isRemote)
			{
				for (int i = 0; i < par2EntityPlayer.inventory.getSizeInventory(); i++)
				{
					ItemStack is = par2EntityPlayer.inventory.getStackInSlot(i);
					if (is != null && is.getItem() instanceof ItemBiomeSolution && is.getItemDamage() != 0)
					{
						NBTTagCompound nbt = is.stackTagCompound;
						int charges = nbt.getInteger("charges");
						if (charges > 0)
						{
							Chunk c = par3World.getChunkFromBlockCoords(par4, par6);
							int biomeID = is.getItemDamage() - 1;
							byte[] biomeArray = c.getBiomeArray();
							if ((biomeArray[(par6 & 15) << 4 | (par4 & 15)] & 255) != biomeID)
							{
								biomeArray[(par6 & 15) << 4 | (par4 & 15)] = (byte) (biomeID & 255);
								c.setBiomeArray(biomeArray);
								if (!par2EntityPlayer.capabilities.isCreativeMode)
								{
									nbt.setInteger("charges", charges-1);
									par2EntityPlayer.inventoryContainer.detectAndSendChanges();
								}
								RandomThings.packetPipeline.sendToDimension(new PacketPaintBiome(par4, par5, par6, par3World.provider.dimensionId, biomeID), par3World.provider.dimensionId);
							}
						}
						return true;
					}
				}
			}
			else
			{
				return true;
			}
		}
		else
		{
			return false;
		}
		return false;
	}
}
