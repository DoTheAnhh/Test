package com.example.test_project.common;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SearchParams {
    private long id;
    private String code;
    private String name;
}
