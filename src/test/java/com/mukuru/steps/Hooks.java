package com.mukuru.steps;

import com.mukuru.base.BrowserFactory;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Cucumber lifecycle hooks.
 * - Initialises the browser once before any scenario runs.
 * - Captures a screenshot on failure into screenshots/failed/.
 * - Generates an HTML results page after the suite completes.
 */
public class Hooks {

    @Before(order = 0)
    public void setUp() {
        BrowserFactory.initDriver();
    }

    @After
    public void afterScenario(Scenario scenario) {
        if (scenario.isFailed() && BrowserFactory.driver != null) {
            try {
                org.openqa.selenium.OutputType type = org.openqa.selenium.OutputType.BYTES;
                byte[] screenshot = ((org.openqa.selenium.TakesScreenshot) BrowserFactory.driver)
                        .getScreenshotAs(type);
                scenario.attach(screenshot, "image/png", "failed_screenshot");
                java.nio.file.Path dir = java.nio.file.Paths.get(
                        System.getProperty("user.dir"), "screenshots", "failed");
                java.nio.file.Files.createDirectories(dir);
                java.nio.file.Files.write(
                        dir.resolve("failed_" + System.currentTimeMillis() + ".png"), screenshot);
            } catch (Exception e) {
                System.out.println("[Hooks] Screenshot failed: " + e.getMessage());
            }
        }
    }

    @AfterAll
    public static void tearDownAll() throws IOException {
        generateHtmlReport();
        BrowserFactory.quitDriver();
    }

    // -------------------------------------------------------------------------
    // HTML report
    // -------------------------------------------------------------------------

    private static void generateHtmlReport() throws IOException {
        Path base = Paths.get(System.getProperty("user.dir"), "screenshots");
        Files.createDirectories(base.resolve("passed"));
        Files.createDirectories(base.resolve("failed"));

        List<String[]> passed = collectScreenshots(base.resolve("passed"), "passed");
        List<String[]> failed = collectScreenshots(base.resolve("failed"), "failed");

        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html lang='en'><head>")
            .append("<meta charset='UTF-8'>")
            .append("<meta name='viewport' content='width=device-width,initial-scale=1'>")
            .append("<title>WhatsApp Test Results</title>")
            .append("<style>")
            .append("*{box-sizing:border-box}body{font-family:Arial,sans-serif;margin:0;padding:24px;background:#f0f2f5;color:#333}")
            .append("h1{margin:0 0 4px}p.ts{color:#888;font-size:13px;margin:0 0 20px}")
            .append(".summary{display:flex;gap:12px;margin-bottom:28px}")
            .append(".badge{padding:10px 20px;border-radius:8px;font-size:15px;font-weight:bold;color:#fff}")
            .append(".badge.p{background:#2e7d32}.badge.f{background:#c62828}")
            .append("h2{margin:0 0 12px;font-size:18px}")
            .append("h2.p{color:#2e7d32}h2.f{color:#c62828}")
            .append(".grid{display:flex;flex-wrap:wrap;gap:16px;margin-bottom:36px}")
            .append(".card{background:#fff;border-radius:10px;padding:12px;box-shadow:0 2px 8px rgba(0,0,0,.12);width:300px}")
            .append(".card img{width:100%;border-radius:6px;border:2px solid #e0e0e0;display:block}")
            .append(".card a{text-decoration:none}")
            .append(".card p{margin:8px 0 0;font-size:12px;color:#666;word-break:break-all}")
            .append(".empty{color:#999;font-style:italic;margin-bottom:36px}")
            .append("</style></head><body>")
            .append("<h1>WhatsApp Test Results</h1>")
            .append("<p class='ts'>Generated: ").append(timestamp).append("</p>")
            .append("<div class='summary'>")
            .append("<div class='badge p'>PASSED: ").append(passed.size()).append("</div>")
            .append("<div class='badge f'>FAILED: ").append(failed.size()).append("</div>")
            .append("</div>");

        appendSection(html, "Passed Tests", passed, "p");
        appendSection(html, "Failed Tests", failed, "f");

        html.append("</body></html>");

        Path report = base.resolve("index.html");
        Files.writeString(report, html.toString());
        System.out.println("==============================================");
        System.out.println("TEST REPORT: file://" + report.toAbsolutePath());
        System.out.println("==============================================");
    }

    private static void appendSection(StringBuilder html, String title,
                                      List<String[]> screenshots, String cssClass) {
        html.append("<h2 class='").append(cssClass).append("'>").append(title)
            .append(" (").append(screenshots.size()).append(")</h2>");
        if (screenshots.isEmpty()) {
            html.append("<p class='empty'>No screenshots yet.</p>");
            return;
        }
        html.append("<div class='grid'>");
        for (String[] entry : screenshots) {
            String rel  = entry[0]; // e.g. passed/filename.png
            String name = entry[1]; // filename only
            html.append("<div class='card'>")
                .append("<a href='").append(rel).append("' target='_blank'>")
                .append("<img src='").append(rel).append("' alt='").append(name).append("' loading='lazy'>")
                .append("</a>")
                .append("<p>").append(name).append("</p>")
                .append("</div>");
        }
        html.append("</div>");
    }

    private static List<String[]> collectScreenshots(Path dir, String subfolder) throws IOException {
        List<String[]> list = new ArrayList<>();
        if (!Files.exists(dir)) return list;
        try (Stream<Path> files = Files.list(dir)) {
            files.filter(p -> p.toString().endsWith(".png"))
                 .sorted()
                 .forEach(p -> list.add(new String[]{
                     subfolder + "/" + p.getFileName(),
                     p.getFileName().toString()
                 }));
        }
        return list;
    }
}
