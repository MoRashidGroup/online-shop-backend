package com.morashid.OnlineShop.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * User entity - inawakilisha table "users" kwenye database.
 * 
 * Kila instance ya class hii = row moja kwenye table "users".
 * Kila field = column moja.
 */
@Entity
@Table(name = "users")  // "user" ni reserved word kwenye MySQL, kwa hivyo tunatumia "users"
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /**
     * Primary Key - ID ya kipekee ya kila user.
     * 
     * @Id              : Inaashiria hii ni primary key
     * @GeneratedValue  : Inaambia Hibernate itengeneze value automatically
     *   - strategy = IDENTITY : Inatumia AUTO_INCREMENT ya MySQL
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Jina kamili la user.
     * 
     * @Column inaweka:
     *   - name       : Jina la column kwenye database
     *   - nullable   : Hawezi kuwa null
     *   - length     : Max characters
     */
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    /**
     * Email - inatumika kama username kwa login.
     * unique = true → Hibernate inaunda UNIQUE constraint.
     */
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    /**
     * Password - itahifadhiwa ikiwa BCrypt-hashed (tuta-implement Phase 5).
     * Kwa sasa inahifadhi plain text (hatuta-register bado).
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * Namba ya simu.
     */
    @Column(name = "phone_no", length = 20)
    private String phoneNo;

    /**
     * Role ya user (ADMIN, SELLER, BUYER).
     * 
     * @Enumerated(EnumType.STRING) : Inahifadhi jina la enum kama STRING kwenye DB
     *   - EnumType.STRING  : Inahifadhi "ADMIN", "SELLER", "BUYER" (TUNATUMIA HII)
     *   - EnumType.ORDINAL : Inahifadhi 0, 1, 2 (HATUTUMII - hatari ukibadilisha order)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    /**
     * Hali ya account - kama ime-activate au la.
     * Default = true (tuta-set kwenye service).
     */
    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    /**
     * Muda wa kuunda account.
     * 
     * @CreationTimestamp : Hibernate inaweka timestamp automatically wakati
     *                      entity inasavewa mara ya kwanza.
     * updatable = false  : Haiwezi kubadilishwa baada ya kuundwa.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}