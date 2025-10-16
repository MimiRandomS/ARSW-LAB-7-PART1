/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blueprints.persistence.impl;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.persistence.BlueprintsPersistence;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author hcadavid
 */
@Repository
public class InMemoryBlueprintPersistence implements BlueprintsPersistence{

    private final ConcurrentHashMap<Tuple<String,String>,Blueprint> blueprints=new ConcurrentHashMap<>();

    public InMemoryBlueprintPersistence() {
        // Load stub data
        Point[] pts = new Point[] { new Point(140, 140), new Point(115, 115) };
        Blueprint bp = new Blueprint("_authorname_", "_bpname_", pts);
        blueprints.put(new Tuple<>(bp.getAuthor(), bp.getName()), bp);

        Point[] pts2 = new Point[] { new Point(140, 140), new Point(115, 115) };
        Blueprint bp2 = new Blueprint("Jorge", "plano 1", pts2);
        blueprints.put(new Tuple<>(bp2.getAuthor(), bp2.getName()), bp2);

        Point[] pts3 = new Point[] { new Point(140, 140), new Point(115, 115) };
        Blueprint bp3 = new Blueprint("Jorge", "plano 2", pts3);
        blueprints.put(new Tuple<>(bp3.getAuthor(), bp3.getName()), bp3);
    }
    
    @Override
    public void saveBlueprint(Blueprint bp) throws BlueprintPersistenceException {
        if (blueprints.containsKey(new Tuple<>(bp.getAuthor(),bp.getName()))){
            throw new BlueprintPersistenceException("The given blueprint already exists: "+bp);
        }
        else{
            blueprints.putIfAbsent(new Tuple<>(bp.getAuthor(),bp.getName()), bp);
        }        
    }

    @Override
    public Blueprint getBlueprint(String author, String bprintname) throws BlueprintNotFoundException {
        return blueprints.get(new Tuple<>(author, bprintname));
    }

    @Override
    public Set<Blueprint> getBlueprints() throws BlueprintNotFoundException {
        return new HashSet<>(blueprints.values());
    }

    @Override
    public Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException {
        Set<Blueprint> blueprintsByAuthor = new HashSet<>();

        for (Map.Entry<Tuple<String, String>, Blueprint> entry : blueprints.entrySet()) {
            Tuple<String, String> tuple = entry.getKey();
            if (tuple.getElem1().equals(author)) {
                blueprintsByAuthor.add(entry.getValue());
            }
        }

        if (blueprintsByAuthor.isEmpty()) {
            throw new BlueprintNotFoundException("No blueprints found for author: " + author);
        }

        return blueprintsByAuthor;
    }

    @Override
    public boolean containBlueprint(Blueprint blueprint) {
        return blueprints.containsKey(new Tuple<>(blueprint.getAuthor(), blueprint.getName()));
    }

    @Override
    public void updateBlueprint(String author, String name, Blueprint updatedBlueprint) throws BlueprintNotFoundException {
        Tuple<String,String> key = new Tuple<>(author, name);
        if (!blueprints.containsKey(key)) {
            throw new BlueprintNotFoundException("Blueprint not found: " + name + " by " + author);
        }
        blueprints.replace(key, updatedBlueprint);
    }

    @Override
    public void deleteBlueprint(String author, String name) throws BlueprintNotFoundException {
        Tuple<String, String> key = new Tuple<>(author, name);
        if (!blueprints.containsKey(key)) {
            throw new BlueprintNotFoundException("Blueprint not found: " + name + " by " + author);
        }
        blueprints.remove(key);
    }


}
