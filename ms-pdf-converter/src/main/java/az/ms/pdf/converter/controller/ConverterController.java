package az.ms.pdf.converter.controller;

import az.ms.pdf.converter.service.ConverterService;
import java.io.ByteArrayInputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/file")
public class ConverterController {
    private final ConverterService service;

    @GetMapping(value = "/template/{template}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> convertByTemplateName(
            @PathVariable String template,
            @RequestBody Object data
    ) {
        var pdfAsByteArray = service.convertByTemplateName(template, data);
        var resource = new InputStreamResource(new ByteArrayInputStream(pdfAsByteArray));
        return ResponseEntity.ok()
                .headers(attachmentHeaders())
                .contentLength(pdfAsByteArray.length)
                .body(resource);
    }

    @GetMapping(value = "/html", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> convertByHTML(@RequestBody String html) {
        var pdfAsByteArray = service.convertByHTML(html);
        var resource = new InputStreamResource(new ByteArrayInputStream(pdfAsByteArray));
        return ResponseEntity.ok()
                .headers(attachmentHeaders())
                .contentLength(pdfAsByteArray.length)
                .body(resource);
    }

    private HttpHeaders attachmentHeaders() {
        var headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment");
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        headers.add(HttpHeaders.EXPIRES, "0");
        return headers;
    }
}
