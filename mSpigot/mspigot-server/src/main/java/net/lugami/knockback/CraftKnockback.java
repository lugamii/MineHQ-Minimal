package net.lugami.knockback;

public class CraftKnockback implements Knockback {

    private String name;
    private double friction;
    private double horizontal;
    private double vertical;
    private double verticalLimit;
    private double extraHorizontal;
    private double extraVertical;
    private boolean wtap;
    private boolean kohi;

    public CraftKnockback(String name) {
        this.friction = 2.0;
        this.horizontal = 0.35;
        this.vertical = 0.35;
        this.verticalLimit = 0.4000000059604645;
        this.extraHorizontal = 0.425;
        this.extraVertical = 0.085;
        this.wtap = false;
        this.kohi = false;
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public double getFriction() {
        return this.friction;
    }

    @Override
    public void setFriction(final double friction) {
        this.friction = friction;
    }

    @Override
    public double getHorizontal() {
        return this.horizontal;
    }

    @Override
    public void setHorizontal(final double horizontal) {
        this.horizontal = horizontal;
    }

    @Override
    public double getVertical() {
        return this.vertical;
    }

    @Override
    public void setVertical(final double vertical) {
        this.vertical = vertical;
    }

    @Override
    public double getVerticalLimit() {
        return this.verticalLimit;
    }

    @Override
    public void setVerticalLimit(final double verticalLimit) {
        this.verticalLimit = verticalLimit;
    }

    @Override
    public double getExtraHorizontal() {
        return this.extraHorizontal;
    }

    @Override
    public void setExtraHorizontal(final double extraHorizontal) {
        this.extraHorizontal = extraHorizontal;
    }

    @Override
    public double getExtraVertical() {
        return this.extraVertical;
    }

    @Override
    public void setExtraVertical(final double extraVertical) {
        this.extraVertical = extraVertical;
    }

    @Override
    public void setWTap(final boolean wtap) {
        this.wtap = wtap;
    }

    @Override
    public boolean isAutoWTap() {
        return this.wtap;
    }

    @Override
    public void setKohi(final boolean kohi) {
        this.kohi = kohi;
    }

    @Override
    public boolean isKohi() {
        return this.kohi;
    }
}
