package com.magic.investor_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync // Esto le dice a Spring: "Busca métodos anotados con @Async y ejecútalos en hilos separados"
public class AsyncConfig {
}