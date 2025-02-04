package ru.mai.lessons.rpks.services;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mai.lessons.rpks.model.Deduplication;
import ru.mai.lessons.rpks.repository.DeduplicationRepository;

@Service
@NoArgsConstructor
@Getter
public class DeduplicationService {
    @Autowired
    private DeduplicationRepository deduplicationRepository;

    public Iterable<Deduplication> getDeduplications() {
        return deduplicationRepository.findAll();
    }

    public Iterable<Deduplication> getDeduplicationsByDeduplicationId(Long deduplicationId) {
        return deduplicationRepository.findAllByDeduplicationId(deduplicationId);
    }

    public Deduplication getDeduplicationByDeduplicationIdAndRuleId(Long deduplicationId, Long ruleId) {
        return deduplicationRepository.findDeduplicationByDeduplicationIdAndRuleId(deduplicationId, ruleId);
    }

    public void deleteDeduplications(){
        deduplicationRepository.deleteAll();
    }

    public void deleteDeduplicationByDeduplicationIdAndRuleId(Long deduplicationId, Long ruleId){
        deduplicationRepository.deleteDeduplicationByDeduplicationIdAndRuleId(deduplicationId, ruleId);
    }

    public void saveDeduplication(Deduplication deduplication){
        deduplicationRepository.save(deduplication);
    }
}
