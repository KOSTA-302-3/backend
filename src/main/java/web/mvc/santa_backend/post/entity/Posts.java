package web.mvc.santa_backend.post.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.Date;


@Entity
@Getter
@Setter
public class Posts {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private   Long postId;
    @Column(nullable = false)
    private  Long createUserId;

    @CreatedDate
    @Column(nullable = false)
    private  LocalDateTime create_at;

    @Column(nullable = false)
            @ColumnDefault("0")
    private Long likeCount;
    @Column(nullable = false)
    @ColumnDefault("0")
    private   Long postLevel;
    @Column(nullable = false)
    @ColumnDefault("false")
    private   boolean contentVisible;

}
