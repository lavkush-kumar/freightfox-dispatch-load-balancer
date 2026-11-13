package com.example.dispatch.util;

/** Calculates great-circle distance between two WGS84 coordinates. */
public final class DistanceCalculator {
    private static final double EARTH_RADIUS_KM = 6371.0088;
    private DistanceCalculator() {}

    public static double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        validateCoordinate(lat1, lon1);
        validateCoordinate(lat2, lon2);
        double phi1 = Math.toRadians(lat1), phi2 = Math.toRadians(lat2);
        double deltaPhi = Math.toRadians(lat2 - lat1), deltaLambda = Math.toRadians(lon2 - lon1);
        double a = Math.sin(deltaPhi / 2) * Math.sin(deltaPhi / 2)
                + Math.cos(phi1) * Math.cos(phi2) * Math.sin(deltaLambda / 2) * Math.sin(deltaLambda / 2);
        a = Math.max(0.0, Math.min(1.0, a)); // guard against floating-point drift at antipodal points
        return 2 * EARTH_RADIUS_KM * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
    private static void validateCoordinate(double latitude, double longitude) {
        if (!Double.isFinite(latitude) || !Double.isFinite(longitude) || latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180)
            throw new IllegalArgumentException("Coordinates must be finite; latitude [-90,90], longitude [-180,180].");
    }
}
