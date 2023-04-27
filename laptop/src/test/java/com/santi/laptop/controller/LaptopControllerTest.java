package com.santi.laptop.controller;

import com.santi.laptop.entity.Laptop;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LaptopControllerTest {
    private TestRestTemplate testRestTemplate;
    @Autowired
    private RestTemplateBuilder restTemplateBuilder;
    @LocalServerPort
    private int port;
    @BeforeEach
    void setUp() {
        restTemplateBuilder=restTemplateBuilder.rootUri("http://localhost:"+port);
        testRestTemplate=new TestRestTemplate(restTemplateBuilder);
    }
    @DisplayName("GET all laptops")
    @Test
    void findAll() {
        ResponseEntity<Laptop[]> response = testRestTemplate.getForEntity("/api/laptops",Laptop[].class);
        assertEquals(HttpStatus.OK,response.getStatusCode());
    }

    @DisplayName("GET one laptop by ID")
    @Test
    void findOneById() {
        ResponseEntity<Laptop> response=testRestTemplate.getForEntity("/api/books/12353", Laptop.class );
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }

    @DisplayName("POST laptop")
    @Test
    void createLaptop() {
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        String json = """
                {
                    "brand":"asusS",
                    "model":"nada",
                    "price":3
                }
                """;
        HttpEntity<String> request = new HttpEntity<>(json,headers);
        ResponseEntity<Laptop> response=testRestTemplate.exchange("/api/laptops",HttpMethod.POST,request,Laptop.class);
        Laptop result = response.getBody();
        assertEquals("asusS",result.getBrand());
    }

    @Test
    void updateLaptop() {
        // Arrange
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        String json = """
                {
                    "brand":"asus",
                    "model":"feo",
                    "price":300.0
                }
                """;
        HttpEntity<String> request = new HttpEntity<>(json,headers);
        Laptop savedLaptop=testRestTemplate.exchange("/api/laptops",HttpMethod.POST,request,Laptop.class).getBody();

        // Act
        ResponseEntity<Laptop> response = testRestTemplate.exchange("/api/laptops/{id}",
                HttpMethod.PUT,
                request,
                Laptop.class,
                savedLaptop.getId());

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Laptop updatedLaptop = response.getBody();
        assertEquals(savedLaptop.getId(), updatedLaptop.getId());
        assertEquals(savedLaptop.getBrand(), updatedLaptop.getBrand());
        assertEquals(savedLaptop.getModel(), updatedLaptop.getModel());
        assertEquals(savedLaptop.getPrice(), updatedLaptop.getPrice());
    }

    @Test
    void deleteLaptop() {
        // Se crea un nuevo laptop para eliminar
        Laptop laptopToDelete = new Laptop(0L,"Dell", "Inspiron", 500.0);
        Laptop result = testRestTemplate.postForObject("/api/laptops", laptopToDelete, Laptop.class);

        // Se verifica que el recurso haya sido creado exitosamente
        assertNotNull(result.getId());

        // Se realiza una solicitud GET para verificar si el recurso existe
        ResponseEntity<Laptop> getResponse = testRestTemplate.getForEntity("/api/laptops/" + result.getId(), Laptop.class);

        // Se verifica que el recurso exista antes de eliminarlo
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        // Se envía una solicitud DELETE para eliminar el recurso
        ResponseEntity<Void> deleteResponse = testRestTemplate.exchange("/api/laptops/" + result.getId(), HttpMethod.DELETE, null, Void.class);

        // Se verifica que el recurso haya sido eliminado exitosamente
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // Se realiza una solicitud GET para verificar que el recurso haya sido eliminado
        ResponseEntity<Laptop> getResponseAfterDelete = testRestTemplate.getForEntity("/api/laptops/" + result.getId(), Laptop.class);

        // Se verifica que el recurso no exista después de ser eliminado
        assertEquals(HttpStatus.NOT_FOUND, getResponseAfterDelete.getStatusCode());
    }
}