package com.magic.investor_api;

import com.magic.investor_api.model.Card;
import com.magic.investor_api.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class
  InvestorApiApplication {

	public static void main(String[] args)  {
		SpringApplication.run(InvestorApiApplication.class, args);
	}
}
