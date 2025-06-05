package com.nutritrack.nutritrackbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "daily_logs", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "log_date"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @Column(name = "log_date", nullable = false)
    private LocalDate date;

    @OneToMany(mappedBy = "dailyLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DailyLogEntry> entries = new HashSet<>();

    @Column(name = "fasting_hours")
    private Integer fastingHours;
}
