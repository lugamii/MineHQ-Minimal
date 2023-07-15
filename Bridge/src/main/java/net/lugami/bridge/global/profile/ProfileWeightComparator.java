package net.lugami.bridge.global.profile;

import java.util.Comparator;

public class ProfileWeightComparator implements Comparator<Profile> {

    @Override
    public int compare(Profile profile,Profile otherProfile) {
        return Integer.compare(profile.getCurrentGrant().getRank().getPriority(),otherProfile.getCurrentGrant().getRank().getPriority());
    }

}
