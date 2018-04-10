package com.lede.blockchain.biz;

import com.lede.blockchain.main.Start;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = ("spring.profiles.active=local"), classes = Start.class)
public class ApplicationTests {

}
