package com.example.mourosub.controller.admin;

import com.example.mourosub.model.Newsletter;
import com.example.mourosub.repository.NewsletterRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin/newsletter")
public class AdminNewsletterController {

    private final NewsletterRepository newsletterRepository;

    public AdminNewsletterController(NewsletterRepository newsletterRepository) {
        this.newsletterRepository = newsletterRepository;
    }



    /**
     * Exporta la tabla NEWSLETTER a un fichero .xlsx y lo devuelve como descarga.
     */
    @GetMapping("/export-excel")
    public void exportExcel(HttpServletResponse response) throws IOException {

        List<Newsletter> suscriptores = newsletterRepository.findAll();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"newsletter_mourosub.xlsx\"");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Suscriptores");

            // --- Estilo cabecera ---
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);

            // --- Fila de cabecera ---
            Row header = sheet.createRow(0);
            String[] cols = {"ID", "Email"};
            for (int i = 0; i < cols.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(cols[i]);
                cell.setCellStyle(headerStyle);
            }

            // --- Estilo filas alternadas ---
            CellStyle altStyle = workbook.createCellStyle();
            altStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
            altStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // --- Datos ---
            int rowIdx = 1;
            for (Newsletter s : suscriptores) {
                Row row = sheet.createRow(rowIdx);
                row.createCell(0).setCellValue(s.getIdNewsletter() != null ? s.getIdNewsletter() : 0L);
                row.createCell(1).setCellValue(s.getEmail() != null ? s.getEmail() : "");
                if (rowIdx % 2 == 0) {
                    row.getCell(0).setCellStyle(altStyle);
                    row.getCell(1).setCellStyle(altStyle);
                }
                rowIdx++;
            }

            // Autoajustar columnas
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            workbook.write(response.getOutputStream());
        }
    }
}
