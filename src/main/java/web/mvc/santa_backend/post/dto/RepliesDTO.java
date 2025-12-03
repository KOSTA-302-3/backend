package web.mvc.santa_backend.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import web.mvc.santa_backend.post.entity.Replies;

@Data
@Getter
@Setter
@AllArgsConstructor
public class RepliesDTO {
    private Replies replies;

}
