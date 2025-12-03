package web.mvc.santa_backend.user.dto;

import jakarta.persistence.Column;

public class BadgeDTO {
    private Long badgeId;
    private String name;
    private String description;
    private String imageUrl;
    private int price;
}
