package com.artur114.bananalib.mc;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public final class BananaMC {
    public static AxisAlignedBB createAABBFromPixels(int x, int y, int z, int x1, int y1, int z1) {
        return new AxisAlignedBB(x / 16.0F, y / 16.0F, z / 16.0F, x1 / 16.0F, y1 / 16.0F, z1 / 16.0F);
    }

    public static long chunkPosAsLong(ChunkPos chunkPos) {
        return (long) chunkPos.x & 4294967295L | ((long) chunkPos.z & 4294967295L) << 32;
    }

    public static ChunkPos chunkPosFromLong(long data) {
        return new ChunkPos((int) (data), (int) (data >> 32));
    }

    public static boolean arrayContainsAny(byte[] array, byte... params) {
        for (int i : array) {
            for (int j : params) {
                if (i == j) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean arrayContains(byte[] array, byte param) {
        for (int i : array) {
            if (i == param) {
                return true;
            }
        }
        return false;
    }

    public static boolean arrayContains(int[] array, int param) {
        for (int i : array) {
            if (i == param) {
                return true;
            }
        }
        return false;
    }

    public static <T> boolean arrayContains(T[] array, T value) {
        for (T obj : array) {
            if (obj == value) {
                return true;
            }
        }
        return false;
    }

    public static int findHighestBlock(World world, BlockPos pos) {
        return findHighestBlock(world, pos.getX(), pos.getZ(), false);
    }

    public static int findHighestBlock(World world, BlockPos pos, boolean ignoreLiquids) {
        return findHighestBlock(world, pos.getX(), pos.getZ(), ignoreLiquids);
    }

    public static int findHighestBlock(World world, BlockPos pos, Block... ignoringBlocks) {
        return findHighestBlock(world, pos.getX(), pos.getZ(), ignoringBlocks);
    }

    public static int findHighestBlock(World world, BlockPos pos, Predicate<IBlockState> ignore) {
        return findHighestBlock(world, pos.getX(), pos.getZ(), ignore);
    }

    public static int findHighestBlock(World world, BlockPos pos, Predicate<IBlockState> ignore, Block... ignoringBlocks) {
        return findHighestBlock(world, pos.getX(), pos.getZ(), ignore, ignoringBlocks);
    }

    public static int findHighestBlock(World world, int x, int z, boolean ignoreLiquids) {
        if (ignoreLiquids) {
            return findHighestBlock(world, x, z, (s) -> s.getMaterial().isLiquid(), Blocks.AIR, Blocks.BEDROCK);
        } else {
            return findHighestBlock(world, x, z, (s) -> false, Blocks.AIR, Blocks.BEDROCK);
        }
    }

    public static int findHighestBlock(World world, int x, int z, Block... ignoringBlocks) {
        return findHighestBlock(world, x, z, (s) -> false, ignoringBlocks);
    }

    public static int findHighestBlock(World world, int x, int z, Predicate<IBlockState> ignore) {
        return findHighestBlock(world, x, z, ignore, Blocks.AIR, Blocks.BEDROCK);
    }

    public static int findHighestBlock(World world, int x, int z, Predicate<IBlockState> ignore, Block... ignoringBlocks) {
        Chunk chunk = world.getChunk(x >> 4, z >> 4);
        ExtendedBlockStorage[] storages = chunk.getBlockStorageArray();
        ExtendedBlockStorage storage = storages[storages.length - 1];
        int storageI = storages.length - 1;
        int posY = 15;
        int posX = x & 15;
        int posZ = z & 15;

        while (storageI >= 0) {
            if (storage == null) {
                storageI--;
                if (storageI >= 0) {
                    storage = storages[storageI];
                }
                continue;
            }

            IBlockState state = storage.get(posX, posY, posZ);

            if (!(ignore.test(state) || arrayContains(ignoringBlocks, state.getBlock()))) {
                break;
            }

            posY--;

            if (posY < 0) {
                storageI--;
                posY = 15;
                if (storageI >= 0) {
                    storage = storages[storageI];
                } else {
                    posY = -1;
                    storageI = 0;
                    break;
                }
            }
        }

        return (storageI << 4) + posY;
    }

    public static @Nullable ExtendedBlockStorage ebsLoaded(World world, BlockPos pos) {
        if ((pos.getY() >> 4) >= 16 || (pos.getY() >> 4) < 0) return null;
        Chunk chunk = world.getChunkProvider().getLoadedChunk(pos.getX() >> 4, pos.getZ() >> 4);
        if (chunk == null) return null;
        ExtendedBlockStorage storage = chunk.getBlockStorageArray()[pos.getY() >> 4];
        return storage == null ? chunk.getBlockStorageArray()[pos.getY() >> 4] = new ExtendedBlockStorage(pos.getY() >> 4 << 4, world.provider.hasSkyLight()) : storage;
    }

    public static ExtendedBlockStorage ebs(World world, BlockPos pos) {
        if ((pos.getY() >> 4) >= 16 || (pos.getY() >> 4) < 0) return null;
        Chunk chunk = world.getChunk(pos);
        ExtendedBlockStorage storage = chunk.getBlockStorageArray()[pos.getY() >> 4];
        return storage == null ? chunk.getBlockStorageArray()[pos.getY() >> 4] = new ExtendedBlockStorage(pos.getY() >> 4 << 4, world.provider.hasSkyLight()) : storage;
    }

    public static boolean biomeHasType(byte biome, BiomeDictionary.Type type) {
        Biome b = Biome.getBiome(biome);
        if (b == null) return false;
        return BiomeDictionary.hasType(b, type);
    }

    public static boolean biomeHasType(Biome biome, BiomeDictionary.Type type) {
        if (biome == null) return false;
        return BiomeDictionary.hasType(biome, type);
    }

    public static byte biomeIdOnPos(World world, BlockPos pos) {
        return biomeIdOnPos(world, pos.getX(), pos.getZ());
    }

    public static byte biomeIdOnPos(World world, int x, int z) {
        return world.getChunk(x >> 4, z >> 4).getBiomeArray()[(x & 15) + (z & 15) * 16];
    }

    public static boolean chunkContainsBiomeTypeOnCorners(Chunk chunk, BiomeDictionary.Type type) {
        byte[] chunkBiomeArray = chunk.getBiomeArray();
        if (BananaMC.biomeHasType(chunkBiomeArray[0], type)) return true;
        if (BananaMC.biomeHasType(chunkBiomeArray[15 * 16], type)) return true;
        if (BananaMC.biomeHasType(chunkBiomeArray[15 + 15 * 16], type)) return true;
        return BananaMC.biomeHasType(chunkBiomeArray[15], type);
    }

    public static boolean chunkContainsBiomeType(Chunk chunk, BiomeDictionary.Type type) {
        byte[] chunkBiomeArray = chunk.getBiomeArray();
        for (byte b : chunkBiomeArray) {
            if (BananaMC.biomeHasType(b, type)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkChunkBiomeByteArray(Chunk chunk, Predicate<Byte> test) {
        byte[] chunkBiomeArray = chunk.getBiomeArray();
        for (byte b : chunkBiomeArray) {
            if (test.test(b)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkChunkBiomeArray(Chunk chunk, Predicate<Biome> test) {
        byte[] chunkBiomeArray = chunk.getBiomeArray();
        for (byte b : chunkBiomeArray) {
            Biome biome = Biome.getBiome(b);
            if (biome != null && test.test(biome)) {
                return true;
            }
        }
        return false;
    }

    public static boolean inventoryContains(EntityPlayer player, ItemStack stack) {
        return inventoryContains(player.inventory, stack);
    }

    public static boolean inventoryContains(EntityPlayer player, Item item) {
        return inventoryContains(player.inventory, item);
    }

    public static boolean inventoryContains(EntityPlayer player, Predicate<ItemStack> predicate) {
        return inventoryContains(player.inventory, predicate);
    }

    public static boolean inventoryContains(IInventory inventory, ItemStack stack) {
        for (int i = 0; i != inventory.getSizeInventory(); i++) {
            if (ItemStack.areItemStacksEqual(inventory.getStackInSlot(i), stack)) {
                return true;
            }
        }
        return false;
    }


    public static boolean inventoryContains(IInventory inventory, Item item) {
        for (int i = 0; i != inventory.getSizeInventory(); i++) {
            if (inventory.getStackInSlot(i).getItem() == item) {
                return true;
            }
        }
        return false;
    }

    public static boolean inventoryContains(IInventory inventory, Predicate<ItemStack> predicate) {
        for (int i = 0; i != inventory.getSizeInventory(); i++) {
            if (predicate.test(inventory.getStackInSlot(i))) {
                return true;
            }
        }
        return false;
    }
}
