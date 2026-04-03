package com.italo.geradorboleto.controller;

import com.italo.geradorboleto.service.ExportService;
import com.italo.geradorboleto.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/export")
@RequiredArgsConstructor
@Tag(name = "Exportação", description = "Endpoints para exportação de dados em Excel")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@Slf4j
public class ExportController {

    private final ExportService exportService;
    private final JwtTokenProvider tokenProvider;

    private static final String EXCEL_MEDIA_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @Operation(summary = "Exportar Custos", description = "Exporta todos os custos com resumo para Excel")
    @GetMapping("/custos")
    public ResponseEntity<byte[]> exportarCustos(HttpServletRequest request) throws IOException {
        try {
            log.debug("Exporting custos");
            String token = extractTokenFromRequest(request);
            String userId = tokenProvider.getUserIdFromToken(token);
            log.debug("Exporting custos for userId: {}", userId);
            
            byte[] excelData = exportService.exportarCustos(userId);
            
            String filename = "custos_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(EXCEL_MEDIA_TYPE));
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(excelData.length);
            
            log.debug("Custos exported successfully for userId: {}", userId);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);
        } catch (Exception e) {
            log.error("Error exporting custos", e);
            throw e;
        }
    }

    @Operation(summary = "Exportar Faturamento", description = "Exporta todos os dados de faturamento para Excel")
    @GetMapping("/faturamento")
    public ResponseEntity<byte[]> exportarFaturamento(HttpServletRequest request) throws IOException {
        try {
            log.debug("Exporting faturamento");
            String token = extractTokenFromRequest(request);
            String userId = tokenProvider.getUserIdFromToken(token);
            log.debug("Exporting faturamento for userId: {}", userId);
            
            byte[] excelData = exportService.exportarFaturamento(userId);
            
            String filename = "faturamento_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(EXCEL_MEDIA_TYPE));
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(excelData.length);
            
            log.debug("Faturamento exported successfully for userId: {}", userId);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);
        } catch (Exception e) {
            log.error("Error exporting faturamento", e);
            throw e;
        }
    }

    @Operation(summary = "Exportar Preços", description = "Exporta todos os preços com cálculos para Excel")
    @GetMapping("/precos")
    public ResponseEntity<byte[]> exportarPrecos(HttpServletRequest request) throws IOException {
        try {
            log.debug("Exporting precos");
            String token = extractTokenFromRequest(request);
            String userId = tokenProvider.getUserIdFromToken(token);
            log.debug("Exporting precos for userId: {}", userId);
            
            byte[] excelData = exportService.exportarPrecos(userId);
            
            String filename = "precos_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(EXCEL_MEDIA_TYPE));
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(excelData.length);
            
            log.debug("Precos exported successfully for userId: {}", userId);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);
        } catch (Exception e) {
            log.error("Error exporting precos", e);
            throw e;
        }
    }

    @Operation(summary = "Exportar Completo", description = "Exporta todos os dados do sistema (custos, faturamento e preços) para Excel")
    @GetMapping("/completo")
    public ResponseEntity<byte[]> exportarCompleto(HttpServletRequest request) throws IOException {
        try {
            log.debug("Exporting completo");
            String token = extractTokenFromRequest(request);
            String userId = tokenProvider.getUserIdFromToken(token);
            log.debug("Exporting completo for userId: {}", userId);
            
            byte[] excelData = exportService.exportarCompleto(userId);
            
            String filename = "bpo_financeiro_completo_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(EXCEL_MEDIA_TYPE));
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(excelData.length);
            
            log.debug("Completo exported successfully for userId: {}", userId);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);
        } catch (Exception e) {
            log.error("Error exporting completo", e);
            throw e;
        }
    }

    @Operation(summary = "Exportar Teste", description = "Gera um arquivo Excel de teste para depuração")
    @GetMapping("/teste")
    public ResponseEntity<byte[]> exportarTeste() throws IOException {
        byte[] excelData = exportService.gerarExcelTeste();
        
        String filename = "teste_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(EXCEL_MEDIA_TYPE));
        headers.setContentDispositionFormData("attachment", filename);
        headers.setContentLength(excelData.length);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }

    @Operation(summary = "Exportar Único", description = "Exporta todos os dados em um único arquivo Excel (padrão)")
    @GetMapping("/excel")
    public ResponseEntity<byte[]> exportarExcelUnico(HttpServletRequest request) throws IOException {
        try {
            log.debug("Exporting excel unico");
            String token = extractTokenFromRequest(request);
            String userId = tokenProvider.getUserIdFromToken(token);
            log.debug("Exporting excel unico for userId: {}", userId);
            
            byte[] excelData = exportService.exportarCompleto(userId);
            
            String filename = "bpo_financeiro_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(EXCEL_MEDIA_TYPE));
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(excelData.length);
            
            log.debug("Excel unico exported successfully for userId: {}", userId);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);
        } catch (Exception e) {
            log.error("Error exporting excel unico", e);
            throw e;
        }
    }
    
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new RuntimeException("Token não encontrado");
    }
}
