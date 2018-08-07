package com.newbiest.common.idgenerator.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.common.idgenerator.model.Sequence;
import com.newbiest.common.idgenerator.repository.custom.SequenceRepositoryCustom;
import org.springframework.stereotype.Repository;

/**
 * Created by guoxunbo on 2018/8/6.
 */
@Repository
public interface SequenceRepository extends IRepository<Sequence, Long>, SequenceRepositoryCustom {

    Sequence getByNameAndGeneratorRrn(String name, long generatorRrn);

}
