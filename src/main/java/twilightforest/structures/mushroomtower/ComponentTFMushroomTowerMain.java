package twilightforest.structures.mushroomtower;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import twilightforest.structures.StructureTFComponent;
import twilightforest.structures.lichtower.ComponentTFTowerRoof;

public class ComponentTFMushroomTowerMain extends ComponentTFMushroomTowerWing {

    public ComponentTFMushroomTowerMain() {
        super();
        // TODO Auto-generated constructor stub
    }

    public ComponentTFMushroomTowerMain(World world, Random rand, int index, int x, int y, int z) {
        this(world, rand, index, x + MAIN_SIZE, y + 4, z + MAIN_SIZE, 2);
    }

    public ComponentTFMushroomTowerMain(World world, Random rand, int index, int x, int y, int z, int rotation) {
        super(index, x, y, z, MAIN_SIZE, 8 + (rand.nextInt(3) * FLOOR_HEIGHT), rotation);

//        // check to make sure we can build the whole tower
//        if (this.boundingBox.maxY > 245)
//        {
//            int amtToLower = (((this.boundingBox.maxY - 245) / 5) * 5) + 5;
//
//            FMLLog.info("[TwilightForest] Lowering Dark Tower max height by %d to be within world bounds", amtToLower);
//            
//            this.height -= amtToLower;
//            this.boundingBox.maxY -= amtToLower;
//        }

        // decorator
        if (this.deco == null) {
            this.deco = new StructureDecoratorMushroomTower();
        }
    }

    protected ComponentTFMushroomTowerMain(int i, int x, int y, int z, int pSize, int pHeight, int direction) {
        super(i, x, y, z, pSize, pHeight, direction);
    }

    @Override
    public void buildComponent(StructureComponent parent, List list, Random rand) {
        if (parent != null && parent instanceof StructureTFComponent) {
            this.deco = ((StructureTFComponent) parent).deco;
        }

        // we should have a door where we started
        addOpening(0, 1, size / 2, 2);

        // should we build a base?
        this.hasBase = true;

        int mainDir = -1;

        // limit sprawl to a reasonable amount
        if (this.getComponentType() < 3) {
            // make a special sub-tower that will lead back here
            // try 6 times
            for (int i = 0; i < 6; i++) {
                mainDir = makeAscenderTower(list, rand);

                if (mainDir != -1) {
                    break;
                }
            }

            // make sub towers
            for (int i = 0; i < 4; i++) {

                if (i == mainDir) {
                    continue;
                }

                int[] dest = getValidOpening(rand, i);

                int childHeight = (rand.nextInt(2) + rand.nextInt(2) + 2) * FLOOR_HEIGHT + 1;

                makeBridge(list, rand, this.getComponentType() + 1, dest[0], dest[1], dest[2], size - 4, childHeight, i);
            }
        } else {
            // add a roof?
            makeARoof(parent, list, rand);
        }

    }

    /**
     * Make a new ascender tower. Return direction (0-4) if successful, -1 if not.
     */
    private int makeAscenderTower(List list, Random rand) {
        int mainDir;
        int[] dest;
        int childHeight;
        mainDir = rand.nextInt(4);

        dest = getValidOpening(rand, mainDir);

        childHeight = (height - dest[1]) + ((rand.nextInt(2) + rand.nextInt(2) + 3) * FLOOR_HEIGHT) + 1;

        boolean madeIt = makeBridge(list, rand, this.getComponentType() + 1, dest[0], dest[1], dest[2], size - 4, childHeight, mainDir, true);

        if (madeIt) {
            FMLLog.fine("[TwilightForest] Main tower made a bridge to another tower");
            return mainDir;
        } else {
            FMLLog.fine("[TwilightForest] Main tower failed to branch off at index " + this.componentType);
            return -1;
        }

    }

    /**
     * Make a mushroom roof!
     */
    @Override
    public void makeARoof(StructureComponent parent, List list, Random rand) {
        ComponentTFTowerRoof roof = new ComponentTFTowerRoofMushroom(this.getComponentType() + 1, this, 1.6F);
        list.add(roof);
        roof.buildComponent(this, list, rand);
    }

    /**
     * Make an opening in this tower for a door. This now only makes one opening, so you need two
     */
    protected void makeDoorOpening(World world, int dx, int dy, int dz, StructureBoundingBox sbb) {
        super.makeDoorOpening(world, dx, dy, dz, sbb);

        // try to remove blocks inside this door
        if (dx == 0) {
            placeBlockAtCurrentPosition(world, Blocks.air, 0, dx + 1, dy + 0, dz, sbb);
            placeBlockAtCurrentPosition(world, Blocks.air, 0, dx + 1, dy + 1, dz, sbb);
        }
        if (dx == size - 1) {
            placeBlockAtCurrentPosition(world, Blocks.air, 0, dx - 1, dy + 0, dz, sbb);
            placeBlockAtCurrentPosition(world, Blocks.air, 0, dx - 1, dy + 1, dz, sbb);
        }
        if (dz == 0) {
            placeBlockAtCurrentPosition(world, Blocks.air, 0, dx, dy + 0, dz + 1, sbb);
            placeBlockAtCurrentPosition(world, Blocks.air, 0, dx, dy + 1, dz + 1, sbb);
        }
        if (dz == size - 1) {
            placeBlockAtCurrentPosition(world, Blocks.air, 0, dx, dy + 0, dz - 1, sbb);
            placeBlockAtCurrentPosition(world, Blocks.air, 0, dx, dy + 1, dz - 1, sbb);
        }

    }

}
