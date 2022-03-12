package com.rooftop.academy.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.rooftop.academy.model.AnalyzedText;

@Repository
public interface TextRepository extends JpaRepository<AnalyzedText, Integer> {
    @Query(value = "SELECT * FROM ANALYSIS WHERE hash = ?1", nativeQuery = true)
    Optional<AnalyzedText> findByHash(String hash);

    @Query(value = "SELECT * FROM ANALYSIS WHERE chars = ?1 AND DELETED = FALSE LIMIT ?2,?3", nativeQuery = true)
    List<AnalyzedText> findAllWithChars(Integer chars, Integer page, Integer rpp);

    @Transactional
    @Modifying
    @Query(value = "UPDATE ANALYSIS SET DELETED = 'TRUE' WHERE ANALYSIS_ID = :id", nativeQuery = true)
    void logicalDelete(Integer id);

    @Query(value = "SELECT * FROM ANALYSIS WHERE ANALYSIS_ID = ?1 AND DELETED = FALSE", nativeQuery = true)
    Optional<AnalyzedText> findById(Integer id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE ANALYSIS SET DELETED = 'FALSE' WHERE ANALYSIS_ID = :id", nativeQuery = true)
    void undeleteRegistry(Integer id);
}
