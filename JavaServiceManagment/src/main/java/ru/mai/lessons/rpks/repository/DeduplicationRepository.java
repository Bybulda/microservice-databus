package ru.mai.lessons.rpks.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.mai.lessons.rpks.model.Deduplication;

@Repository
public interface DeduplicationRepository extends CrudRepository<Deduplication, Long> {

    Iterable<Deduplication> findAllByDeduplicationId(Long deduplicationId);

    Deduplication findDeduplicationByDeduplicationIdAndRuleId(Long deduplicationId, Long ruleId);

    void deleteDeduplicationByDeduplicationIdAndRuleId(Long deduplicationId, Long ruleId);
}
