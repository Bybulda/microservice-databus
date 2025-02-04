package ru.mai.lessons.rpks.repository;

import org.springframework.data.repository.CrudRepository;
import ru.mai.lessons.rpks.model.Enrichment;

public interface EnrichmentRepository extends CrudRepository<Enrichment, Long> {
    Iterable<Enrichment> findAllByEnrichmentId(Long enrichmentId);

    Enrichment findEnrichmentByEnrichmentIdAndRuleId(Long enrichmentId, Long ruleId);

    void deleteEnrichmentByEnrichmentIdAndRuleId(Long enrichmentId, Long ruleId);
}
