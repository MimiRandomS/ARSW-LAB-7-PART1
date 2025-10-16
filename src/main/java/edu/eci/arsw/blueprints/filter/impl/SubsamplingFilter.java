package edu.eci.arsw.blueprints.filter.impl;

import edu.eci.arsw.blueprints.filter.BlueprintFilter;
import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Filtro que remueve 1 de cada 2 puntos del plano de manera intercalada
 */
// @Component("subsamplingFilter") // Descomentar para usar
public class SubsamplingFilter implements BlueprintFilter {

    @Override
    public Blueprint filter(Blueprint blueprint) {
        List<Point> originalPoints = blueprint.getPoints();
        List<Point> filteredPoints = new ArrayList<>();

        if (originalPoints == null || originalPoints.isEmpty()) {
            return blueprint;
        }

        for (int i = 0; i < originalPoints.size(); i += 2) {
            filteredPoints.add(originalPoints.get(i));
        }

        Point[] filteredArray = filteredPoints.toArray(new Point[filteredPoints.size()]);
        return new Blueprint(blueprint.getAuthor(), blueprint.getName(), filteredArray);
    }
}