package io.github.foundationgames.automobility.intermediary;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.foundationgames.automobility.util.HexCons;
import io.github.foundationgames.automobility.util.TriCons;
import io.github.foundationgames.automobility.util.TriFunc;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Intermediary {
    static void init(Intermediary instance) {
        GlobalIntermediaryHolder.INSTANCE = instance;
    }

    static Intermediary get() {
        if (GlobalIntermediaryHolder.INSTANCE == null) {
            throw new RuntimeException("Automobility's load order was disrupted!");
        }

        return GlobalIntermediaryHolder.INSTANCE;
    }

    CreativeModeTab creativeTab(ResourceLocation rl, Supplier<ItemStack> icon);

    void builtinItemRenderer(Item item, HexCons<ItemStack, ItemTransforms.TransformType, PoseStack, MultiBufferSource, Integer, Integer> renderer);

    <T extends AbstractContainerMenu> MenuType<T> menuType(BiFunction<Integer, Inventory, T> factory);

    <T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> void registerMenuScreen(MenuType<T> type, TriFunc<T, Inventory, Component, U> factory);

    void blockRenderType(Block block, RenderType type);

    void blockColorProvider(BlockColor color, Block... blocks);

    void itemColorProvider(ItemColor color, Item... items);

    <T extends BlockEntity> BlockEntityType<T> blockEntity(BiFunction<BlockPos, BlockState, T> factory, Block... blocks);

    <T extends BlockEntity> void blockEntityRenderer(BlockEntityType<T> type, Function<BlockEntityRendererProvider.Context, BlockEntityRenderer<T>> provider);

    void serverSendPacket(ServerPlayer player, ResourceLocation rl, FriendlyByteBuf buf);

    void clientSendPacket(ResourceLocation rl, FriendlyByteBuf buf);

    void serverReceivePacket(ResourceLocation rl, TriCons<MinecraftServer, ServerPlayer, FriendlyByteBuf> andThen);

    void clientReceivePacket(ResourceLocation rl, BiConsumer<Minecraft, FriendlyByteBuf> andThen);

    <T extends Entity> EntityType<T> entityType(MobCategory category, BiFunction<EntityType<?>, Level, T> factory, EntityDimensions size, int updateRate, int updateRange);

    <T extends Entity> void entityRenderer(EntityType<T> entity, Function<EntityRendererProvider.Context, EntityRenderer<T>> factory);

    void modelLayer(ModelLayerLocation layer);

    SimpleParticleType simpleParticleType(boolean z);

    <T extends ParticleOptions> void particleFactory(ParticleType<T> type, Function<SpriteSet, ParticleProvider<T>> factory);

    boolean controllerAccel();

    boolean controllerBrake();

    boolean controllerDrift();

    boolean inControllerMode();
}
