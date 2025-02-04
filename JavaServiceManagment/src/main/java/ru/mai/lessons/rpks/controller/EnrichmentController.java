package ru.mai.lessons.rpks.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.mai.lessons.rpks.model.Enrichment;
import ru.mai.lessons.rpks.services.EnrichmentService;


@RestController
@RequestMapping("enrichment")
public class EnrichmentController {
    @Autowired
    private EnrichmentService enrichmentService;

    @GetMapping("/findAll")
    @Operation(summary = "Получить информацию о всех правилах обогащения в БД")
    public Iterable<Enrichment> getAllEnrichments() {
        return enrichmentService.getEnrichments();
    }

    @GetMapping("/findAll/{id}")
    @Operation(summary = "Получить информацию о всех правилах обогащения в БД по enrichment id")
    public Iterable<Enrichment> getAllEnrichmentsByEnrichmentId(@PathVariable long id) {
        return enrichmentService.getEnrichmentsByEnrichmentId(id);
    }

    @GetMapping("/find/{enrichmentId}/{ruleId}")
    @Operation(summary = "Получить информацию о правиле обогащения по enrichment id и rule id")
    public Enrichment getEnrichmentById(@PathVariable long enrichmentId, @PathVariable long ruleId) {
        return enrichmentService.getEnrichmentByEnrichmentIdAndRuleId(enrichmentId, ruleId);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Удалить информацию о всех правилах обогащения")
    public void deleteEnrichment() {
        enrichmentService.deleteEnrichments();
    }

    @DeleteMapping("/delete/{enrichmentId}/{ruleId}")
    @Operation(summary = "Удалить информацию по конкретному правилу обогащения с enrichment id и rule id")
    @Transactional
    public void deleteEnrichmentById(@PathVariable long enrichmentId, @PathVariable long ruleId) {
        enrichmentService.deleteEnrichmentByEnrichmentIdAndRuleId(enrichmentId, ruleId);
    }

    @PostMapping("/save")
    @Operation(summary = "Создать правило обогащения")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void save(@RequestBody @Valid Enrichment enrichment) {
        enrichmentService.saveEnrichment(enrichment);
    }

}
