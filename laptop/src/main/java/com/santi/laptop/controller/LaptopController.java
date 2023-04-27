package com.santi.laptop.controller;

import com.santi.laptop.entity.Laptop;
import com.santi.laptop.repository.LaptopRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
public class LaptopController {
    private LaptopRepository laptopRepository;
    public LaptopController (LaptopRepository laptopRepository){
        this.laptopRepository = laptopRepository;
    }
    @Operation(
            summary = "Get all laptops",
            description = "Get all laptops from database."
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status 200 OK"
    )
    @GetMapping("/api/laptops")
    public ResponseEntity<List<Laptop>> findAll(){
        try{
            List<Laptop> result = laptopRepository.findAll();
            return ResponseEntity.ok(result);
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }
    @Operation(
            summary = "Get laptop by ID",
            description = "Get one laptop by ID from database."
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status 200 OK"
    )
    @GetMapping("/api/laptops/{id}")
    public ResponseEntity<Laptop> findOneById(@PathVariable Long id){
        try{
            Optional<Laptop> laptopOpt = laptopRepository.findById(id);
            if(laptopOpt.isEmpty()){
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(laptopOpt.get());
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }
    @Operation(
            summary = "Create laptop",
            description = "Create and save laptop into database"
    )
    @ApiResponse(
            responseCode = "201",
            description = "HTTP Status 201 CREATED"
    )
    @PostMapping("/api/laptops")
    public ResponseEntity<Laptop> createLaptop(@RequestBody Laptop laptop){
        try{
            Laptop result = laptopRepository.save(laptop);
            return ResponseEntity.created(new URI("/api/laptops/" + result.getId())).body(result);
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }
    @Operation(
            summary = "Update laptop",
            description = "Update laptop in database"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status 200 NO CONTENT"
    )
    @PutMapping("/api/laptops/{id}")
    public ResponseEntity<Laptop> updateLaptop(@PathVariable Long id, @RequestBody Laptop laptop){
        try{
            Optional<Laptop> laptopOptional=laptopRepository.findById(id);
            if (laptopOptional.isPresent()){
                Laptop existingLaptop = laptopOptional.get();
                if (laptop.getBrand() != null) {
                    existingLaptop.setBrand(laptop.getBrand());
                }
                if (laptop.getModel() != null) {
                    existingLaptop.setModel(laptop.getModel());
                }
                if (laptop.getPrice() != null) {
                    existingLaptop.setPrice(laptop.getPrice());
                }
                return ResponseEntity.ok(laptopRepository.save(existingLaptop));
            }
            return ResponseEntity.notFound().build();
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }
    @Operation(
            summary = "Delete laptop",
            description = "Delete laptop from database"
    )
    @ApiResponse(
            responseCode = "204",
            description = "HTTP Status 204 OK"
    )
    @DeleteMapping("/api/laptops/{id}")
    public ResponseEntity<Void> deleteLaptop(@PathVariable Long id){
        try {
            if(!laptopRepository.existsById(id)){
                return ResponseEntity.notFound().build();
            }
            laptopRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
