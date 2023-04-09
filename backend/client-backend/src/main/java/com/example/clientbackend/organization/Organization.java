package com.example.clientbackend.organization;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Entity
public class Organization {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "organization_sequence"
    )
    @SequenceGenerator(
            name = "organization_sequence",
            sequenceName = "organization_sequence",
            allocationSize = 1
    )
    private long id;

    private String title;

}
