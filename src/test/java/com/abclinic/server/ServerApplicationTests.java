package com.abclinic.server;

import com.abclinic.server.service.ScheduleReminderService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;
import java.util.Scanner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Transactional
class ServerApplicationTests {
    @Autowired
    private ScheduleReminderService scheduleReminderService;

    @Test
    public void checkReminder() {
        scheduleReminderService.remind();
    }

}
