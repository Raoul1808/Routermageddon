package mew;

import arc.Events;
import arc.util.Log;
import mindustry.Vars;
import mindustry.content.StatusEffects;
import mindustry.content.UnitTypes;
import mindustry.game.EventType.*;
import mindustry.game.SpawnGroup;
import mindustry.gen.Call;
import mindustry.gen.Unit;
import mindustry.mod.Mod;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.Router;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class RouterDeathMod extends Mod
{
    public RouterDeathMod()
    {
        Log.info("Loaded Routermageddon");

        Events.on(BlockBuildEndEvent.class, e -> {
            if (Vars.state.rules.pvp) return;
            if (e.tile.build.getClass() == Router.RouterBuild.class)
            {
                if (routerNearby(e.tile))
                {
                    Log.info("Found router nearby!");
                    Vars.ui.hudfrag.showToast("death");
                    triggerDeath();
                }
            }
        });
    }

    private boolean routerNearby(Tile tile)
    {
        boolean up = Vars.world.tile(tile.x, tile.y - 1).block().toString().equals("router");
        boolean down = Vars.world.tile(tile.x, tile.y + 1).block().toString().equals("router");
        boolean left = Vars.world.tile(tile.x - 1, tile.y).block().toString().equals("router");
        boolean right = Vars.world.tile(tile.x + 1, tile.y).block().toString().equals("router");
        return up || down || left || right;
    }

    private void triggerDeath()
    {
        SpawnGroup group = new SpawnGroup();
        group.type = UnitTypes.antumbra;
        group.unitAmount = 100;
        for (Tile spawn : Vars.spawner.getSpawns())
        {
            for (int i = 0; i < group.unitAmount; i++)
            {
                Unit unit = group.createUnit(Vars.state.rules.waveTeam, Vars.state.wave);
                unit.set(spawn.x, spawn.y);
                spawnFlyingUnit(unit);
            }
        }
    }

    private void spawnFlyingUnit(Unit unit)
    {
        // Shamelessly copied from WaveSpawner.java
        unit.rotation = unit.angleTo(world.width()/2f * tilesize, world.height()/2f * tilesize);
        unit.apply(StatusEffects.unmoving, 30f);
        unit.add();
        Call.spawnEffect(unit.x, unit.y, unit.rotation, unit.type);
    }

    @Override
    public void loadContent()
    {
        // load content
    }
}
