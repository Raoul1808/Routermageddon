package mew;

import arc.Core;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.game.SpawnGroup;
import mindustry.gen.*;
import mindustry.type.Category;
import mindustry.world.Tile;
import mindustry.world.consumers.ConsumeItems;
import mindustry.world.consumers.ConsumeType;

import java.util.Locale;
import java.util.Random;

import static mindustry.Vars.*;

public class RouterEvents
{
    private static Random random;

    public static void triggerRandomEvent(Tile srcTile)
    {
        if (random == null)
            random = new Random();

        RouterEventType[] events = RouterEventType.values();

        switch (events[random.nextInt(events.length)])
        {
            case HundredBosses:
                HundredBosses();
                break;
            case PowerOut:
                RemovePower();
                break;
            case Pacifism:
                RemoveTurrets();
                break;
            case LuckyEnding:
                RemoveSurroundingRouters(srcTile);
                break;
            case Eksuplosion:
                SpawnNuke(srcTile);
                break;
            case OuiOuiBaguette:
                ChangeLanguage(srcTile);
                break;
            case BulletSpiral:
                SpawnSpreadBulletCircle(srcTile);
                break;
        }
    }

    private static void HundredBosses()
    {
        showMessage("Die.");
        SpawnGroup group = new SpawnGroup();
        group.type = UnitTypes.antumbra;
        group.unitAmount = 100;
        for (Tile spawn : Vars.spawner.getSpawns())
        {
            for (int i = 0; i < group.unitAmount; i++)
            {
                Unit unit = group.createUnit(Vars.state.rules.waveTeam, Vars.state.wave);
                unit.set(spawn.getX(), spawn.getY());
                spawnFlyingUnit(unit);
            }
        }
    }

    private static void RemovePower()
    {
        showMessage("Looks like you'll need new reactors, huh");
        world.tiles.forEach(tile -> {
            if (tile.block().category == Category.power)
                tile.remove();
        });
    }

    private static void RemoveTurrets()
    {
        showMessage("I hope you didn't care too much about your defenses");
        world.tiles.forEach(tile -> {
            if (tile.block().category == Category.turret)
                tile.remove();
        });
    }

    private static void RemoveSurroundingRouters(Tile srcTile)
    {
        showMessage("Consider yourself lucky");
        for (int x = -1; x <= 1; x++)
        {
            for (int y = -1; y <= 1; y++)
            {
                world.tile(srcTile.x + x, srcTile.y + y).remove();
            }
        }
    }

    private static void SpawnNuke(Tile srcTile)
    {
        showMessage("BOOM!");
        // Spawn thorium reactor explosion
        float x = srcTile.getX();
        float y = srcTile.getY();
        float explosionRadius = 19;
        float explosionDamage = 1250;
        Vec2 tr = new Vec2();

        Sounds.explosionbig.at(srcTile);

        Effect.shake(6f, 16f, x, y);
        Fx.nuclearShockwave.at(x, y);
        for(int i = 0; i < 6; i++){
            Time.run(Mathf.random(40), () -> Fx.nuclearcloud.at(x, y));
        }

        Damage.damage(x, y, explosionRadius * tilesize, explosionDamage * 4);

        for(int i = 0; i < 20; i++){
            Time.run(Mathf.random(50), () -> {
                tr.rnd(Mathf.random(40f));
                Fx.explosion.at(tr.x + x, tr.y + y);
            });
        }

        for(int i = 0; i < 70; i++){
            Time.run(Mathf.random(80), () -> {
                tr.rnd(Mathf.random(120f));
                Fx.nuclearsmoke.at(tr.x + x, tr.y + y);
            });
        }
    }

    private static void ChangeLanguage(Tile srcTile)
    {
        if (Core.settings.get("locale", Locale.class).equals(Locale.FRENCH))
            triggerRandomEvent(srcTile);
        showMessage("You are going to France");
        Core.settings.put("locale", Locale.FRENCH.toString());
    }

    private static void SpawnSpreadBulletCircle(Tile srcTile)
    {
        showMessage("Kaboom");
        srcTile.remove();
        float bulletVelMin = 0.5f;
        float bulletVelMax = 5f;
        int w = world.unitWidth();
        int h = world.unitHeight();
        for (int i = 0; i < 360; i++)
        {
            Bullets.standardCopper.create(state.rules.waveTeam.core(), state.rules.waveTeam, srcTile.getX(), srcTile.getY(), i, bulletVelMin + (bulletVelMax - bulletVelMin)*random.nextFloat()).add();
        }
    }

    private static void spawnFlyingUnit(Unit unit)
    {
        // Shamelessly copied from WaveSpawner.java
        unit.rotation = unit.angleTo(world.width()/2f * tilesize, world.height()/2f * tilesize);
        unit.apply(StatusEffects.unmoving, 30f);
        unit.add();
        Call.spawnEffect(unit.x, unit.y, unit.rotation, unit.type);
    }

    private static void showMessage(String message)
    {
        Vars.ui.hudfrag.showToast(message);
    }
}