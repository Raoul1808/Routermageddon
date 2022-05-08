package mew;

import arc.Events;
import arc.util.Log;
import mindustry.Vars;
import mindustry.content.StatusEffects;
import mindustry.game.EventType.*;
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
                    RouterEvents.triggerRandomEvent(e.tile);
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

    @Override
    public void loadContent()
    {
        // load content
    }
}
