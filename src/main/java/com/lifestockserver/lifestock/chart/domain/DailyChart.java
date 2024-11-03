package com.lifestockserver.lifestock.chart.domain;

import com.lifestockserver.lifestock.common.domain.Base;
import com.lifestockserver.lifestock.company.domain.Company;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "daily_chart")
public class DailyChart extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    private LocalDate date;
    private Long open;
    private Long high;
    private Long low;
    private Long close;

    @Builder.Default
    private int todoCount = 0;
    @Builder.Default
    private int completedCount = 0;

    @Builder.Default
    private boolean dailyCompleted = false;
    private int weeklyCompletedCount;
    @Builder.Default
    private int consecutiveCompletedCount = 0;
}
