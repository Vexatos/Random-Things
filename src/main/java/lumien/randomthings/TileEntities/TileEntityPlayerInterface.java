package lumien.randomthings.TileEntities;

import lumien.randomthings.Blocks.ModBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;

public class TileEntityPlayerInterface extends TileEntity implements ISidedInventory
{
	String playerName = "";
	EntityPlayerMP playerEntity;

	int[] armorSlots = new int[4];
	int[] hotbarSlots = new int[9];
	int[] mainSlots = new int[27];
	int lockMode;

	public TileEntityPlayerInterface()
	{
		int i = 0;
		for (int slot = 36; slot < 40; slot++)
		{
			armorSlots[i] = slot;
			i += 1;
		}

		i = 0;
		for (int slot = 9; slot < 36; slot++)
		{
			mainSlots[i] = slot;
			i += 1;
		}

		i = 0;
		for (int slot = 0; slot < 9; slot++)
		{
			hotbarSlots[i] = slot;
			i += 1;
		}

		lockMode = 0;
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet)
	{
		readFromNBT(packet.func_148857_g());
	}

	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound nbtTag = new NBTTagCompound();
		this.writeToNBT(nbtTag);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTag);
	}

	public EntityPlayer getPlayer()
	{
		return this.playerEntity;
	}

	@Override
	public void updateEntity()
	{
		if (!this.worldObj.isRemote)
		{
			if (this.playerEntity == null && this.playerName != "")
			{
				EntityPlayerMP tempPlayer = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(this.playerName);
				if (tempPlayer != null)
				{
					playerEntity = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(this.playerName);
					this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, ModBlocks.playerInterface);
				}
			}
			else
			{
				EntityPlayerMP tempPlayer = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(this.playerName);
				if (tempPlayer != playerEntity)
				{
					this.playerEntity = null;
					this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, ModBlocks.playerInterface);
				}
			}
		}
	}

	public void setPlayerName(String name)
	{
		this.playerName = name;
	}

	@Override
	public int getSizeInventory()
	{
		if (this.playerEntity == null)
		{
			return 0;
		}
		return playerEntity.inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int i)
	{
		if (this.playerEntity == null)
		{
			return null;
		}
		return playerEntity.inventory.getStackInSlot(i);
	}

	@Override
	public ItemStack decrStackSize(int i, int j)
	{
		if (this.playerEntity == null)
		{
			return null;
		}
		ItemStack newStack = playerEntity.inventory.decrStackSize(i, j);
		updateSlot(i);
		return newStack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i)
	{
		if (this.playerEntity == null)
		{
			return null;
		}
		return playerEntity.inventory.getStackInSlotOnClosing(i);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack)
	{
		if (this.playerEntity == null)
		{
			return;
		}
		playerEntity.inventory.setInventorySlotContents(i, itemstack);
		updateSlot(i);
	}

	@Override
	public int getInventoryStackLimit()
	{
		if (this.playerEntity == null)
		{
			return 0;
		}
		return playerEntity.inventory.getInventoryStackLimit();
	}

	private void updateSlot(int slot)
	{
		playerEntity.sendContainerToPlayer(playerEntity.inventoryContainer);
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this && player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) < 64;
	}

	@Override
	public void openInventory()
	{
		if (this.playerEntity != null)
		{
			this.playerEntity.inventory.openInventory();
		}
	}

	@Override
	public void markDirty()
	{
		super.markDirty();
		if (this.playerEntity != null && this.playerEntity.inventoryContainer != null)
		{
			this.playerEntity.inventoryContainer.detectAndSendChanges();
		}
	}

	@Override
	public void closeInventory()
	{
		if (this.playerEntity != null)
		{
			this.playerEntity.inventory.closeInventory();
		}
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack)
	{
		if (this.playerEntity == null && this.playerEntity.inventoryContainer != null)
		{
			return false;
		}
		return this.playerEntity.inventoryContainer.getSlotFromInventory(this.playerEntity.inventory, i).isItemValid(itemstack);
	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setString("playerName", this.playerName);
		par1NBTTagCompound.setInteger("lockMode", lockMode);
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);
		this.playerName = par1NBTTagCompound.getString("playerName");
		this.lockMode = par1NBTTagCompound.getInteger("lockMode");
	}

	public boolean hasPlayer()
	{
		return this.playerEntity != null;
	}

	public InventoryPlayer getPlayerInventory()
	{
		return this.playerEntity.inventory;
	}

	public String getPlayerName()
	{
		return playerName;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side)
	{
		if (side == 0)
		{
			return hotbarSlots;
		}
		else if (side == 1)
		{
			return armorSlots;
		}
		else
		{
			return mainSlots;
		}
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j)
	{
		return true;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j)
	{
		return true;
	}

	@Override
	public String getInventoryName()
	{
		return "Player Interface";
	}

	@Override
	public boolean hasCustomInventoryName()
	{
		return true;
	}
}