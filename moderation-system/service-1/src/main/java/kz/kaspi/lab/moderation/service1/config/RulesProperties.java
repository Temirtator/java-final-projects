package kz.kaspi.lab.moderation.service1.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

@Configuration
@ConfigurationProperties(prefix = "rules")
public class RulesProperties {
    private Set<String> restrictedCategories = new HashSet<>();
    private LocalTime workStart = LocalTime.of(9, 0);
    private LocalTime workEnd = LocalTime.of(18, 0);
    private String timeZone = "Asia/Almaty";

    public Set<String> getRestrictedCategories() {
        return restrictedCategories;
    }

    public void setRestrictedCategories(Set<String> restrictedCategories) {
        this.restrictedCategories = restrictedCategories;
    }

    public LocalTime getWorkStart() {
        return workStart;
    }

    public void setWorkStart(LocalTime workStart) {
        this.workStart = workStart;
    }

    public LocalTime getWorkEnd() {
        return workEnd;
    }

    public void setWorkEnd(LocalTime workEnd) {
        this.workEnd = workEnd;
    }

    public ZoneId getZoneId() {
        return ZoneId.of(timeZone);
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}
