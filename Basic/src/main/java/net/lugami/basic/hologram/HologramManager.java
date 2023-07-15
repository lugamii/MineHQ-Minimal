package net.lugami.basic.hologram;

import net.lugami.basic.Basic;
import net.lugami.qlib.hologram.FrozenHologramHandler;
import net.lugami.qlib.hologram.construct.Hologram;
import net.lugami.qlib.hologram.type.UpdatingHologram;
import net.lugami.qlib.qLib;
import java.beans.ConstructorProperties;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mkremins.fanciful.shaded.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import org.bukkit.Location;

public class HologramManager {

    private Map<Integer, Hologram> holograms;

    public HologramManager() {
        this.holograms = new HashMap<>();
        File file = new File(Basic.getInstance().getDataFolder(), "holograms.json");
        if (!file.exists()) {
            return;
        }
        try {
            List<SerializedHologram> loaded = qLib.GSON.fromJson(FileUtils.readFileToString(file), new TypeToken<List<SerializedHologram>>() {}.getType());
            for (SerializedHologram serialized : loaded) {
                Hologram hologram = FrozenHologramHandler.createHologram().at(serialized.location).addLines(serialized.lines).build();
                this.holograms.put(serialized.id, hologram);
                hologram.send();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int register(Hologram hologram) {
        if (hologram instanceof UpdatingHologram) {
            throw new IllegalArgumentException("We can only serialize static holograms.");
        }
        int nextId = this.createId();
        this.holograms.put(nextId, hologram);
        this.save();
        return nextId;
    }

    public void save() {
        ArrayList<SerializedHologram> toSerialize = new ArrayList<>();
        for (Map.Entry<Integer, Hologram> entry : this.holograms.entrySet()) {
            toSerialize.add(new SerializedHologram(entry.getKey(), entry.getValue().getLocation(), entry.getValue().getLines()));
        }
        File file = new File(Basic.getInstance().getDataFolder(), "holograms.json");
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileUtils.write(file, qLib.GSON.toJson(toSerialize));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int createId() {
        int id = this.holograms.size() + 1;
        while (this.holograms.get(id) != null) {
            ++id;
        }
        return id;
    }

    public void move(int id, Location location) {
        Hologram hologram = this.getHolograms().get(id);
        List lines = hologram.getLines();
        this.holograms.remove(id);
        hologram.destroy();
        this.save();
        Hologram newHologram = FrozenHologramHandler.createHologram().at(location).addLines(lines).build();
        newHologram.send();
        this.holograms.put(id, newHologram);
        this.save();
    }

    public Map<Integer, Hologram> getHolograms() {
        return this.holograms;
    }

    private static class SerializedHologram {
        int id;
        Location location;
        List<String> lines;

        @ConstructorProperties(value={"id", "location", "lines"})
        public SerializedHologram(int id, Location location, List<String> lines) {
            this.id = id;
            this.location = location;
            this.lines = lines;
        }
    }
}

