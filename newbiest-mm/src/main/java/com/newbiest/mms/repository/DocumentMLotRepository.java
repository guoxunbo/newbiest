package com.newbiest.mms.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.DocumentMLot;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DocumentMLotRepository extends IRepository<DocumentMLot, String> {

    List<DocumentMLot> findByDocumentId(String documentId);

    List<DocumentMLot> findByStatus(String status);
}
