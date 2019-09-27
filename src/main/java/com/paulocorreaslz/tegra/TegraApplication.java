package com.paulocorreaslz.tegra;

/**
 * @author Paulo Correa <pauloyaco@gmail.com> - 2019
 *
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@SpringBootApplication
public class TegraApplication  {

	@RequestMapping("/")
    @ResponseBody
    String home() {
      return "Super Voos API!";
    }
	
	public static void main(String[] args) {
		SpringApplication.run(TegraApplication.class, args);
	}
}
