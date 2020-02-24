package com.abclinic.server.controller;

import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author tmduc
 * @package com.abclinic.server.controller
 * @created 2/14/2020 2:57 PM
 */
public class HomeController {
    @GetMapping("/")
    public String index() {
        return "Hello there! I'm running.";
    }
}
