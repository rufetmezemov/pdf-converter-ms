package az.ms.pdf.converter.service;

public interface ConverterService {
    byte[] convertByTemplateName(String template, Object data);

    byte[] convertByHTML(String html);
}
