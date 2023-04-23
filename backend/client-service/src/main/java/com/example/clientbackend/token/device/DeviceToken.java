package com.example.clientbackend.token.device;

import com.example.clientbackend.appuser.model.AppUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DeviceToken {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "device_token_sequence"
    )
    @SequenceGenerator(
            name = "device_token_sequence",
            sequenceName = "device_token_sequence",
            allocationSize = 1
    )
    private long id;

    @Column(nullable = false)
    private String token;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(nullable = false)
    private AppUser appUser;
}
