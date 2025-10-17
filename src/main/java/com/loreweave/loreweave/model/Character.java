/// ==========================================
/// File Name:    Character.java
/// Created By:   Chris Ennis
/// Created On:   2025-09-15
/// Purpose:      JPA file that creates the Character entity
/// Update History:
/// Updated by:  Wyatt Bechtle
/// Updated Notes: Added getters and setters for lorePoints
/// ==========================================

package com.loreweave.loreweave.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@jakarta.persistence.Table(name = "`character`")
public class Character {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private int lorePoints;


    @OneToOne
    @JoinColumn(name = "user_id") // foreign key needs table
    @JsonBackReference
    private User user;

    protected Character() {}

    public Character(String name, String description, int lorePoints, User user) {
        this.name = name;
        this.description = description;
        this.lorePoints = lorePoints;
        this.user = user;
    }
    // Getters and Setters
    public int getLorePoints() { return lorePoints; }
    public void setLorePoints(int lorePoints) { this.lorePoints = lorePoints; }

    @Override
    public String toString() {
        return String.format(
                "Character[id=%d, name='%s', description='%s', lorePoints=%d]",
                id, name, description, lorePoints);
    }
}