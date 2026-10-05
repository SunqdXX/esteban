package club.sunqd.esteban.modules.movement;

import club.sunqd.esteban.module.Category;
import club.sunqd.esteban.module.Module;
import club.sunqd.esteban.module.Setting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Jesus extends Module {

    static final double RISE = 0.11;
    static final double DIVE = 0.5;
    static final double ON_TOP = 1.0E-4;
    static final double LIFT = 0.005;

    private static final VoxelShape[] SURFACE = new VoxelShape[10];
    private static Jesus instance;

    static {
        for (int i = 1; i < SURFACE.length; i++)
            SURFACE[i] = Shapes.box(0, 0, 0, 1, top(i), 1);
    }

    private final Setting.Bool lava =
            register(new Setting.Bool("Lava", true));
    private final Setting.Bool vehicles =
            register(new Setting.Bool("Vehicles", true));

    public Jesus() {
        super("Jesus", "Walk, sprint and ride on water and lava. Sneak to sink.", Category.MOVEMENT);
        instance = this;
    }

    public static boolean on() {
        final Jesus j = instance;
        return j != null && j.isActive();
    }

    @Override
    public void onTick() {
        final LocalPlayer p = Minecraft.getInstance().player;
        if (p == null)
            return;
        final Entity mover = mover(p);
        if (mover == null || (mover == p && p.isShiftKeyDown()))
            return;
        if (mover.isInWater() || (lava.get() && mover.isInLava())) {
            final Vec3 v = mover.getDeltaMovement();
            if (v.y < RISE)
                mover.setDeltaMovement(v.x, RISE, v.z);
        }
    }

    private Entity mover(LocalPlayer p) {
        final Entity vehicle = p.getVehicle();
        if (vehicle == null)
            return p.isFallFlying() || p.getAbilities().flying ? null : p;
        if (vehicles.get() && vehicle instanceof LivingEntity && vehicle.getControllingPassenger() == p)
            return vehicle;
        return null;
    }

    public static VoxelShape surface(BlockBehaviour.BlockStateBase state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        if (!(ctx instanceof EntityCollisionContext ec))
            return null;
        final FluidState fluid = state.getFluidState();
        if (fluid.isEmpty())
            return null;
        final Entity e = ec.getEntity();
        final LocalPlayer p = Minecraft.getInstance().player;
        if (e == null || p == null || (e != p && e != p.getVehicle()))
            return null;
        final Jesus j = instance;
        final boolean isLava = fluid.is(FluidTags.LAVA);
        if (isLava ? !j.lava.get() : !fluid.is(FluidTags.WATER))
            return null;
        if (e != j.mover(p) || (e == p && p.isShiftKeyDown()))
            return null;
        if (!isLava && e.getDeltaMovement().y < -DIVE)
            return null;
        final VoxelShape shape = SURFACE[Mth.clamp(Math.round(fluid.getHeight(level, pos) * 9), 1, 9)];
        return standsOn(e.getY(), pos.getY() + shape.max(Direction.Axis.Y)) ? shape : null;
    }

    static double top(int ninths) {
        return Math.min(1.0, ninths / 9.0f + LIFT);
    }

    static boolean standsOn(double feet, double top) {
        return feet >= top - ON_TOP;
    }
}
