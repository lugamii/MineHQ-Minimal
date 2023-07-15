package net.lugami.knockback;

public interface Knockback {

    String getName();

    double getFriction();

    void setFriction(double friction);

    double getHorizontal();

    void setHorizontal(double horizontal);

    double getVertical();

    void setVertical(double vertical);

    double getVerticalLimit();

    void setVerticalLimit(double verticalLimit);

    double getExtraHorizontal();

    void setExtraHorizontal(double extraHorizontal);

    double getExtraVertical();

    void setExtraVertical(double extraVertical);

    void setWTap(final boolean p0);

    boolean isAutoWTap();

    void setKohi(final boolean p0);

    boolean isKohi();
}
