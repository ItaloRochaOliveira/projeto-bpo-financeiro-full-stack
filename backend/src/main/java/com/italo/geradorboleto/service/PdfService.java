package com.italo.geradorboleto.service;

import com.italo.geradorboleto.dto.BoletoResponse;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    public byte[] generateBoletoPdf(BoletoResponse boleto) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Header
        Paragraph header = new Paragraph("BOLETO BANCÁRIO")
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(20)
            .setBold()
            .setMarginBottom(20);
        document.add(header);

        // Beneficiário Section
        Table beneficiarioTable = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
            .setMarginBottom(15);
        
        beneficiarioTable.addCell(createHeaderCell("Beneficiário"));
        beneficiarioTable.addCell(createValueCell(boleto.getNomeEmpresa()));
        
        beneficiarioTable.addCell(createHeaderCell("CPF/CNPJ"));
        beneficiarioTable.addCell(createValueCell(boleto.getCpfCnpj()));
        
        beneficiarioTable.addCell(createHeaderCell("Endereço"));
        beneficiarioTable.addCell(createValueCell(boleto.getEndereco()));
        
        document.add(beneficiarioTable);

        // Dados do Boleto Section
        Table dadosTable = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
            .setMarginBottom(15);
        
        dadosTable.addCell(createHeaderCell("Descrição"));
        dadosTable.addCell(createValueCell(boleto.getDescricaoReferencia()));
        
        dadosTable.addCell(createHeaderCell("Valor"));
        dadosTable.addCell(createValueCell(String.format("R$ %.2f", boleto.getValor())));
        
        dadosTable.addCell(createHeaderCell("Data de Vencimento"));
        dadosTable.addCell(createValueCell(formatDate(boleto.getVencimento())));
        
        dadosTable.addCell(createHeaderCell("Código do Boleto"));
        dadosTable.addCell(createValueCell(boleto.getId()));
        
        document.add(dadosTable);

        // Linha Digitável (simulada)
        Paragraph linhaDigitavel = new Paragraph("Linha Digitável: " + generateLinhaDigitavel(boleto))
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(12)
            .setMarginTop(20)
            .setMarginBottom(10);
        document.add(linhaDigitavel);

        // Código de Barras (simulado)
        Paragraph codigoBarras = new Paragraph("Código de Barras: " + generateCodigoBarras(boleto))
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(10)
            .setFontColor(ColorConstants.GRAY);
        document.add(codigoBarras);

        // Marca d'águra
        Paragraph watermark = new Paragraph("DOCUMENTO NÃO FISCAL")
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(8)
            .setFontColor(ColorConstants.LIGHT_GRAY)
            .setMarginTop(30);
        document.add(watermark);

        // Footer
        Paragraph footer = new Paragraph("Gerado em: " + java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(8)
            .setFontColor(ColorConstants.GRAY)
            .setMarginTop(20);
        document.add(footer);

        document.close();
        return outputStream.toByteArray();
    }

    private Cell createHeaderCell(String text) {
        Cell cell = new Cell();
        cell.add(new Paragraph(text).setBold().setFontColor(ColorConstants.WHITE));
        cell.setBackgroundColor(ColorConstants.DARK_GRAY);
        cell.setPadding(5);
        return cell;
    }

    private Cell createValueCell(String text) {
        Cell cell = new Cell();
        cell.add(new Paragraph(text));
        cell.setPadding(5);
        return cell;
    }

    private String formatDate(String date) {
        if (date == null) return "Não definido";
        try {
            java.time.LocalDate localDate = java.time.LocalDate.parse(date);
            return localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            return date;
        }
    }

    private String generateLinhaDigitavel(BoletoResponse boleto) {
        // Simulação de linha digitável (formato FEBRABAN)
        return String.format("%05d %05d %05d %05d %05d %05d %05d %05d %05d %05d %05d",
            (int)(Math.random() * 100000),
            (int)(Math.random() * 100000),
            (int)(Math.random() * 100000),
            (int)(Math.random() * 100000),
            (int)(Math.random() * 100000),
            (int)(Math.random() * 100000),
            (int)(Math.random() * 100000),
            (int)(Math.random() * 100000),
            (int)(Math.random() * 100000),
            (int)(Math.random() * 100000),
            (int)(Math.random() * 100000)
        );
    }

    private String generateCodigoBarras(BoletoResponse boleto) {
        // Simulação de código de barras (44 dígitos)
        StringBuilder codigo = new StringBuilder();
        for (int i = 0; i < 44; i++) {
            codigo.append((int)(Math.random() * 10));
        }
        return codigo.toString();
    }
}
