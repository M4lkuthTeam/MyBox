package mara.mybox.data;

/**
 * @Author Mara
 * @CreateDate 2018-11-11 12:29:29
 * @Version 1.0
 * @Description
 * @License Apache License Version 2.0
 */
public class IntCircle {

    private int centerX, centerY, radius, radius2;

    public IntCircle() {

    }

    public IntCircle(int x, int y, int r) {
        centerX = x;
        centerY = y;
        radius = r;
        radius2 = r * r;
    }

    public IntCircle cloneValues() {
        return new IntCircle(centerX, centerY, radius);
    }

    public boolean isValid() {
        return radius > 0;
    }

    public boolean include(int x, int y) {
        int distanceX = centerX - x;
        int distaneY = centerY - y;
        return distanceX * distanceX + distaneY * distaneY <= radius2;
    }

    public int getCenterX() {
        return centerX;
    }

    public void setCenterX(int centerX) {
        this.centerX = centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public void setCenterY(int centerY) {
        this.centerY = centerY;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        radius2 = radius * radius;
        this.radius = radius;
    }

}
