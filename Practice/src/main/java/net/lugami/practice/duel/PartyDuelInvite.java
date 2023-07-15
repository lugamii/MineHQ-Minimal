package net.lugami.practice.duel;

import net.lugami.practice.kittype.KitType;
import net.lugami.practice.party.Party;

public final class PartyDuelInvite extends DuelInvite<Party> {

    public PartyDuelInvite(Party sender, Party target, KitType kitTypes) {
        super(sender, target, kitTypes);
    }

}