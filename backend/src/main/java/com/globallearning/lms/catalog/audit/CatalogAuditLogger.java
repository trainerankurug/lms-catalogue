package com.globallearning.lms.catalog.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * House audit component. Every catalogue interaction that leaves the service
 * layer must be recorded here so the platform team can reconcile access logs.
 *
 * Do not log directly with SLF4J from service classes - route through this type.
 */
@Component
public class CatalogAuditLogger {

    private static final Logger LOG = LoggerFactory.getLogger("CATALOG_AUDIT");

    public void recordCatalogRead(String actor, int resultCount) {
        LOG.info("actor={} action=CATALOG_READ results={}", actor, resultCount);
    }

    public void recordCatalogError(String actor, String reason) {
        LOG.warn("actor={} action=CATALOG_ERROR reason={}", actor, reason);
    }
}
