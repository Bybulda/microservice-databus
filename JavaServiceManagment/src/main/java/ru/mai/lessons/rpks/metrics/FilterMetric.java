package ru.mai.lessons.rpks.metrics;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;
import ru.mai.lessons.rpks.repository.FilteringRepository;

@Component
@RequiredArgsConstructor
public class FilterMetric implements InfoContributor {

    private final FilteringRepository filteringRepository;

    @Override
    public void contribute(Info.Builder builder) {
        var countFilters = filteringRepository.count();
        builder.withDetail("countFilters", countFilters);
    }
}
