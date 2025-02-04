package ru.mai.lessons.rpks.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "deduplication_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Deduplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    @Min(value = 1)
    private long id;

    @NotNull
    @Min(value = 1)
    private long deduplicationId;

    @NotNull
    @Min(value = 1)
    private long ruleId;

    @NotNull
    @NotBlank
    private String fieldName;

    @NotNull
    private long timeToLiveSec;

    @NotNull
    private boolean isActive;
}
