package com.paulocorreaslz.tegra.controller;

/*
*
* Criado por Paulo Correa <pauloyaco@gmail.com> - 2019
*
*/

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = {"http://localhost","*"})
@RestController
@RequestMapping("/api")
public class FlightController {
 
  @GetMapping("/online")
  public String online() {
	  return "online";
  }
 
}