package com.lede.blockchain.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * @author bjyiguoqiang on 2018/4/8.
 */
@Configuration
@ImportResource(locations = {"classpath:app-db.xml"})
public class XmlConfig {
}
