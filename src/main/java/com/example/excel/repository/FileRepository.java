package com.example.excel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.excel.domain.ExcelFileEntity;

/**
 * Repository for Excel Entity
 * 
 * @author MEHUL TRIVEDI
 *
 */
@Repository
public interface FileRepository extends JpaRepository<ExcelFileEntity, Long> {
	/**
	 * @return
	 */
	@Query("from ExcelFileEntity e where e.isDeleted = false")
	List<ExcelFileEntity> findByDeletedFalse();

	/**
	 * @param id
	 * @return
	 */
	@Query("from ExcelFileEntity e where e.isDeleted = false and e.id = :id")
	Optional<ExcelFileEntity> findByIdAndDeletedFalse(@Param("id") Long id);

	/**
	 * @param id
	 * @return
	 */
	@Query("select e.status from ExcelFileEntity e where e.isDeleted = false and e.id = :id")
	String findStausByIdAndDeletedFalse(@Param("id") Long id);

	/**
	 * @return
	 */
	@Query("from ExcelFileEntity e where e.isDeleted = false and e.status = :status")
	List<ExcelFileEntity> findByDeletedFalseAndStatusSaved(@Param("status") String status);
}
