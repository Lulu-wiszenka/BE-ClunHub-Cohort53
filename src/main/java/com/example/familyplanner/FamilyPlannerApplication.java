package com.example.familyplanner;

import com.example.familyplanner.Security.JWT.JwtCore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FamilyPlannerApplication {

	private JwtCore jwtCore;

	@Autowired
	public void setJwtCore(JwtCore jwtCore) {
		this.jwtCore = jwtCore;
	}

	public static void main(String[] args) {
		SpringApplication.run(FamilyPlannerApplication.class, args);
	}

}
