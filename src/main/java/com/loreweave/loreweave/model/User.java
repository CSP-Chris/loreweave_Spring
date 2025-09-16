/// ==========================================
/// File Name:    User.java
/// Created By:   Jamie Coker
/// Created On:   2025-09-15
/// Purpose:      Entity representing application users
/// Updated By:
/// Updated By:
/// ==========================================
package com.loreweave.loreweave.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "`user`")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class User {

    @Id
    private Integer id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String username;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

