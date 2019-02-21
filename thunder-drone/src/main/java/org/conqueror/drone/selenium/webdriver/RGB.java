package org.conqueror.drone.selenium.webdriver;


public class RGB {

    private int r;
    private int g;
    private int b;

    public RGB(String color) {
        try {
            int sidx = color.indexOf('(') + 1;
            int eidx = color.indexOf(',');
            r = Integer.parseInt(color.substring(sidx, eidx).trim());

            sidx = eidx + 1;
            eidx = color.indexOf(',', eidx + 1);
            g = Integer.parseInt(color.substring(sidx, eidx).trim());

            sidx = eidx + 1;
            eidx = color.indexOf(',', eidx + 1);
            b = Integer.parseInt(color.substring(sidx, eidx).trim());
        } catch (NumberFormatException e) {
            r = -1;
            g = -1;
            b = -1;
        }
    }


    public RGB(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public void add(RGB rgb) {
        this.r += rgb.getR();
        this.g += rgb.getG();
        this.b += rgb.getB();
    }

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }

    public static double distance(RGB rgb1, RGB rgb2) {
        return Math.sqrt(Math.pow(rgb1.getR() - rgb2.getR(), 2d) + Math.pow(rgb1.getG() - rgb2.getG(), 2d) + Math.pow(rgb1.getB() - rgb2.getB(), 2d));
    }

    @Override
    public boolean equals(Object rgb) {
        return rgb instanceof RGB && ((RGB) rgb).getR() == getR() && ((RGB) rgb).getG() == getG() && ((RGB) rgb).getB() == getB();
    }

}
