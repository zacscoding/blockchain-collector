package collector.ethereum.api;

import collector.configuration.EthereumConfiguration;
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
@ConditionalOnBean(value = EthereumConfiguration.class)
public class EthereumApiController {
    // tag : blocks
    // -- tag : blocks

    // tag : transactions
    // -- tag : transactions

    // tag : account
    // -- tag : account
}