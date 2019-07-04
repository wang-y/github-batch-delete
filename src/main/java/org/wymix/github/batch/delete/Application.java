package org.wymix.github.batch.delete;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.List;

@Slf4j
@SpringBootApplication
public class Application implements CommandLineRunner {

    @Value("${github.repositorys}")
    private List<String> repos;

    @Value("${github.username}")
    private String username;

    @Value("${github.password}")
    private String password;

    @Value("${chrome.win.bin:''}")
    private String chromeBin;

    private WebDriver webDriver;

    private static final String loginPageUrl = "https://github.com/login";

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class).web(WebApplicationType.NONE).run(args);
    }

    private void init() {
        String os = System.getProperty("os.name");

        ChromeOptions chromeOptions = null;

        if (StringUtils.startsWithIgnoreCase(os, "win")) {
            System.setProperty("webdriver.chrome.bin", chromeBin);
            System.setProperty("webdriver.chrome.driver", "src/main/resources/driver/chromedriver.exe");
        } else if (StringUtils.startsWithIgnoreCase(os, "linux")) {
            chromeOptions = new ChromeOptions();
            chromeOptions.setHeadless(true);
            System.setProperty("webdriver.chrome.driver", "src/main/resources/driver/chromedriver_linux");
        } else if (StringUtils.startsWithIgnoreCase(os, "mac")) {
            System.setProperty("webdriver.chrome.driver", "src/main/resources/driver/chromedriver_mac");
        }

        log.info("正在读取系统配置...");
        log.info("webdriver.chrome.bin: [{}]  webdriver.chrome.driver: [{}]", System.getProperty("webdriver.chrome.bin"), System.getProperty("webdriver.chrome.driver"));
        log.info("正在初始化WebDriver...");
        if (chromeOptions != null){
            webDriver = new ChromeDriver(chromeOptions);
        }else {
            webDriver = new ChromeDriver();
        }
        log.info("初始化WebDriver完毕");

        log.info("正在登陆Github...");
        webDriver.get(loginPageUrl);
        WebElement loginField = webDriver.findElement(By.id("login_field"));
        loginField.sendKeys(this.username);
        WebElement password = webDriver.findElement(By.id("password"));
        password.sendKeys(this.password);
        WebElement commit = webDriver.findElement(By.name("commit"));
        commit.click();
        log.info("登陆成功");
    }

    @Override
    public void run(String... args) {
        init();

        String urlPrefix = "https://github.com/" + this.username + "/";
        repos.stream().forEach(repo -> {
            log.info("正在删除仓库 [{}]...", repo);
            webDriver.get(urlPrefix + repo + "/settings");
            WebElement deleteButton = webDriver.findElement(By.xpath("//*[@id=\"options_bucket\"]/div[8]/ul/li[4]/details/summary"));
            deleteButton.click();

            List<WebElement> verifys = webDriver.findElements(By.name("verify"));
            verifys.get(verifys.size() - 1).sendKeys(repo);

            WebElement confirmCommit = webDriver.findElement(By.xpath("//*[@id=\"options_bucket\"]/div[8]/ul/li[4]/details/details-dialog/div[3]/form/button"));
            confirmCommit.click();
            log.info("仓库 [{}] 删除成功", repo);
        });

        quit();
    }

    private void quit() {
        if (webDriver != null) {
            webDriver.quit();
        }
    }


}

