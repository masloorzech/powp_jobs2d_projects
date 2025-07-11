package edu.kis.powp.jobs2d.drivers.monitoring;

import edu.kis.powp.jobs2d.drivers.AbstractDecorator;
import edu.kis.powp.jobs2d.drivers.VisitableJob2dDriver;
import edu.kis.powp.jobs2d.drivers.monitoring.DriverEventManager.DriverEventType;
import edu.kis.powp.observer.Publisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class DriverMonitorDecorator extends AbstractDecorator {
    private final DriverUsageMonitor monitor;
    private final Publisher movePublisher = new Publisher();
    private final DriverMonitoringConfig config;

    private static boolean globalMonitorEnabled = true;

    public DriverMonitorDecorator(VisitableJob2dDriver driver, DriverUsageMonitor monitor, DriverMonitoringConfig config) {
        super(driver);
        this.monitor = Objects.requireNonNull(monitor, "Monitor cannot be null");
        this.config = config != null ? config : createDefaultConfig();
    }

    public DriverMonitorDecorator(VisitableJob2dDriver driver, DriverUsageMonitor monitor) {
        this(driver, monitor, null);
    }

    private void notifyObserversIfEnabled() {
        if (config.isMonitorEnabled()) {
            movePublisher.notifyObservers();
        }
    }
    private DriverMonitoringConfig createDefaultConfig() {
        return new DriverMonitoringConfig.Builder()
                .withMonitorEnabled(true)
                .build();
    }
    @Override
    public void setPosition(int x, int y) {
        monitor.recordHeadMove(x, y);

        if (!monitor.isMovementAllowed()) {
            return;
        }

        driver.setPosition(x, y);
        notifyObserversIfEnabled();
    }

    @Override
    public void operateTo(int x, int y) {
        monitor.recordOperationMove(x, y);

        if (!monitor.isMovementAllowed()) {
            return;
        }

        driver.operateTo(x, y);
        notifyObserversIfEnabled();
    }

    public Publisher getMovePublisher(){
        return movePublisher;
    }

    public static boolean isMonitorEnabled() {
        return globalMonitorEnabled;
    }

    public static void setMonitorEnabled(boolean enabled) {
        globalMonitorEnabled = enabled;
    }
}
