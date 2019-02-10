package collector.api;

import collector.configuration.EthConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Support ethereum api
 *
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j(topic = "api")
@RestController
@RequestMapping("/api/ethereum/**")
@ConditionalOnBean(value = EthConfiguration.class)
public class EthApiController {
    // tag : blocks
    // -- tag : blocks

    // tag : transactions
    // -- tag : transactions

    // tag : account
    // -- tag : account
}