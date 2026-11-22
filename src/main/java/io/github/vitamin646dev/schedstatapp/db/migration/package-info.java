/**
 * Flyway migration scripts live in the matching resources directory.
 * This file makes the package real so that module-info can open it
 * (Flyway scans for migrations through the class loader).
 */
package io.github.vitamin646dev.schedstatapp.db.migration;
