package edu.eci.arsw.blueprints.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Blueprint {

    private String author = null;
    private List<Point> points = null;
    private String name = null;

    public Blueprint(String author, String name, Point[] pnts) {
        this.author = author;
        this.name = name;
        // Cambio importante: crear una nueva lista mutable
        points = new ArrayList<>(Arrays.asList(pnts));
    }

    public Blueprint(String author, String name) {
        this.author = author;
        this.name = name;
        points = new ArrayList<>();
    }

    public Blueprint() {
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public void addPoint(Point p) {
        this.points.add(p);
    }

    @Override
    public String toString() {
        return "Blueprint{" + "author=" + author + ", name=" + name +
                ", points=" + points.size() + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this.author);
        hash = 31 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Blueprint other = (Blueprint) obj;
        if (!Objects.equals(this.author, other.author)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (this.points.size() != other.points.size()) {
            return false;
        }
        for (int i = 0; i < this.points.size(); i++) {
            Point thisPoint = this.points.get(i);
            Point otherPoint = other.points.get(i);
            if (thisPoint.getX() != otherPoint.getX() || thisPoint.getY() != otherPoint.getY()) {
                return false;
            }
        }
        return true;
    }
}