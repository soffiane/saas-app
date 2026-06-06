package com.boudissa.saasapp.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

/**
 * active le filtre Hibernate avant chaque requete jouée dans une classe appelée dans le repertoire repositories
 *
 * ca ajoute aux requetes une clause WHERE tenant_id = :tenantId
 *
 * Alternative :
 * On pourrait aussi utiliser un HandlerInterceptor ou un @EventListener
 */
@Component
@Aspect
public class TenantHibernateFilter {

    @PersistenceContext
    private EntityManager entityManager;

    @Before("execution(* com.boudissa.saasapp.repositories.*.*(..))")
    public void activateTenantFilter() {
        final String tenantId = TenantContext.getCurrentTenant();
        if (tenantId != null) {
            //unwrap permet de recuperer le session de hibernate
            //enableFilter permet d'activer le filtre
            //setParameter permet de parametrer le filtre
            entityManager.unwrap(Session.class).enableFilter("tenantFilter").setParameter("tenantId", tenantId);
        }
    }
}
