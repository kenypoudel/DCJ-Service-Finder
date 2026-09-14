package au.nsw.servicefinder.model;

public record ServiceRecord(
        Integer id,
        String title,
        String category,
        String date,
        String hours,
        String url,
        String description
) {
}