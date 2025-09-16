package entity;

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
public class Character {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;
    private int lorePoints;


    @OneToOne
    @JoinColumn(name = "player_id") // foreign key needs table
    @JsonBackReference
    public Player player;

    protected Character() {}

    public Character(String name, String description, int lorePoints, Player player) {
        this.name = name;
        this.description = description;
        this.lorePoints = lorePoints;
        this.player = player;
    }

    @Override
    public String toString() {
        return String.format(
                "Character[id=%d, name='%s', description='%s', lorePoints=%d]",
                id, name, description, lorePoints);
    }
}