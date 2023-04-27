package com.example.obrestdatajpa.controller;

import com.example.obrestdatajpa.entities.Book;
import com.example.obrestdatajpa.repository.BookRepository;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Optional;

@RestController
public class BookController {

    // atributos
    private BookRepository bookRepository;
    private final Logger log = LoggerFactory.getLogger(BookController.class);

    // contructores

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }


    // CRUD sobre la entidad Book

    // Buscar todos los libros (lista de libros)

    /**
     * http://localhost:8081/api/books
     * @return
     */

    @GetMapping("/api/books")
    public List<Book> findAll(){
        // recuperar y devolver los libros de base de datos
        return bookRepository.findAll();
    }


    /**
     * Request
     * Response
     * @param id
     * @return
     */
    // buscar un solo libro en base de datos segun su id
    @GetMapping("/api/books/{id}")
    @ApiOperation("Buscar un libro por clave primaria id Long")
    public ResponseEntity<Book> findOneById(@ApiParam("Clave primaria tipo Long") @PathVariable Long id){

        Optional<Book> bookOpt = bookRepository.findById(id); // 3456546456435
        // opcion 1
        if(bookOpt.isPresent())
            return ResponseEntity.ok(bookOpt.get());
        else
            return ResponseEntity.notFound().build();

        // opcion 2
//        return bookOpt.orElse(null);
        // return bookOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }



    // crear un nuevo libro en base de datos
    @PostMapping("/api/books")
    public ResponseEntity<Book> create(@RequestBody Book book, @RequestHeader HttpHeaders headers){
        System.out.println(headers.get("User-Agent"));
        if(book.getId() != null){
            log.warn("trying to create a book with id");
            return ResponseEntity.badRequest().build();
        }
        // guardar el libro recibido por parámetro en la base de datos
        Book result = bookRepository.save(book);
        return ResponseEntity.ok(result);
    }


    // actualizar un libro existente en base de datos
    @PutMapping("/api/books")
    public ResponseEntity<Book> update(@RequestBody Book book){
        if(book.getId() == null){
            log.warn("Trying to update a book without id");
            return ResponseEntity.badRequest().build();
        } else if (!bookRepository.existsById(book.getId())) {
            log.warn("Trying to update a non existing book id");
            return ResponseEntity.notFound().build();
        }
        // guardar el libro recibido por parámetro en la base de datos
        Book result = bookRepository.save(book);
        return ResponseEntity.ok(result);
    }
    // borrar libros en base de datos
    @ApiIgnore
    @DeleteMapping("/api/books")
    public ResponseEntity<Book> deleteAll(){
        bookRepository.deleteAll();
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/api/books/{id}")
    public ResponseEntity<Book> deleteOneById(@PathVariable Long id){
        if(!bookRepository.existsById(id)){
            log.warn("Trying to delete a non existent book");
            return ResponseEntity.notFound().build();
        }
        bookRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
