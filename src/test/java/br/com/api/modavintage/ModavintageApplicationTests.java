package br.com.api.modavintage;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean; // 
import br.com.api.modavintage.Notification.EmailService; // 

@SpringBootTest
class ModavintageApplicationTests {


    @MockBean
    private EmailService emailService;

    @Test
    void contextLoads() {
    }
}