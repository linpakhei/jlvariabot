package com.jlvariabot.task;

import com.jlvariabot.utils.log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@Component
public class ScheduledTasks {

    @Value("${bot.env}")
    private String env;

    @Value("${bot.prod.testaliveurl}")
    private String prodTestAliveUrl;
    @Value("${bot.dev.testaliveurl}")
    private String devTestAliveUrl;

    @Scheduled(fixedRate = 180000)
    public void testAlive() {
        try {
            URL url = new URL("prod".equals(env) ? prodTestAliveUrl : devTestAliveUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            log.info("response: " + content);

            con.disconnect();
        } catch(MalformedURLException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
