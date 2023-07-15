package net.lugami.practice.kit.listener;

import net.lugami.practice.kit.menu.kits.KitsMenu;
import net.lugami.practice.kittype.menu.select.SelectKitTypeMenu;
import net.lugami.practice.Practice;
import net.lugami.practice.kit.KitItems;
import net.lugami.practice.lobby.LobbyHandler;
import net.lugami.practice.util.ItemListener;

public final class KitItemListener extends ItemListener {

    public KitItemListener() {
        addHandler(KitItems.OPEN_EDITOR_ITEM, player -> {
            LobbyHandler LobbyHandler = Practice.getInstance().getLobbyHandler();

            if (LobbyHandler.isInLobby(player)) {
                new SelectKitTypeMenu(kitType -> {
                    new KitsMenu(kitType).openMenu(player);
                }, "Select a kit to edit...").openMenu(player);
            }
        });
    }

}