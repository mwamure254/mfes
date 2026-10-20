package com.mfano.mfes.utils.payments;

import java.time.LocalDate;
import java.time.ZoneId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mfano.mfes.services.RepaymentService;

@Component
public class RepaymentScheduler {
    private final RepaymentService repaymentService;

    public RepaymentScheduler(RepaymentService repaymentService){
        this.repaymentService = repaymentService;
    }
    private static final Logger log =
            LoggerFactory.getLogger(RepaymentScheduler.class);

    private static final ZoneId ZONE =
            ZoneId.of("Africa/Nairobi");

    // Run every day at 00:00 EAT.
    @Scheduled(cron = "0 0 0 * * *", zone = "Africa/Nairobi")
    public void processDueRepayments() {

        LocalDate today = LocalDate.now(ZONE);

        log.info("Starting scheduled loan repayments for {}", today);

        try {
            int processed = repaymentService.processDueInstallments(today);

            log.info(
                    "Scheduled repayment run completed. Date={}, processed={}",
                    today, processed
            );
        } catch (Exception ex) {
            log.error(
                    "Scheduled repayment run failed for {}",
                    today, ex
            );
        }
    }
}
