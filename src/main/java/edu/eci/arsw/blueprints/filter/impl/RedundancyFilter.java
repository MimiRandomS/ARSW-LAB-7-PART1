package edu.eci.arsw.blueprints.filter.impl;

import edu.eci.arsw.blueprints.filter.BlueprintFilter;
import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Filtro que remueve puntos consecutivos redundantes (repetidos)
 */
@Component("redundancyFilter") // Comentar/Descomentar para usar
public class RedundancyFilter implements BlueprintFilter {

    @Override
    public Blueprint filter(Blueprint blueprint) {
        List<Point> originalPoints = blueprint.getPoints();
        List<Point> filteredPoints = new ArrayList<>();

        if (originalPoints == null || originalPoints.isEmpty()) {
            return blueprint;
        }

        filteredPoints.add(originalPoints.get(0));

        for (int i = 1; i < originalPoints.size(); i++) {
            Point currentPoint = originalPoints.get(i);
            Point previousPoint = originalPoints.get(i - 1);

            if (currentPoint.getX() != previousPoint.getX() ||
                    currentPoint.getY() != previousPoint.getY()) {
                filteredPoints.add(currentPoint);
            }
        }

        Point[] filteredArray = filteredPoints.toArray(new Point[filteredPoints.size()]);
        return new Blueprint(blueprint.getAuthor(), blueprint.getName(), filteredArray);
    }
}