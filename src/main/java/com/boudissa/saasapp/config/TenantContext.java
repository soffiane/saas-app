package com.boudissa.saasapp.config;

/**
 * TenantContext
 *
 * Permet de stocker le tenant_id dans un thread local
 * Permet de recuperer le tenant_id dans un thread local
 * Permet de supprimer le tenant_id dans un thread local
 *
 * Chaque requete HTTP a un thread local different
 * Le ThreadLocal garantit que le tenant_id est disponible dans toute la requete et isolé par thread
 *
 * Flux:
 * 1. TenantFilter intercepte la requete HTTP et ajoute le tenant_id dans le thread local
 * 2. TenantContext.setCurrentTenant()
 * 3. Le code metier accede au tenant via TenantContext.getCurrentTenant()
 * 4. TenantContext.clear() permet de supprimer le tenant_id dans le thread local
 *
 */
public class TenantContext {

    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    public static void setCurrentTenant(final String tenant){
        CURRENT_TENANT.set(tenant);
    }

    public static String getCurrentTenant(){
        return CURRENT_TENANT.get();
    }

    public static void clear(){
        CURRENT_TENANT.remove();
    }
}
