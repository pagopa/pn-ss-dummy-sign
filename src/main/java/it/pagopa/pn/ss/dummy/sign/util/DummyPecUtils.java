package it.pagopa.pn.ss.dummy.sign.util;

import lombok.CustomLog;
import java.time.Duration;

@CustomLog
public class DummyPecUtils {

    private DummyPecUtils(){
        throw new UnsupportedOperationException("This is a Utility class");
    }

    public static Duration delayElement(int fileSize,double baseDelay,double fileSizeScale,double minScaling,double maxScaling) {
            long delay = (long) Math.ceil(Math.max(baseDelay, fileSize / fileSizeScale) * randomInRange(minScaling, maxScaling));

            log.debug("Delaying for {} millis.",delay);

            return Duration.ofMillis(delay);
    }


    public static Duration delayElement(double baseDelay,double maxScaling){
            log.debug("Base delay: {} ; Max Scaling : {}",baseDelay,maxScaling);
            log.debug("Max delay: {}",baseDelay*maxScaling);
            long delay = (long) randomInRange(baseDelay, baseDelay * maxScaling);

            log.debug("Delaying for {} millis.",delay);

            return Duration.ofMillis(delay);
    }

    public static double randomInRange(double min, double max){
        return min + Math.random() * (max - min);
    }
}
