package com.gmail.yevgen.spring.worker;

import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class ActivationWorker {
    public int getActivationCode() {
        int digits = 8;
        int result = 0;
        Random r = new Random();

        for (int i = 0; i < digits; i++) {
            result += Math.pow(10, i) * (r.nextInt(9) + 1);
        }
        return result;
    }
}
