package com.classspace_backend.demo.dto;

import java.util.List;

public class FeedbackResponseDTO {
    private double averageStars;
    private long understoodCount;
    private long notUnderstoodCount;
    private List<FeedbackDTO> items;

    public FeedbackResponseDTO(
            double averageStars,
            long understoodCount,
            long notUnderstoodCount,
            List<FeedbackDTO> items
    ) {
        this.averageStars = averageStars;
        this.understoodCount = understoodCount;
        this.notUnderstoodCount = notUnderstoodCount;
        this.items = items;
    }

    public double getAverageStars() { return averageStars; }
    public long getUnderstoodCount() { return understoodCount; }
    public long getNotUnderstoodCount() { return notUnderstoodCount; }
    public List<FeedbackDTO> getItems() { return items; }
}

