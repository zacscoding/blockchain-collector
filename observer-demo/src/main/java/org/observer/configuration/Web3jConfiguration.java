package org.observer.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * @author zacconding
 * @Date 2018-05-01
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j
@Configuration
public class Web3jConfiguration {

    @Value("${web3j.urls}")
    private String urlValues;
    private List<String> urls;

    @PostConstruct
    private void setUp() {
        if(!StringUtils.hasText(urlValues)) {
            log.error("Must be added web3j http urls");
            System.exit(-1);
        }

        StringTokenizer st = new StringTokenizer(urlValues, ",");
        urls = new ArrayList<>(st.countTokens());
        while (st.hasMoreElements()) {
            urls.add(st.nextToken());
        }

        log.info("# Web3j http url size : " + urls.size());
    }

    public List<String> getHttpUrls() {
        return urls;
    }
}
