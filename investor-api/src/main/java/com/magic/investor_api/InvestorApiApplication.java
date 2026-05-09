package com.magic.investor_api;

import com.magic.investor_api.dao.ExpansionDAO;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;

@SpringBootApplication
@EnableScheduling
public class
  InvestorApiApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(InvestorApiApplication.class, args);

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	}
}