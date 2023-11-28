package com.chance.auth.entity;

import com.chance.auth.enums.EnumRole;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Getter
@Document(collection = "roles")
public class Role {
    @Id
    private String id;

    private EnumRole name;
}
