package web.mvc.santa_backend.post.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
public class FeedBakcs {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long feedbakcId;

    @Column(nullable = false)
    private Long userId;

    @ManyToOne
            @JoinColumn(name = "post_id")
    private Posts posts;

    @Column(nullable = false)
    private Long level;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createAt;


}
