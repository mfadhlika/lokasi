package com.fadhlika.lokasi.dto.owntracks;

public record StatusIos(
        String altimeterAuthorizationStatus,
        Boolean altimeterIsRelativeAltitudeAvailable,
        String backgroundRefreshStatus,
        String deviceIdentifierForVendor,
        String deviceModel,
        String deviceSystemName,
        String deviceSystemVersion,
        String deviceUserInterfaceIdiom,
        String locale,
        Boolean localeUsesMetricSystem,
        String locationManagerAuthorizationStatus,
        String motionActivityManagerAuthorizationStatus,
        Boolean motionActivityManagerIsActivityAvailable,
        Boolean pedometerIsDistanceAvailable,
        Boolean pedometerIsFloorCountingAvailable,
        Boolean pedometerIsStepCountingAvailable,
        String version) {

}
