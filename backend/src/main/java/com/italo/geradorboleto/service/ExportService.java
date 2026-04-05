package com.italo.geradorboleto.service;

import com.italo.geradorboleto.dto.CustoSimplesResponse;
import com.italo.geradorboleto.dto.CustosArrayResponse;
import com.italo.geradorboleto.dto.FaturamentoResponse;
import com.italo.geradorboleto.dto.PrecoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
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
        List<CustoSimplesResponse> custos = custosResponse != null ? custosResponse.getData() : null;
        
        log.info("CustosResponse: {}", custosResponse);
        log.info("Quantidade de custos: {}", custos != null ? custos.size() : 0);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Custos");
            
            // Tabela de Resumo (linhas 0-3)
            createResumoSection(sheet, custos);
            
            // Tabela Detalhada (começa na linha 5)
            createDetalhadosSection(sheet, custos);

            // Auto-size columns
            autoSizeColumns(sheet);

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
        List<CustoSimplesResponse> custos = custosResponse != null ? custosResponse.getData() : null;
        List<FaturamentoResponse> faturamentos = faturamentoService.getAllFaturamentos(userId);
        List<PrecoResponse> precos = precoService.getAllPrecos(userId);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // Aba de Custos Detalhados
            Sheet custosSheet = workbook.createSheet("Custos");
            createResumoSection(custosSheet, custos);
            createDetalhadosSection(custosSheet, custos);
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

    private void createResumoSection(Sheet sheet, List<CustoSimplesResponse> custos) {
        // Calcular valores
        double totalFixos = 0.0;
        double totalVariaveis = 0.0;
        
        if (custos != null) {
            for (CustoSimplesResponse custo : custos) {
                if ("FIXO".equalsIgnoreCase(custo.getTipoCusto())) {
                    totalFixos += custo.getValor().doubleValue();
                } else if ("VARIÁVEL".equalsIgnoreCase(custo.getTipoCusto()) || "VARIAVEL".equalsIgnoreCase(custo.getTipoCusto())) {
                    totalVariaveis += custo.getValor().doubleValue();
                }
            }
        }
        
        double totalGeral = totalFixos + totalVariaveis;
        double percFixos = totalGeral > 0 ? (totalFixos / totalGeral) * 100 : 0;
        double percVariaveis = totalGeral > 0 ? (totalVariaveis / totalGeral) * 100 : 0;
        
        // Estilos
        CellStyle titleStyle = createTitleStyle(sheet.getWorkbook());
        CellStyle cardFixoStyle = createCardStyle(sheet.getWorkbook(), IndexedColors.DARK_BLUE);
        CellStyle cardVariavelStyle = createCardStyle(sheet.getWorkbook(), IndexedColors.GREEN);
        CellStyle cardTotalStyle = createCardStyle(sheet.getWorkbook(), IndexedColors.GREY_50_PERCENT);
        CellStyle currencyCardStyle = createCurrencyCardStyle(sheet.getWorkbook());
        CellStyle percentCardStyle = createPercentCardStyle(sheet.getWorkbook());
        
        // Título "Resumo de Custos" (similar aos cards do front)
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Resumo de Custos");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
        
        // Espaço
        Row spaceRow = sheet.createRow(1);
        spaceRow.createCell(0).setCellValue("");
        
        // Layout de Cards (3 lado a lado como no front)
        int cardRow = 2;
        
        // Card 1: Custos Fixos (coluna A)
        Row fixosRow = sheet.createRow(cardRow);
        Cell fixosIconCell = fixosRow.createCell(0);
        fixosIconCell.setCellValue("💰 Custos Fixos");
        fixosIconCell.setCellStyle(cardFixoStyle);
        
        Row fixosValorRow = sheet.createRow(cardRow + 1);
        Cell fixosValorCell = fixosValorRow.createCell(0);
        fixosValorCell.setCellValue(totalFixos);
        fixosValorCell.setCellStyle(currencyCardStyle);
        
        Row fixosPercentRow = sheet.createRow(cardRow + 2);
        Cell fixosPercentCell = fixosPercentRow.createCell(0);
        fixosPercentCell.setCellValue(String.format("%.1f%% do total", percFixos));
        fixosPercentCell.setCellStyle(percentCardStyle);
        
        // Card 2: Custos Variáveis (coluna C)
        Row variaveisRow = sheet.createRow(cardRow);
        Cell variaveisIconCell = variaveisRow.createCell(2);
        variaveisIconCell.setCellValue("📈 Custos Variáveis");
        variaveisIconCell.setCellStyle(cardVariavelStyle);
        
        Row variaveisValorRow = sheet.createRow(cardRow + 1);
        Cell variaveisValorCell = variaveisValorRow.createCell(2);
        variaveisValorCell.setCellValue(totalVariaveis);
        variaveisValorCell.setCellStyle(currencyCardStyle);
        
        Row variaveisPercentRow = sheet.createRow(cardRow + 2);
        Cell variaveisPercentCell = variaveisPercentRow.createCell(2);
        variaveisPercentCell.setCellValue(String.format("%.1f%% do total", percVariaveis));
        variaveisPercentCell.setCellStyle(percentCardStyle);
        
        // Card 3: Total (coluna E)
        Row totalRow = sheet.createRow(cardRow);
        Cell totalIconCell = totalRow.createCell(4);
        totalIconCell.setCellValue("📊 Total");
        totalIconCell.setCellStyle(cardTotalStyle);
        
        Row totalValorRow = sheet.createRow(cardRow + 1);
        Cell totalValorCell = totalValorRow.createCell(4);
        totalValorCell.setCellValue(totalGeral);
        totalValorCell.setCellStyle(currencyCardStyle);
        
        Row totalPercentRow = sheet.createRow(cardRow + 2);
        Cell totalPercentCell = totalPercentRow.createCell(4);
        totalPercentCell.setCellValue("100% do total");
        totalPercentCell.setCellStyle(percentCardStyle);
        
        // Espaço após os cards
        Row spaceRow2 = sheet.createRow(cardRow + 3);
        spaceRow2.createCell(0).setCellValue("");
    }

    private void createDetalhadosSection(Sheet sheet, List<CustoSimplesResponse> custos) {
        // Calcular total para percentuais
        double totalGeral = 0.0;
        if (custos != null) {
            for (CustoSimplesResponse custo : custos) {
                totalGeral += custo.getValor().doubleValue();
            }
        }
        
        // Espaço antes da tabela detalhada (cards terminam na linha 5)
        Row spaceRow = sheet.createRow(6);
        spaceRow.createCell(0).setCellValue("");
        
        // Título "Custos Detalhados" (similar ao front)
        Row titleRow = sheet.createRow(7);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Custos Detalhados");
        titleCell.setCellStyle(createTitleStyle(sheet.getWorkbook()));
        sheet.addMergedRegion(new CellRangeAddress(7, 7, 0, 3));
        
        // Informação adicional (similar ao front)
        Row infoRow = sheet.createRow(8);
        Cell infoCell = infoRow.createCell(0);
        infoCell.setCellValue(String.format("%d custos cadastrados", custos != null ? custos.size() : 0));
        infoCell.setCellStyle(createInfoStyle(sheet.getWorkbook()));
        sheet.addMergedRegion(new CellRangeAddress(8, 8, 0, 3));
        
        // Cabeçalho da tabela detalhada
        Row headerRow = sheet.createRow(9);
        String[] headers = {"Descrição", "Valor", "Tipo", "% do Total"};
        CellStyle headerStyle = createHeaderStyle(sheet.getWorkbook(), IndexedColors.DARK_GREEN);
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Dados detalhados
        if (custos != null && !custos.isEmpty()) {
            CellStyle currencyStyle = createCurrencyStyle(sheet.getWorkbook());
            CellStyle dataStyle = createDataStyle(sheet.getWorkbook());
            CellStyle percentStyle = createPercentStyle(sheet.getWorkbook());
            CellStyle badgeFixoStyle = createBadgeStyle(sheet.getWorkbook(), IndexedColors.RED);
            CellStyle badgeVariavelStyle = createBadgeStyle(sheet.getWorkbook(), IndexedColors.BLUE);
            int rowNum = 10;
            
            for (CustoSimplesResponse custo : custos) {
                Row row = sheet.createRow(rowNum++);
                
                // Descrição
                Cell descCell = row.createCell(0);
                descCell.setCellValue(custo.getDescricao());
                descCell.setCellStyle(dataStyle);
                
                // Valor
                Cell valorCell = row.createCell(1);
                valorCell.setCellValue(custo.getValor().doubleValue());
                valorCell.setCellStyle(currencyStyle);
                
                // Tipo de Custo (com badge style como no front)
                String tipoCusto = "FIXO".equalsIgnoreCase(custo.getTipoCusto()) ? "Fixo" : "Variável";
                Cell tipoCell = row.createCell(2);
                tipoCell.setCellValue(tipoCusto);
                if ("FIXO".equalsIgnoreCase(custo.getTipoCusto())) {
                    tipoCell.setCellStyle(badgeFixoStyle);
                } else {
                    tipoCell.setCellStyle(badgeVariavelStyle);
                }
                
                // Percentual
                double percentual = totalGeral > 0 ? (custo.getValor().doubleValue() / totalGeral) * 100 : 0;
                Cell percentCell = row.createCell(3);
                percentCell.setCellValue(String.format("%.1f%%", percentual));
                percentCell.setCellStyle(percentStyle);
            }
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook, IndexedColors color) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setFillForegroundColor(color.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        return style;
    }

    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        font.setColor(IndexedColors.DARK_BLUE.getIndex()); // Cor similar ao account-primary
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat((short) 8); // Formato de moeda padrão
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        return style;
    }

    private CellStyle createPercentStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        return style;
    }

    private CellStyle createTotalStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.DARK_GREEN.getIndex()); // Cor similar ao account-success
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);
        style.setTopBorderColor(IndexedColors.DARK_GREEN.getIndex());
        style.setBottomBorderColor(IndexedColors.DARK_GREEN.getIndex());
        style.setLeftBorderColor(IndexedColors.DARK_GREEN.getIndex());
        style.setRightBorderColor(IndexedColors.DARK_GREEN.getIndex());
        style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex()); // Cor similar ao account-light
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
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

    private CellStyle createCardStyle(Workbook workbook, IndexedColors color) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(color.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        return style;
    }

    private CellStyle createCurrencyCardStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 18);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setDataFormat((short) 8); // Formato de moeda padrão
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        return style;
    }

    private CellStyle createPercentCardStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        return style;
    }

    private CellStyle createInfoStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createBadgeStyle(Workbook workbook, IndexedColors color) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 10);
        font.setColor(color.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setTopBorderColor(color.getIndex());
        style.setBottomBorderColor(color.getIndex());
        style.setLeftBorderColor(color.getIndex());
        style.setRightBorderColor(color.getIndex());
        style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }
}
