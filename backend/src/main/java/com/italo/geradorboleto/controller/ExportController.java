import com.italo.geradorboleto.service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
@Tag(name = "Exportação", description = "Endpoints para exportação de dados em Excel")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class ExportController {

    private final ExportService exportService;

    private static final String EXCEL_MEDIA_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @Operation(summary = "Exportar Custos", description = "Exporta todos os custos com resumo para Excel")
    @GetMapping("/custos")
    public ResponseEntity<byte[]> exportarCustos(@RequestParam String userId) throws IOException {
        byte[] excelData = exportService.exportarCustos(userId);
        
        String filename = "custos_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(EXCEL_MEDIA_TYPE));
        headers.setContentDispositionFormData("attachment", filename);
        headers.setContentLength(excelData.length);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }

    @Operation(summary = "Exportar Faturamento", description = "Exporta todos os dados de faturamento para Excel")
    @GetMapping("/faturamento")
    public ResponseEntity<byte[]> exportarFaturamento(@RequestParam String userId) throws IOException {
        byte[] excelData = exportService.exportarFaturamento(userId);
        
        String filename = "faturamento_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(EXCEL_MEDIA_TYPE));
        headers.setContentDispositionFormData("attachment", filename);
        headers.setContentLength(excelData.length);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }

    @Operation(summary = "Exportar Preços", description = "Exporta todos os preços com cálculos para Excel")
    @GetMapping("/precos")
    public ResponseEntity<byte[]> exportarPrecos(@RequestParam String userId) throws IOException {
        byte[] excelData = exportService.exportarPrecos(userId);
        
        String filename = "precos_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(EXCEL_MEDIA_TYPE));
        headers.setContentDispositionFormData("attachment", filename);
        headers.setContentLength(excelData.length);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }

    @Operation(summary = "Exportar Completo", description = "Exporta todos os dados do sistema (custos, faturamento e preços) para Excel")
    @GetMapping("/completo")
    public ResponseEntity<byte[]> exportarCompleto(@RequestParam String userId) throws IOException {
        byte[] excelData = exportService.exportarCompleto(userId);
        
        String filename = "bpo_financeiro_completo_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(EXCEL_MEDIA_TYPE));
        headers.setContentDispositionFormData("attachment", filename);
        headers.setContentLength(excelData.length);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
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
    public ResponseEntity<byte[]> exportarExcelUnico(@RequestParam String userId) throws IOException {
        byte[] excelData = exportService.exportarCompleto(userId);
        
        String filename = "bpo_financeiro_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(EXCEL_MEDIA_TYPE));
        headers.setContentDispositionFormData("attachment", filename);
        headers.setContentLength(excelData.length);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }
}
