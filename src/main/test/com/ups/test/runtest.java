package com.ups.test;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class) 
@CucumberOptions(
		plugin = {"json:target/cucumber.json", "pretty","html:target/cucumber"
		        },
				features = {"features"},
				//tags = {"@output"},
				//glue = {"com.webbdd.stepdefinitions"},
				monochrome = true
		) 
public class runtest { 
		
	}


