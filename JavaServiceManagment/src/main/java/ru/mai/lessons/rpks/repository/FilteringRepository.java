package ru.mai.lessons.rpks.repository;

import org.springframework.data.repository.CrudRepository;
import ru.mai.lessons.rpks.model.Filter;

public interface FilteringRepository extends CrudRepository<Filter, Long> {
    Iterable<Filter> findAllByFilterId(Long filterId);

    Filter findFilterByFilterIdAndRuleId(Long filterId, Long ruleId);

    void deleteFilterByFilterIdAndRuleId(Long filterId, Long ruleId);
}
