package com.newbiest.base.repository.custom;

import com.newbiest.base.model.NBBase;
import com.newbiest.base.repository.custom.impl.AbstractRepositoryImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * Created by guoxunbo on 2018/7/17.
 */
public class RepositoryFactoryBean<R extends JpaRepository<T, I>, T extends NBBase, I extends Serializable>
        extends JpaRepositoryFactoryBean<R, T, I> {

    public RepositoryFactoryBean(Class<? extends R> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        return new RepositoryFactory(entityManager);
    }

    private class RepositoryFactory extends JpaRepositoryFactory {

        private final EntityManager em;

        public RepositoryFactory(EntityManager entityManager) {
            super(entityManager);
            this.em = entityManager;
        }

        @Override
        protected Object getTargetRepository(RepositoryInformation information) {
            return new AbstractRepositoryImpl<T, I>((Class<T>) information.getDomainType(), em);
        }

        @Override
        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
            return AbstractRepositoryImpl.class;
        }
    }
}
