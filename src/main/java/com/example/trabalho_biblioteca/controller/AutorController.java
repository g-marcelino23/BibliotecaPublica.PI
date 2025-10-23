package com.example.trabalho_biblioteca.controller;

import com.example.trabalho_biblioteca.dto.AutorDTO;
import com.example.trabalho_biblioteca.model.Autor;
import com.example.trabalho_biblioteca.service.AutorService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("api/autor")
public class AutorController {
    @Autowired
    AutorService autorService;

    @PostMapping()
    public ResponseEntity<Autor> saveaAutor(@RequestParam MultipartFile capa, @RequestParam String nome, @RequestParam String biografia){
        Autor autorSalvo = null;
        ResponseEntity<Autor> response = null;
        try{
            autorSalvo = autorService.saveAutor(capa, nome, biografia);
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

    @PutMapping("/{idAutor}")
    public ResponseEntity<Autor> updateAutor(@RequestParam(required = false) String nome, @RequestParam(required = false) String biografia, @RequestParam(required = false) MultipartFile capa , @PathVariable Long idAutor){
        return ResponseEntity.ok().body(autorService.updateAutor(capa, nome, biografia, idAutor));
    }

    @GetMapping("/capa/{nome}")
    public ResponseEntity<Resource> getCapaLivro(@PathVariable String nome) {
        try {
            Resource capaResource = autorService.carregarCapaComoResource(nome);
            if (capaResource == null || !capaResource.exists() || !capaResource.isReadable()) {
                return ResponseEntity.notFound().build();
            }
            String contentType = null;
            try {
                Path capaPath = capaResource.getFile().toPath();
                contentType = Files.probeContentType(capaPath);
            } catch (IOException e) {
                String filename = capaResource.getFilename();
                if (filename != null) {
                    if (filename.toLowerCase().endsWith(".png")) contentType = MediaType.IMAGE_PNG_VALUE;
                    else if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")) contentType = MediaType.IMAGE_JPEG_VALUE;
                    else if (filename.toLowerCase().endsWith(".gif")) contentType = MediaType.IMAGE_GIF_VALUE;
                }
            }
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + capaResource.getFilename() + "\"")
                    .body(capaResource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
