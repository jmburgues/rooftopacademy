package com.rooftop.academy.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.rooftop.academy.model.AnalyzedText;

@Repository
public interface TextRepository extends JpaRepository<AnalyzedText, Integer> {
    @Query(value = "SELECT * FROM TEXTS WHERE hash = ?1", nativeQuery = true)
    Optional<AnalyzedText> findByHash(String hash);

    @Query(value = "SELECT * FROM TEXTS WHERE chars = ?1 LIMIT ?2,?3", nativeQuery = true)
    List<AnalyzedText> findAllWithChars(Integer chars, Integer page, Integer rpp);
}
