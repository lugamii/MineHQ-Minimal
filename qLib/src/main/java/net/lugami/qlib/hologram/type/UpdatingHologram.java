package net.lugami.qlib.hologram.type;

import net.lugami.qlib.hologram.builder.UpdatingHologramBuilder;
import net.lugami.qlib.hologram.construct.Hologram;
import net.lugami.qlib.qLib;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Consumer;

public final class UpdatingHologram extends BaseHologram {

	private long interval;

	private Consumer<Hologram> updateFunction;
	private boolean showing = false;

	public UpdatingHologram(UpdatingHologramBuilder builder) {
		super(builder);

		this.interval = builder.getInterval();
		this.updateFunction = builder.getUpdateFunction();
	}


	public void send() {

		if (this.showing) {
			this.update();
			return;
		}

		super.send();

		this.showing = true;

		qLib.getInstance().getServer().getScheduler().runTaskTimerAsynchronously(qLib.getInstance(),new BukkitRunnable() {
			@Override
			public void run() {

				if (showing) {
					update();
				} else {
					cancel();
				}

			}
		},0L,this.interval*20L);

	}

	public void destroy() {
		super.destroy();
		this.showing = false;
	}

	public void update() {
		this.updateFunction.accept(this);
		super.update();
	}

}
