package web.mvc.santa_backend.post.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Posts {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private   Long postId;
    @Column(nullable = false)
    private  Long createUserId;

    @CreatedDate
    @Column(nullable = false)
    private  LocalDateTime createAt;
    @Column(nullable = true, columnDefinition = "text")
    private String content;

    @Column(nullable = false)
            @ColumnDefault("0")
    private Long likeCount;
    @Column(nullable = false)
    @ColumnDefault("0")
    private   Long postLevel;
    @Column(nullable = false)
    @ColumnDefault("false")
    private   boolean contentVisible;

    @OneToMany(mappedBy = "posts", fetch = FetchType.LAZY)
    private List<HashTags> hashTags = new ArrayList<>();

    @OneToMany(mappedBy = "posts", fetch = FetchType.LAZY)
    private List<ImageSources> imageSources = new ArrayList<>();


    public void increaseLikeCount(){
        this.likeCount++;
    }

    public void decreaseLikeCount(){
        this.likeCount--;
    }



}
