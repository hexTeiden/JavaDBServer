package com.marie.dbserver.model;

/**
 * Operator Model - repräsentiert eine Zeile aus der Operators-Tabelle.
 *
 * In Express/TS wäre das so etwas wie:
 *   interface Operator { id: number; name: string; side: string; ... }
 *
 * Java Record = immutable Data Class. Kurz und schmerzlos.
 */
public record Hotel(
        int id,
        int noRooms,
        int noBeds,
        String category,
        String name,
        String owner,
        String contact,
        String address,
        String city,
        String cityCode,
        String phone
) {}
