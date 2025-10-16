package com.example.trabalho_biblioteca.controller;

import com.example.trabalho_biblioteca.dto.AutorDTO;
import com.example.trabalho_biblioteca.model.Autor;
import com.example.trabalho_biblioteca.service.AutorService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/autor")
public class AutorController {
    @Autowired
    AutorService autorService;

    @PostMapping()
    public ResponseEntity<Autor> saveaAutor(@RequestBody Autor autor){
        Autor autorSalvo = null;
        ResponseEntity<Autor> response = null;
        try{
            autorSalvo = autorService.saveAutor(autor);
        }catch (RuntimeException e){
            e.printStackTrace();
            response=  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        if(autorSalvo != null){
            response =  ResponseEntity.status(HttpStatus.CREATED).body(autorSalvo);
        }
        return response;
    }

    @GetMapping()
    public ResponseEntity<List<Autor>> getAutores(){
        ResponseEntity response = null;
        List<Autor> autores = null;
        try{
            autores = autorService.getAutores();
        }catch (RuntimeException e){
            e.printStackTrace();
            response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            return response;
        }
        if(autores.isEmpty()){
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            return response;
        }else{
            response = ResponseEntity.status(HttpStatus.OK).body(autores);
        }
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Autor> getAutorById(@PathVariable Long id){
        Autor autorRecuperado = autorService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(autorRecuperado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteById(@PathVariable Long id){
        return autorService.deleteById(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Autor> updateAutor(@RequestBody AutorDTO autorDTO, @PathVariable Long id){
        return ResponseEntity.ok().body(autorService.updateAutor(autorDTO, id));
    }
}
