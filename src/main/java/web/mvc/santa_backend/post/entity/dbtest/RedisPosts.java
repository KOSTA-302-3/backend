package web.mvc.santa_backend.post.entity.dbtest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedisPosts {
    public Long job_id;
    public List<String> image_urls;
    public String content;


}
