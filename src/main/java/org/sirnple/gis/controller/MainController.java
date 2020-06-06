package org.sirnple.gis.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by: sirnple
 * Created in: 2020-06-06
 * Description:
 */
@RestController
@RequestMapping("/hello_gis")
public class MainController {
    @GetMapping("")
    public String helloGis() {
        return "hello gis!";
    }
}
