/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blueprints.controllers;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.impl.Tuple;
import edu.eci.arsw.blueprints.services.BlueprintsServices;
import org.apache.catalina.connector.Response;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/api")
public class BlueprintAPIController {

    private final BlueprintsServices services;

    public BlueprintAPIController(BlueprintsServices services){
        this.services = services;
    }

    @GetMapping("/blueprints")
    public ResponseEntity<?> getAllBlueprints() {
        try {
            Set<Blueprint> blueprints = services.getAllBlueprints();
            return new ResponseEntity<>(blueprints, HttpStatus.ACCEPTED);
        } catch (Exception ex) {
            Logger.getLogger(BlueprintAPIController.class.getName()).log(Level.SEVERE, "Don't found blueprints in persistence", ex);
            return new ResponseEntity<>("Error getting blueprints", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/blueprints/{author}")
    public ResponseEntity<?> getBluprintByAuthor(@PathVariable("author") String author){
        try {
            Set<Blueprint> blueprints = services.getBlueprintsByAuthor(author);
            return new ResponseEntity<>(blueprints, HttpStatus.ACCEPTED);
        } catch (Exception ex) {
            Logger.getLogger(BlueprintAPIController.class.getName()).log(Level.SEVERE, "Don't found blueprints with that author", ex);
            return  new ResponseEntity<>("Error getting blueprints", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/blueprints/{author}/{bpname}")
    public ResponseEntity<?> getBlueprint(@PathVariable("author") String author, @PathVariable("bpname") String bpname){
        try {
            Blueprint blueprint = services.getBlueprint(author, bpname);
            return new ResponseEntity<>(blueprint, HttpStatus.ACCEPTED);
        } catch (Exception ex) {
            Logger.getLogger(BlueprintAPIController.class.getName()).log(Level.SEVERE, "Don't found blueprints with that author and name", ex);
            return  new ResponseEntity<>("Error getting blueprints", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/blueprints")
    public ResponseEntity<?> addBlueprint(@RequestBody Blueprint blueprint){
        if (services.containBlueprint(blueprint)) return new ResponseEntity<>("Error blueprint is already in persistence", HttpStatus.FORBIDDEN);
        services.addNewBlueprint(blueprint);
        return new ResponseEntity<>(blueprint, HttpStatus.ACCEPTED);
    }

    @PutMapping("/blueprints/{author}/{bpname}")
    public ResponseEntity<?> updateBlueprint(@PathVariable("author") String author,
                                             @PathVariable("bpname") String bpname,
                                             @RequestBody Blueprint updatedBlueprint){
        try {
            Blueprint bp = services.getBlueprint(author, bpname);
            services.updateBlueprint(author, bpname, updatedBlueprint);
            return new ResponseEntity<>(updatedBlueprint, HttpStatus.ACCEPTED);
        } catch (BlueprintNotFoundException ex) {
            return new ResponseEntity<>("Blueprint not found", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/blueprints/{author}/{bpname}")
    public ResponseEntity<?> deleteBlueprint(@PathVariable("author") String author,
                                             @PathVariable("bpname") String bpname) {
        try {
            services.deleteBlueprint(author, bpname);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (BlueprintNotFoundException ex) {
            return new ResponseEntity<>("Blueprint not found", HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            Logger.getLogger(BlueprintAPIController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("Error deleting blueprint", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}

