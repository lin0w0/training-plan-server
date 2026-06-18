package com.hfk.training.modules.statistics.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jakarta.servlet.ServletOutputStream;
import java.awt.Color;
import java.util.List;
import java.util.Map;

/**
 * PDF 报表导出服务
 */
@Service
@RequiredArgsConstructor
public class PdfExportService {

    private final JdbcTemplate jdbc;

    // 中文字体 (使用系统自带)
    private static final String FONT_PATH = "/System/Library/Fonts/STHeiti Light.ttc,1";

    /**
     * 导出培养计划执行情况 PDF
     */
    public void exportPlanExecution(ServletOutputStream out) throws Exception {
        Document doc = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(doc, out);
        doc.open();

        BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        Font titleFont = new Font(bf, 18, Font.BOLD);
        Font headerFont = new Font(bf, 11, Font.BOLD);
        Font cellFont = new Font(bf, 10);

        doc.add(new Paragraph("培养计划执行情况统计报表", titleFont));
        doc.add(new Paragraph("生成时间: " + java.time.LocalDateTime.now().toString().replace("T", " "), cellFont));
        doc.add(new Paragraph("\n"));

        List<Map<String, Object>> data = jdbc.queryForList("""
            SELECT m.major_name, COUNT(DISTINCT s.id) AS student_count,
                   COALESCE(SUM(CASE WHEN scr.is_pass=1 THEN 3 ELSE 0 END)/NULLIF(COUNT(DISTINCT s.id),0), 0) AS avg_earned,
                   ROUND(COALESCE(COUNT(CASE WHEN scr.is_pass=1 THEN 1 END)*100.0/NULLIF(COUNT(scr.id),0), 0), 1) AS pass_rate
            FROM major m LEFT JOIN student s ON s.major_id=m.id AND s.deleted=0
            LEFT JOIN student_course_record scr ON scr.student_id=s.id AND scr.deleted=0
            WHERE m.deleted=0 GROUP BY m.id, m.major_name ORDER BY student_count DESC
            """);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3, 1, 1, 1});
        addHeader(table, headerFont, "专业名称", "学生数", "平均已获学分", "通过率(%)");

        for (Map<String, Object> row : data) {
            table.addCell(new PdfPCell(new Phrase(str(row, "major_name"), cellFont)));
            table.addCell(new PdfPCell(new Phrase(str(row, "student_count"), cellFont)));
            table.addCell(new PdfPCell(new Phrase(str(row, "avg_earned"), cellFont)));
            table.addCell(new PdfPCell(new Phrase(str(row, "pass_rate"), cellFont)));
        }

        doc.add(table);
        doc.close();
    }

    /**
     * 导出课程统计 PDF
     */
    public void exportCourseStats(ServletOutputStream out) throws Exception {
        Document doc = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(doc, out);
        doc.open();

        BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        Font titleFont = new Font(bf, 18, Font.BOLD);
        Font headerFont = new Font(bf, 11, Font.BOLD);
        Font cellFont = new Font(bf, 10);

        doc.add(new Paragraph("课程选课人数统计报表", titleFont));
        doc.add(new Paragraph("生成时间: " + java.time.LocalDateTime.now().toString().replace("T", " "), cellFont));
        doc.add(new Paragraph("\n"));

        List<Map<String, Object>> data = jdbc.queryForList("""
            SELECT c.course_name,
                   (SELECT COUNT(*) FROM plan_course pc WHERE pc.course_id=c.id) AS plan_count,
                   (SELECT COUNT(*) FROM student_course_record scr WHERE scr.course_id=c.id) AS enrolled_count,
                   (SELECT COUNT(*) FROM student_course_record scr WHERE scr.course_id=c.id AND scr.is_pass=1) AS passed_count
            FROM course c WHERE c.deleted=0 ORDER BY enrolled_count DESC LIMIT 50
            """);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3, 1, 1, 1});
        addHeader(table, headerFont, "课程名称", "开设专业数", "选课人数", "通过人数");

        for (Map<String, Object> row : data) {
            table.addCell(new PdfPCell(new Phrase(str(row, "course_name"), cellFont)));
            table.addCell(new PdfPCell(new Phrase(str(row, "plan_count"), cellFont)));
            table.addCell(new PdfPCell(new Phrase(str(row, "enrolled_count"), cellFont)));
            table.addCell(new PdfPCell(new Phrase(str(row, "passed_count"), cellFont)));
        }

        doc.add(table);
        doc.close();
    }

    /**
     * 导出学生学业统计 PDF
     */
    public void exportStudentStats(ServletOutputStream out) throws Exception {
        Document doc = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(doc, out);
        doc.open();

        BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        Font titleFont = new Font(bf, 18, Font.BOLD);
        Font headerFont = new Font(bf, 11, Font.BOLD);
        Font cellFont = new Font(bf, 10);
        Font warnFont = new Font(bf, 10, Font.NORMAL, new Color(200, 0, 0));

        doc.add(new Paragraph("学生学业统计报表", titleFont));
        doc.add(new Paragraph("生成时间: " + java.time.LocalDateTime.now().toString().replace("T", " "), cellFont));
        doc.add(new Paragraph("\n"));

        List<Map<String, Object>> data = jdbc.queryForList("""
            SELECT s.student_no, s.real_name, m.major_name,
                   COUNT(scr.id) AS total_courses,
                   COUNT(CASE WHEN scr.is_pass=1 THEN 1 END) AS passed,
                   ROUND(COUNT(CASE WHEN scr.is_pass=1 THEN 1 END)*100.0/NULLIF(COUNT(scr.id),0), 1) AS rate
            FROM student s LEFT JOIN major m ON s.major_id=m.id
            LEFT JOIN student_course_record scr ON scr.student_id=s.id AND scr.deleted=0
            WHERE s.deleted=0 GROUP BY s.id, s.student_no, s.real_name, m.major_name ORDER BY rate
            """);

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2, 1.5f, 2, 1, 1, 1});
        addHeader(table, headerFont, "学号", "姓名", "专业", "选课总数", "通过数", "通过率(%)");

        for (Map<String, Object> row : data) {
            table.addCell(new PdfPCell(new Phrase(str(row, "student_no"), cellFont)));
            table.addCell(new PdfPCell(new Phrase(str(row, "real_name"), cellFont)));
            table.addCell(new PdfPCell(new Phrase(str(row, "major_name"), cellFont)));
            table.addCell(new PdfPCell(new Phrase(str(row, "total_courses"), cellFont)));
            table.addCell(new PdfPCell(new Phrase(str(row, "passed"), cellFont)));
            double rate = row.get("rate") != null ? ((Number) row.get("rate")).doubleValue() : 0;
            PdfPCell rateCell = new PdfPCell(new Phrase(String.format("%.1f%%", rate), rate < 60 ? warnFont : cellFont));
            table.addCell(rateCell);
        }

        doc.add(table);
        doc.close();
    }

    private void addHeader(PdfPTable table, Font font, String... headers) {
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, font));
            cell.setBackgroundColor(new Color(230, 240, 250));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6);
            table.addCell(cell);
        }
    }

    private String str(Map<String, Object> row, String key) {
        Object v = row.get(key);
        return v == null ? "-" : v.toString();
    }
}
