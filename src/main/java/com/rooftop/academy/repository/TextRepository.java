package com.rooftop.academy.repository;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.rooftop.academy.model.AnalyzedText;

@Repository
public interface TextRepository extends PagingAndSortingRepository<AnalyzedText, Integer> {
    @Query(value = "SELECT * FROM ANALYSIS WHERE hash = ?1", nativeQuery = true)
    Optional<AnalyzedText> findByHash(String hash);

    @Query(value = "SELECT * FROM ANALYSIS A WHERE A.chars = ?1 AND A.deleted = false", nativeQuery = true)
    Page<AnalyzedText> findAllByCharsPaginated(Integer chars, Pageable page);

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
