package com.magic.investor_api.controller;

import com.magic.investor_api.Scheduler.SchedulerTask;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final SchedulerTask scheduledTask;

    public AdminController(SchedulerTask scheduledTask) {
        this.scheduledTask = scheduledTask;
    }

    @PostMapping("/updateDB")
    public ResponseEntity<String> forceUpdate() throws IOException {
        scheduledTask.updateBBDD();
        return ResponseEntity.ok("Actualización iniciada");
    }

    // Limpiar de la tabla las expansiones que no tienen cartas
    @PostMapping("clean/expansion")
        public ResponseEntity<String> cleanExpansion(){

        return ResponseEntity.ok("Expansiones actualizadas.");
        }

}