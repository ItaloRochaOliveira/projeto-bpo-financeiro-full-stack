package com.italo.geradorboleto.service;

import com.italo.geradorboleto.dto.CustoCompletoResponse;
import com.italo.geradorboleto.dto.CustoDetalheResponse;
import com.italo.geradorboleto.dto.CustosArrayResponse;
import com.italo.geradorboleto.dto.FaturamentoResponse;
import com.italo.geradorboleto.dto.PrecoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportService {

    private final CustoService custoService;
    private final FaturamentoService faturamentoService;
    private final PrecoService precoService;

    public byte[] exportarCustos(String userId) throws IOException {
        log.info("Iniciando exportação de custos para userId: {}", userId);
        
        CustosArrayResponse custosResponse = custoService.getAllCustos(userId);
        List<CustoCompletoResponse> custos = custosResponse != null ? custosResponse.getData() : null;
        
        log.info("CustosResponse: {}", custosResponse);
        log.info("Quantidade de custos: {}", custos != null ? custos.size() : 0);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // Aba de Detalhes
            Sheet detalhesSheet = workbook.createSheet("Custos Detalhados");
            createCustosHeader(detalhesSheet);
            if (custos != null && !custos.isEmpty()) {
                createCustosData(detalhesSheet, custos);
            }

            // Auto-size columns
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                autoSizeColumns(workbook.getSheetAt(i));
            }

            workbook.write(out);
            byte[] result = out.toByteArray();
            log.info("Arquivo Excel gerado com tamanho: {} bytes", result.length);
            return result;
        }
    }

    public byte[] exportarFaturamento(String userId) throws IOException {
        List<FaturamentoResponse> faturamentos = faturamentoService.getAllFaturamentos(userId);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Faturamento");
            createFaturamentoHeader(sheet);
            if (faturamentos != null && !faturamentos.isEmpty()) {
                createFaturamentoData(sheet, faturamentos);
            }
            autoSizeColumns(sheet);

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] exportarPrecos(String userId) throws IOException {
        List<PrecoResponse> precos = precoService.getAllPrecos(userId);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Preços");
            createPrecosHeader(sheet);
            if (precos != null && !precos.isEmpty()) {
                createPrecosData(sheet, precos);
            }
            autoSizeColumns(sheet);

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] exportarCompleto(String userId) throws IOException {
        CustosArrayResponse custosResponse = custoService.getAllCustos(userId);
        List<CustoCompletoResponse> custos = custosResponse != null ? custosResponse.getData() : null;
        List<FaturamentoResponse> faturamentos = faturamentoService.getAllFaturamentos(userId);
        List<PrecoResponse> precos = precoService.getAllPrecos(userId);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // Aba de Custos Detalhados
            Sheet custosSheet = workbook.createSheet("Custos Detalhados");
            createCustosHeader(custosSheet);
            if (custos != null && !custos.isEmpty()) {
                createCustosData(custosSheet, custos);
            }
            autoSizeColumns(custosSheet);

            // Aba de Faturamento
            Sheet faturamentoSheet = workbook.createSheet("Faturamento");
            createFaturamentoHeader(faturamentoSheet);
            if (faturamentos != null && !faturamentos.isEmpty()) {
                createFaturamentoData(faturamentoSheet, faturamentos);
            }
            autoSizeColumns(faturamentoSheet);

            // Aba de Preços
            Sheet precosSheet = workbook.createSheet("Preços");
            createPrecosHeader(precosSheet);
            if (precos != null && !precos.isEmpty()) {
                createPrecosData(precosSheet, precos);
            }
            autoSizeColumns(precosSheet);

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private void createCustosHeader(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Descrição");
        headerRow.createCell(2).setCellValue("Valor (R$)");
        headerRow.createCell(3).setCellValue("Tipo de Custo");
        headerRow.createCell(4).setCellValue("Porcentagem (%)");
        headerRow.createCell(5).setCellValue("Data de Criação");
        headerRow.createCell(6).setCellValue("Data de Atualização");

        // Style para header
        CellStyle headerStyle = sheet.getWorkbook().createCellStyle();
        Font headerFont = sheet.getWorkbook().createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        for (int i = 0; i < 7; i++) {
            headerRow.getCell(i).setCellStyle(headerStyle);
        }
    }

    private void createCustosData(Sheet sheet, List<CustoCompletoResponse> custos) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        int rowNum = 1;
        for (CustoCompletoResponse custo : custos) {
            CustoDetalheResponse detalhe = custo.getValueDB();
            if (detalhe != null) {
                Row row = sheet.createRow(rowNum++);
                
                row.createCell(0).setCellValue(detalhe.getId());
                row.createCell(1).setCellValue(detalhe.getDescricao());
                row.createCell(2).setCellValue(detalhe.getValor().doubleValue());
                row.createCell(3).setCellValue(detalhe.getTipoCusto());
                row.createCell(4).setCellValue(detalhe.getPorcentagem() != null ? detalhe.getPorcentagem().doubleValue() : 0.0);
                row.createCell(5).setCellValue(""); // Placeholder para createdAt
                row.createCell(6).setCellValue(""); // Placeholder para updatedAt
            }
        }
    }

    private void createFaturamentoData(Sheet sheet, List<FaturamentoResponse> faturamentos) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        int rowNum = 1;
        for (FaturamentoResponse faturamento : faturamentos) {
            Row row = sheet.createRow(rowNum++);
            
            row.createCell(0).setCellValue(faturamento.getId());
            row.createCell(1).setCellValue(faturamento.getEquipamento());
            row.createCell(2).setCellValue(faturamento.getMediaAlugados() != null ? faturamento.getMediaAlugados().doubleValue() : 0.0);
            row.createCell(3).setCellValue(faturamento.getMediaAlugados() != null ? faturamento.getMediaAlugados().doubleValue() : 0.0);
            row.createCell(4).setCellValue(faturamento.getPorcentagem() != null ? faturamento.getPorcentagem().doubleValue() : 0.0);
            row.createCell(5).setCellValue(0.0); // Placeholder para preço
            row.createCell(6).setCellValue(faturamento.getPorcentagem() != null ? faturamento.getPorcentagem().doubleValue() : 0.0);
            row.createCell(7).setCellValue(""); // Placeholder para createdAt
            row.createCell(8).setCellValue(""); // Placeholder para updatedAt
        }
    }

    private void createFaturamentoHeader(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Equipamento");
        headerRow.createCell(2).setCellValue("Total de Equipamentos");
        headerRow.createCell(3).setCellValue("Média Alugados");
        headerRow.createCell(4).setCellValue("Taxa de Ocupação (%)");
        headerRow.createCell(5).setCellValue("Preço (R$)");
        headerRow.createCell(6).setCellValue("Porcentagem (%)");
        headerRow.createCell(7).setCellValue("Data de Criação");
        headerRow.createCell(8).setCellValue("Data de Atualização");

        // Style para header
        CellStyle headerStyle = sheet.getWorkbook().createCellStyle();
        Font headerFont = sheet.getWorkbook().createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        for (int i = 0; i < 9; i++) {
            headerRow.getCell(i).setCellStyle(headerStyle);
        }
    }

    private void createPrecosHeader(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Equipamento");
        headerRow.createCell(2).setCellValue("Investimento (R$)");
        headerRow.createCell(3).setCellValue("Valor Residual (R$)");
        headerRow.createCell(4).setCellValue("Depreciação (meses)");
        headerRow.createCell(5).setCellValue("Preço Atual Mensal (R$)");
        headerRow.createCell(6).setCellValue("Margem");
        headerRow.createCell(7).setCellValue("Manutenção Atual (R$)");
        headerRow.createCell(8).setCellValue("Preço Adequado (R$)");
        headerRow.createCell(9).setCellValue("Diferença (R$)");
        headerRow.createCell(10).setCellValue("Payback (meses)");
        headerRow.createCell(11).setCellValue("Faturamento Estimado (R$)");
        headerRow.createCell(12).setCellValue("Lucro Total (R$)");
        headerRow.createCell(13).setCellValue("Data de Criação");
        headerRow.createCell(14).setCellValue("Data de Atualização");

        // Style para header
        CellStyle headerStyle = sheet.getWorkbook().createCellStyle();
        Font headerFont = sheet.getWorkbook().createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        for (int i = 0; i < 15; i++) {
            headerRow.getCell(i).setCellStyle(headerStyle);
        }
    }

    private void createPrecosData(Sheet sheet, List<PrecoResponse> precos) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        int rowNum = 1;
        for (PrecoResponse preco : precos) {
            Row row = sheet.createRow(rowNum++);
            
            row.createCell(0).setCellValue(preco.getId());
            row.createCell(1).setCellValue(preco.getEquipamento());
            row.createCell(2).setCellValue(preco.getInvestimento().doubleValue());
            row.createCell(3).setCellValue(preco.getResidual().doubleValue());
            row.createCell(4).setCellValue(preco.getDepreciacaoMeses());
            row.createCell(5).setCellValue(preco.getPrecoAtualMensal().doubleValue());
            row.createCell(6).setCellValue(preco.getMargem().doubleValue());
            row.createCell(7).setCellValue(preco.getManutencaoAnual() != null ? preco.getManutencaoAnual().doubleValue() : 0);
            row.createCell(8).setCellValue(preco.getPrecoAdequado() != null ? preco.getPrecoAdequado().doubleValue() : 0);
            row.createCell(9).setCellValue(preco.getPrecoAtualMenosPrecoAdequado() != null ? preco.getPrecoAtualMenosPrecoAdequado().doubleValue() : 0);
            row.createCell(10).setCellValue(preco.getPaybackMeses() != null ? preco.getPaybackMeses().doubleValue() : 0);
            row.createCell(11).setCellValue(preco.getFaturamentoEstimado() != null ? preco.getFaturamentoEstimado().doubleValue() : 0);
            row.createCell(12).setCellValue(preco.getLucroTotal() != null ? preco.getLucroTotal().doubleValue() : 0);
            row.createCell(13).setCellValue(""); // Placeholder para createdAt
            row.createCell(14).setCellValue(""); // Placeholder para updatedAt
        }
    }

    public byte[] gerarExcelTeste() throws IOException {
        log.info("Gerando arquivo Excel de teste");
        
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Teste");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Coluna 1");
            headerRow.createCell(1).setCellValue("Coluna 2");
            headerRow.createCell(2).setCellValue("Coluna 3");
            
            Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue("Dado 1");
            dataRow.createCell(1).setCellValue("Dado 2");
            dataRow.createCell(2).setCellValue("Dado 3");

            workbook.write(out);
            byte[] result = out.toByteArray();
            log.info("Arquivo Excel de teste gerado com tamanho: {} bytes", result.length);
            return result;
        }
    }

    private void autoSizeColumns(Sheet sheet) {
        Row headerRow = sheet.getRow(0);
        if (headerRow != null) {
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                sheet.autoSizeColumn(i);
                // Ajustar um pouco mais para não cortar o conteúdo
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1000);
            }
        }
    }
}
