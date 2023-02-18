package az.ms.pdf.converter.service.impl;

import az.ms.pdf.converter.exception.FileException;
import az.ms.pdf.converter.logger.DPLogger;
import az.ms.pdf.converter.service.ConverterService;
import az.ms.pdf.converter.util.NanoIdUtils;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitUntilState;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

@Service
public class ConverterServiceImpl implements ConverterService {
    private static final DPLogger log = DPLogger.getLogger(ConverterServiceImpl.class);
    private static final String SOURCE_PATH = "generated/";
    private static final String PROTOCOL = "file://";
    public static final Page.NavigateOptions NAVIGATE_OPTIONS = new Page.NavigateOptions()
            .setWaitUntil(WaitUntilState.NETWORKIDLE);
    private static final Page.PdfOptions PDF_OPTIONS = new Page.PdfOptions()
            .setFormat("A4")
            .setScale(0.7D)
            .setPrintBackground(true);

    private final PlaywrightService service;

    public ConverterServiceImpl(PlaywrightService service) {
        this.service = service;
        var path = Path.of(SOURCE_PATH);
        if (Files.notExists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                log.error("ActionLog.ConverterServiceImpl.error {}", e.getMessage());
                throw new FileException(e.getMessage());
            }
        }
    }

    @Override
    public byte[] convertByTemplateName(String template, Object data) {
        log.info("ActionLog.convertByTemplateName.start");
        byte[] result;
        try (var page = service.context.newPage()) {
            var context = new Context();
            context.setVariable("data", data);
            var filledHTML = service.engine.process(template, context);
            var htmlFile = new File(SOURCE_PATH + NanoIdUtils.randomNanoId() + ".html");
            writeHtmlTextToFile(filledHTML, htmlFile);
            result = generatePDF(page, htmlFile.getAbsolutePath());
            Files.deleteIfExists(htmlFile.toPath());
        } catch (IOException e) {
            log.error("ActionLog.convertByTemplateName.error Unable to delete file: {}", e.getMessage());
            throw new FileException(e.getMessage());
        }
        log.info("ActionLog.convertByTemplateName.end");
        return result;
    }

    @Override
    public byte[] convertByHTML(String text) {
        log.info("ActionLog.convertByHTML.start");
        byte[] result;
        try (var page = service.context.newPage()) {
            var htmlFile = new File(SOURCE_PATH + NanoIdUtils.randomNanoId() + ".html");
            writeHtmlTextToFile(text, htmlFile);
            result = generatePDF(page, htmlFile.getAbsolutePath());
            Files.deleteIfExists(htmlFile.toPath());
        } catch (IOException e) {
            log.info("ActionLog.convertByHTML.error Unable to delete file: {}", e.getMessage());
            throw new FileException(e.getMessage());
        }
        log.info("ActionLog.convertByHTML.end");
        return result;
    }

    private void writeHtmlTextToFile(String htmlText, File resultFile) {
        log.info("ActionLog.writeHtmlTextToFile.start");
        try (FileWriter writer = new FileWriter(resultFile)) {
            writer.write(htmlText);
            writer.flush();
        } catch (IOException e) {
            log.error("ActionLog.writeHtmlTextToFile.error {}", e.getMessage());
            throw new FileException(e.getMessage());
        }
        log.info("ActionLog.writeHtmlTextToFile.end");
    }

    private byte[] generatePDF(Page page, String htmlPath) {
        log.info("ActionLog.generatePDF.start");
        page.navigate(PROTOCOL + htmlPath, NAVIGATE_OPTIONS);
        var data = page.pdf(PDF_OPTIONS);
        page.close();
        log.info("ActionLog.generatePDF.end");
        return data;
    }
}
