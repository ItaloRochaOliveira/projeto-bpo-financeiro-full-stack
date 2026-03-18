package com.italo.geradorboleto.controller;

import com.italo.geradorboleto.dto.BoletoRequest;
import com.italo.geradorboleto.dto.BoletoResponse;
import com.italo.geradorboleto.service.BoletoService;
import com.italo.geradorboleto.service.PdfService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/boleto")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BoletoController {

    @Autowired
    private BoletoService boletoService;

    @Autowired
    private PdfService pdfService;

    @GetMapping
    public ResponseEntity<?> getBoletos() {
        try {
            String userEmail = getCurrentUserEmail();
            List<BoletoResponse> boletos = boletoService.getBoletosByUser(userEmail);
            return ResponseEntity.ok(boletos);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createBoleto(@Valid @RequestBody BoletoRequest boletoRequest) {
        try {
            String userEmail = getCurrentUserEmail();
            BoletoResponse boleto = boletoService.createBoleto(boletoRequest, userEmail);
            return ResponseEntity.ok(boleto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBoleto(@PathVariable String id) {
        try {
            String userEmail = getCurrentUserEmail();
            BoletoResponse boleto = boletoService.getBoletoById(id, userEmail);
            return ResponseEntity.ok(boleto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/pdf")
    public void generateBoletoPdf(@PathVariable String id, HttpServletResponse response) throws IOException {
        try {
            String userEmail = getCurrentUserEmail();
            BoletoResponse boleto = boletoService.getBoletoById(id, userEmail);
            
            byte[] pdfContent = pdfService.generateBoletoPdf(boleto);
            
            response.setContentType(MediaType.APPLICATION_PDF_VALUE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, 
                "attachment; filename=\"boleto_" + boleto.getId() + ".pdf\"");
            response.setContentLength(pdfContent.length);
            
            response.getOutputStream().write(pdfContent);
            response.getOutputStream().flush();
        } catch (RuntimeException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(e.getMessage());
        }
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userDetails.getUsername();
        }
        throw new RuntimeException("Usuário não autenticado");
    }
}
