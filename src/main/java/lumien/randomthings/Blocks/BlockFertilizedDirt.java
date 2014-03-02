package lumien.randomthings.Blocks;

import static net.minecraftforge.common.util.ForgeDirection.UP;

import java.util.Random;

import lumien.randomthings.RandomThings;
import lumien.randomthings.Configuration.Settings;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockFertilizedDirt extends Block
{
	boolean tilled;

	protected BlockFertilizedDirt(boolean tilled)
	{
		super(Material.ground);

		this.setTickRandomly(true);
		this.setHardness(0.6F);
		this.setStepSound(soundTypeGravel);

		if (tilled)
		{
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.9375F, 1.0F);
			this.setLightOpacity(255);
		}

		if (!tilled)
		{
			this.setCreativeTab(RandomThings.creativeTab);
		}

		this.tilled = tilled;
		this.setBlockName("fertilizedDirt" + (tilled ? "_tilled" : ""));
		this.setBlockTextureName("RandomThings:fertilizedDirt");
		GameRegistry.registerBlock(this, "fertilizedDirt" + (tilled ? "_tilled" : ""));
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_)
	{
		return AxisAlignedBB.getAABBPool().getAABB((double) (p_149668_2_ + 0), (double) (p_149668_3_ + 0), (double) (p_149668_4_ + 0), (double) (p_149668_2_ + 1), (double) (p_149668_3_ + 1), (double) (p_149668_4_ + 1));
	}

	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
	{
		return Item.getItemFromBlock(ModBlocks.fertilizedDirt);
	}

	@Override
	public boolean isOpaqueCube()
	{
		return !tilled;
	}

	@Override
	public boolean isFertile(World world, int x, int y, int z)
	{
		return true;
	}

	public boolean canSustainPlant(IBlockAccess world, int x, int y, int z, ForgeDirection direction, IPlantable plantable)
	{
		Block plant = plantable.getPlant(world, x, y + 1, z);
		EnumPlantType plantType = plantable.getPlantType(world, x, y + 1, z);

		switch (plantType)
		{
			case Desert:
				return !tilled;
			case Nether:
				return false;
			case Crop:
				return tilled;
			case Cave:
				return !tilled;
			case Plains:
				return !tilled;
			case Water:
				return false;
			case Beach:
				return !tilled;
		}

		return false;
	}

	@Override
	public void updateTick(World par1World, int posX, int posY, int posZ, Random rng)
	{
		if (par1World != null && !par1World.isAirBlock(posX, posY + 1, posZ) && ((par1World.getBlock(posX, posY, posZ) instanceof IGrowable) || (par1World.getBlock(posX, posY, posZ) instanceof IPlantable)))
		{
			for (int i = 0; i < Settings.fertilizedDirtGrowth; i++)
			{
				if (!par1World.isAirBlock(posX, posY + 1, posZ))
				{
					Block toBoost = par1World.getBlock(posX, posY + 1, posZ);
					if ((toBoost instanceof IGrowable) || (toBoost instanceof IPlantable))
					{
						toBoost.updateTick(par1World, posX, posY, posZ, rng);
					}
				}
			}
		}
	}

}