package com.p2p.tutoring.service.booking;

import com.p2p.tutoring.model.Slot;
import com.p2p.tutoring.model.User;
import com.p2p.tutoring.service.common.ServiceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class LoggingBookingService implements BookingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingBookingService.class);

    private final BookingService delegate;

    public LoggingBookingService(@Qualifier("coreBookingService") BookingService delegate) {
        this.delegate = delegate;
    }

    @Override
    public ServiceResult<Slot> getBookableSlot(Long slotId, User learner) {
        LOGGER.info("Booking confirmation requested for slot {} by user {}", slotId, learner.getId());
        ServiceResult<Slot> result = delegate.getBookableSlot(slotId, learner);
        if (!result.isSuccess()) {
            LOGGER.warn("Booking confirmation failed for slot {}: {}", slotId, result.getMessage());
        }
        return result;
    }

    @Override
    public ServiceResult<Void> bookSlot(Long slotId, User learner) {
        LOGGER.info("Booking submit requested for slot {} by user {}", slotId, learner.getId());
        ServiceResult<Void> result = delegate.bookSlot(slotId, learner);
        if (!result.isSuccess()) {
            LOGGER.warn("Booking submit failed for slot {}: {}", slotId, result.getMessage());
        }
        return result;
    }
}
