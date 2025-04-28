package com.example.demo.controllers;

import com.example.demo.models.Usuario;
import com.example.demo.repositories.UsuarioRepository;
import com.example.demo.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuariosController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;  // Inyecta el repositorio para verificar el email

    @GetMapping()
    public ArrayList<Usuario> obtenerUsuarios() {
        return usuarioService.obtenerUsuarios();
    }

    @PostMapping()
    public ResponseEntity<String> guardarUsuario(@RequestBody Usuario usuario) {
        // Validar nombre
        if (usuario.getNombre() == null || usuario.getNombre().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El nombre es obligatorio.");
        }

        // Validar email
        if (usuario.getEmail() == null || usuario.getEmail().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El email es obligatorio.");
        }

        // Validar formato del email
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        if (!usuario.getEmail().matches(emailRegex)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El email no tiene un formato válido.");
        }

        // Verificar si el email ya está registrado
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(usuario.getEmail());
        if (usuarioExistente.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El email ya está registrado.");
        }

        // Establecer la fecha de registro
        usuario.setFechaRegistro(new Date());

        // Guardar el usuario
        usuarioService.guardarUsuario(usuario);

        return ResponseEntity.status(HttpStatus.CREATED).body("Usuario creado exitosamente.");
    }

    @GetMapping(path = "/{id}")
    public Optional<Usuario> obtenerUsuarioPorId(@PathVariable("id") Long id) {
        return this.usuarioService.obtenerUsuarioPorId(id);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable("id") Long id) {
        boolean ok = this.usuarioService.eliminarUsuario(id);
        if (ok) {
            return ResponseEntity.ok("El usuario se eliminó exitosamente.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se pudo eliminar el usuario.");
        }
    }
}
