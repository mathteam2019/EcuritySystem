package com.haomibo.haomibo.controllers;

import com.haomibo.haomibo.models.db.Test;
import com.haomibo.haomibo.repositories.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    TestRepository repository;

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public Object index() {
        return repository.findAll();
    }

}
