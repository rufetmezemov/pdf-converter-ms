package az.ms.pdf.converter.service.impl;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Playwright;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Service
public class PlaywrightService implements DisposableBean {
    private final Playwright playwright;
    public final Browser browser;
    public final BrowserContext context;
    public final SpringTemplateEngine engine;

    public PlaywrightService() {
        this.playwright = Playwright.create();
        this.browser = this.playwright.chromium().launch();
        this.context = this.browser.newContext();
        var resolver = new ClassLoaderTemplateResolver();
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setPrefix("/templates/");
        resolver.setSuffix(".html");
        this.engine = new SpringTemplateEngine();
        this.engine.setTemplateResolver(resolver);
    }

    @Override
    public void destroy() {
        this.context.close();
        this.browser.close();
        this.playwright.close();
    }
}
