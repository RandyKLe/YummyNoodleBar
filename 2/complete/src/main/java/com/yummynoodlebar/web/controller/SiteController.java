package com.yummynoodlebar.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class SiteController {

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<String> getCurrentMenu() {

		return new ResponseEntity<>("Hello World", HttpStatus.FOUND);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/hw")
	@ResponseBody
	public String helloWorld() {

		return "Hello World baby";
	}

}
