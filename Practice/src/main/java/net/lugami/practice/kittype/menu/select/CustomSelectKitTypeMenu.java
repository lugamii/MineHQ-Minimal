package net.lugami.practice.kittype.menu.select;

import com.google.common.base.Preconditions;

import java.util.Map;
import java.util.function.BiFunction;

import net.lugami.qlib.menu.Button;
import net.lugami.qlib.menu.Menu;
import net.lugami.qlib.util.Callback;
import net.lugami.practice.kittype.KitType;
import net.lugami.practice.util.InventoryUtils;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Similar to {@link SelectKitTypeMenu} but allows the user to set custom
 * descriptions/item counts for each KitType. For example, this is used by
 * the queue system to show the number of players in each queue prior to joining.
 */
public final class CustomSelectKitTypeMenu extends Menu {

    private final Callback<KitType> callback;
    private final BiFunction<Player, KitType, CustomKitTypeMeta> metaFunc;
    private final boolean ranked;

    public CustomSelectKitTypeMenu(Callback<KitType> callback, BiFunction<Player, KitType, CustomKitTypeMeta> metaFunc, String title, boolean ranked) {
        super(title);

        setAutoUpdate(true);

        this.callback = Preconditions.checkNotNull(callback, "callback");
        this.metaFunc = Preconditions.checkNotNull(metaFunc, "metaFunc");
        this.ranked = ranked;
    }

    @Override
    public void onClose(Player player) {
        InventoryUtils.resetInventoryDelayed(player);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int index = 0;

        for (KitType kitType : KitType.getAllTypes()) {
            if (!player.isOp() && kitType.isHidden()) {
                continue;
            }

            if (ranked && !kitType.isSupportsRanked()) {
                continue;
            }

            CustomKitTypeMeta meta = metaFunc.apply(player, kitType);
            buttons.put(index++, new KitTypeButton(kitType, callback, meta.getDescription(), meta.getQuantity()));
        }

        return buttons;
    }

    @AllArgsConstructor
    public static final class CustomKitTypeMeta {

        @Getter private int quantity;
        @Getter private List<String> description;

    }

}