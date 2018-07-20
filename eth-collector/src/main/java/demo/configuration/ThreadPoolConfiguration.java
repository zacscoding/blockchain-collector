package demo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author zacconding
 * @Date 2018-07-19
 * @GitHub : https://github.com/zacscoding
 */
@Configuration
public class ThreadPoolConfiguration {

    @Bean
    public TaskExecutor commonThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(1000);
        executor.setThreadNamePrefix("[common-thread-pool]");
        executor.initialize();
        executor.setDaemon(true);

        return executor;
    }
}