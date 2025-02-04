package ru.mai.lessons.rpks.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "filter_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Filter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    @Min(value = 1)
    private long id;

    @NotNull
    @Min(value = 1)
    private long filterId;

    @NotNull
    @Min(value = 1)
    private long ruleId;

    @NotNull
    @NotBlank
    private String fieldName;

    @NotNull
    @NotBlank
    private String filterFunctionName;

    @NotNull
    @NotBlank
    private String filterValue;
}
