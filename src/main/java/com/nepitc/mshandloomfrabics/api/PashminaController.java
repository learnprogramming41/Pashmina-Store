/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nepitc.mshandloomfrabics.api;

import com.nepitc.mshandloomfrabics.common.CloudinaryConfig;
import com.nepitc.mshandloomfrabics.entity.*;
import com.nepitc.mshandloomfrabics.service.*;
import com.nepitc.mshandloomfrabics.service.PashminaService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author Nishan Dhungana
 */
@Controller
@RequestMapping(value = "/admin-api")
public class PashminaController {

    public static int pashminaId = 0;
    private List<PashminaModel> pashmina = new ArrayList<>();

    @Autowired
    private PashminaService pashminaService;

    @Autowired
    private PashminaColorService pashminaColorService;

    @Autowired
    private DescriptionService descriptionService;

    @Autowired
    private ImageService imageService;

    @RequestMapping(value = "/add-pashmina", method = RequestMethod.POST)
    public ResponseEntity<String> insertPashmina(@RequestBody PashminaModel pashmina) {
        if (pashmina != null) {
            try {
                pashminaService.insert(pashmina);

                pashminaId = pashmina.getPashminaId();

                for (PashminaColourModel pash : pashmina.getPashminaColor()) {
                    pashminaColorService.insert(new PashminaColourModel(pash.getColor(), new PashminaModel(pashminaId)));
                }

                for (DescriptionModel desc : pashmina.getDescriptions()) {
                    descriptionService.insert(new DescriptionModel(desc.getPashminaDescription(), new PashminaModel(pashminaId)));
                }

                return new ResponseEntity(HttpStatus.OK);

            } catch (HibernateException e) {
                return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
    }

    @Async
    @RequestMapping(value = "/get-all-pashmina/{pageSize}/{pageNumber}", method = RequestMethod.GET)
    public ResponseEntity<List<PashminaModel>> getAllPashmina(final @PathVariable int pageSize, final @PathVariable int pageNumber) {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    pashmina = pashminaService.getAllPashmina(pageSize, pageNumber);
                }
            }).start();
            
            return new ResponseEntity(pashmina, HttpStatus.OK);
        } catch (HibernateException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/get-pashmina-count", method = RequestMethod.GET)
    public ResponseEntity<Long> getPashminaCount() {
        Long count = pashminaService.getPashminaCount();
        try {
            return new ResponseEntity(count, HttpStatus.OK);
        } catch (HibernateException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/delete-pashmina", method = RequestMethod.DELETE)
    public ResponseEntity deletePashmina(@RequestParam("pashminaId") int pashminaId) {
        if (pashminaId != 0) {
            try {
                List<String> list = imageService.deleteImageFromPashminaId(pashminaId);
                for (String s : list) {
                    CloudinaryConfig.deleteImage(s);
                }
                pashminaService.delete(new PashminaModel(pashminaId));
                return new ResponseEntity(HttpStatus.OK);
            } catch (HibernateException | IOException e) {
                return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity("Pashmina id is empty", HttpStatus.NO_CONTENT);
        }
    }

    @RequestMapping(value = "/get-pashmina-by-id/{pashminaId}", method = RequestMethod.GET)
    public ResponseEntity<PashminaModel> getPashminaById(@PathVariable int pashminaId) {
        try {
            return new ResponseEntity(pashminaService.getById(pashminaId), HttpStatus.OK);
        } catch (HibernateException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/search-pashmina", method = RequestMethod.GET)
    public ResponseEntity searchPashmina(@RequestParam("searchText") String searchText) {
        if (searchText.isEmpty() || searchText == null) {
            return new ResponseEntity("search text is empty", HttpStatus.NO_CONTENT);
        } else {
            try {
                return new ResponseEntity(pashminaService.searchPashmina(searchText), HttpStatus.OK);
            } catch (HibernateException e) {
                return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
    }
}
