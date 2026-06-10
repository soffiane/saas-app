package com.boudissa.saasapp.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;

/**
 * active le filtre Hibernate avant chaque requete jouée dans une classe appelée dans le repertoire repositories
 * <p>
 * ca ajoute aux requetes une clause WHERE tenant_id = :tenantId
 * <p>
 * Alternative :
 * On pourrait aussi utiliser un HandlerInterceptor ou un @EventListener
 */
// Désactivé: @Component et @Aspect retirés pour que l'aspect ne soit plus appliqué
// (le filtre Hibernate "tenantFilter" est aussi commenté dans AbstractEntity)
//@Component
//@Aspect
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
