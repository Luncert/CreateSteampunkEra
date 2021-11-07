// package com.luncert.ccenhanced.content.robot;
//
// import com.luncert.ccenhanced.index.AllBlocks;
// import com.luncert.ccenhanced.content.robot.RobotTileEntity.RobotMovementMode;
// import com.simibubi.create.content.contraptions.components.structureMovement.*;
// import com.simibubi.create.foundation.utility.NBTHelper;
// import net.minecraft.block.BlockState;
// import net.minecraft.entity.Entity;
// import net.minecraft.nbt.CompoundNBT;
// import net.minecraft.util.Direction;
// import net.minecraft.util.math.BlockPos;
// import net.minecraft.world.IWorld;
// import net.minecraft.world.World;
// import net.minecraft.world.gen.feature.template.Template.BlockInfo;
// import org.apache.commons.lang3.tuple.Pair;
//
// import java.util.Queue;
//
// public class RobotContraption extends TranslatingContraption {
//
//     private static final ContraptionType ROBOT = ContraptionType.register("robot", RobotContraption::new);
//
//     private RobotMovementMode rotationMode;
//
//     public RobotContraption() {
//         this(RobotMovementMode.ROTATE);
//     }
//
//     public RobotContraption(RobotMovementMode rotationMode) {
//         this.rotationMode = rotationMode;
//     }
//
//     @Override
//     public boolean assemble(World world, BlockPos pos) throws AssemblyException {
//         if (!searchMovedStructure(world, pos, null))
//             return false;
//
//         // add robot block itself
//         BlockInfo blockInfo = new BlockInfo(pos, AllBlocks.ROBOT.getDefaultState(), null);
//         addBlock(pos, Pair.of(blockInfo, null));
//
//         return blocks.size() != 1;
//     }
//
//     @Override
//     protected boolean addToInitialFrontier(World world, BlockPos pos, Direction direction, Queue<BlockPos> frontier) {
//         frontier.clear();
//         // refers to MountedContraption, use block above as anchor
//         frontier.add(pos.above());
//         return true;
//     }
//
//     @Override
//     protected ContraptionType getType() {
//         return ROBOT;
//     }
//
//
//     @Override
//     public CompoundNBT writeNBT(boolean spawnPacket) {
//         CompoundNBT tag = super.writeNBT(spawnPacket);
//         NBTHelper.writeEnum(tag, "RotationMode", rotationMode);
//         return tag;
//     }
//
//     @Override
//     public void readNBT(World world, CompoundNBT nbt, boolean spawnData) {
//         rotationMode = NBTHelper.readEnum(nbt, "RotationMode", RobotMovementMode.class);
//         super.readNBT(world, nbt, spawnData);
//     }
//
//     @Override
//     protected boolean customBlockPlacement(IWorld world, BlockPos pos, BlockState state) {
//         return AllBlocks.ROBOT.has(state);
//     }
//
//     @Override
//     protected boolean customBlockRemoval(IWorld world, BlockPos pos, BlockState state) {
//         return AllBlocks.ROBOT.has(state);
//     }
//
//     @Override
//     public boolean canBeStabilized(Direction facing, BlockPos localPos) {
//         // refers to MountedContraption
//         return true;
//     }
//
//     @Override
//     public void addExtraInventories(Entity robotEntity) {
//         // TODO
//         // if (!(cart instanceof IInventory))
//         //     return;
//         // IItemHandlerModifiable handlerFromInv = new ContraptionInvWrapper(true, new InvWrapper((IInventory) cart));
//         // inventory = new ContraptionInvWrapper(handlerFromInv, inventory);
//     }
//
//     @Override
//     public ContraptionLighter<?> makeLighter() {
//         return new NonStationaryLighter<>(this);
//     }
// }
