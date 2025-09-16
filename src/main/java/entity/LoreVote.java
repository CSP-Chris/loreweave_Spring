package entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class LoreVote {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @JsonBackReference
    private StoryPart storyPart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player voter;

    @Enumerated(EnumType.STRING)
    private VoteType voteType;

    private LocalDateTime createdAt;

    protected LoreVote() {}

//    public LoreVote(StoryPart storyPart, Player voter, VoteType voteType) {
//        this.storyPart = storyPart;
//        this.voter = voter;
//        this.voteType = voteType;
//        this.createdAt = LocalDateTime.now();
//    }

    public enum VoteType {
        POSITIVE, NEGATIVE
    }
}
