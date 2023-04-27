package com.example.obrestdatajpa.service;

import com.example.obrestdatajpa.entities.Book;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BookPriceCalculatorTest {

    @Test
    void calculatePrice() {
        //Configuracion de la prueba
        Book book = new Book(1L, "El senor de los anillos","Tokien",500,100.0, LocalDate.now(),false);
        BookPriceCalculator calculator = new BookPriceCalculator();
        //Se ejecuta el comportamiento a testear
        double price=calculator.calculatePrice(book);
        //Comprobaciones
        assertTrue(price>0);
    }
}