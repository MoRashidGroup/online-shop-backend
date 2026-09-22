package com.morashid.OnlineShop.user.entity;

/**
 * Role enum - inawakilisha aina za watumiaji kwenye mfumo wetu.
 * 
 * Enum ni special class yenye set ya constants zilizo fixed.
 * Inatumika sana kwa values ambazo hazibadiliki kama roles, status, n.k.
 * 
 * Kwa nini enum na si String?
 *   - Type-safe: Compiler inakagua kwamba unaweka value sahihi tu
 *   - Refactoring rahisi: Ukibadilisha jina la constant, IDE ina-update zote
 *   - Inaepusha typo errors: "ADMIN" vs "admin" vs "Admin"
 */
public enum Role {
    ADMIN,
    SELLER,
    BUYER
}