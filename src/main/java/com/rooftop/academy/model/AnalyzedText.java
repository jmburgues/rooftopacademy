package com.rooftop.academy.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "TEXTS")
public class AnalyzedText {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String hash;
    private int chars;
    private String result;
    private Boolean deleted = false;
}
