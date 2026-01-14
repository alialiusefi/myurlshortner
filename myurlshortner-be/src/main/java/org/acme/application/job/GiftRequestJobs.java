package org.acme.application.job;

import io.quarkus.scheduler.Scheduled;
import org.acme.application.usecases.GiftRequestUseCases;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GiftRequestJobs {

    private final GiftRequestUseCases useCases;
    private final static Logger logger = LoggerFactory.getLogger(GiftRequestJobs.class);

    public GiftRequestJobs(GiftRequestUseCases useCases) {
        this.useCases = useCases;
    }

    @Scheduled(every = "30s")
    public void cancelOutdatedAwaitingGiftRequestJobs() {
        logger.info("cancelOutdatedAwaitingGiftRequestJobs() was invoked!");
        useCases.cancelOutdatedAwaitingGiftRequestJobs();
        logger.info("cancelOutdatedAwaitingGiftRequestJobs() finished!");
    }
}
