package ch.zhaw.freelancer4u.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class Mail {
    private String to;
    private String subject;
    private String message;
}
